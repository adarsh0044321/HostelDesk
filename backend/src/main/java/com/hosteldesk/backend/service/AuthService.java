package com.hosteldesk.backend.service;

import com.hosteldesk.backend.config.AppProperties;
import com.hosteldesk.backend.dto.*;
import com.hosteldesk.backend.entity.*;
import com.hosteldesk.backend.exception.BadRequestException;
import com.hosteldesk.backend.exception.ForbiddenException;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.CampusRepository;
import com.hosteldesk.backend.repository.HostelRepository;
import com.hosteldesk.backend.repository.InstituteRepository;
import com.hosteldesk.backend.repository.PasswordResetRequestRepository;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.JwtTokenProvider;
import com.hosteldesk.backend.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final HostelRepository hostelRepository;
    private final InstituteRepository instituteRepository;
    private final CampusRepository campusRepository;
    private final com.hosteldesk.backend.repository.DepartmentRepository departmentRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AppProperties appProperties;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       HostelRepository hostelRepository,
                       InstituteRepository instituteRepository,
                       CampusRepository campusRepository,
                       com.hosteldesk.backend.repository.DepartmentRepository departmentRepository,
                       PasswordResetRequestRepository passwordResetRequestRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AppProperties appProperties) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.hostelRepository = hostelRepository;
        this.instituteRepository = instituteRepository;
        this.campusRepository = campusRepository;
        this.departmentRepository = departmentRepository;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String identifier = request.getEmailOrInstitutionalId();
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new BadRequestException("Email, Student ID, or Staff ID is required");
        }

        String instituteCode = request.getInstituteCode();
        User user;

        if (instituteCode != null && !instituteCode.trim().isEmpty()) {
            Institute institute = instituteRepository.findByCode(instituteCode.trim())
                    .orElseThrow(() -> new BadRequestException("Institute not found with ID: " + instituteCode));

            if (!"ACTIVE".equalsIgnoreCase(institute.getStatus())) {
                throw new ForbiddenException("Institute account is " + institute.getStatus() + ". Please contact administrator.");
            }

            user = userRepository.findByInstituteCodeAndInstitutionalId(institute.getCode(), identifier)
                    .or(() -> userRepository.findByInstituteCodeAndEmail(institute.getCode(), identifier))
                    .orElseThrow(() -> new BadRequestException("Invalid credentials or user does not belong to institute " + instituteCode));
        } else {
            // Fallback for single-tenant / existing legacy calls
            user = userRepository.findByEmailOrInstitutionalId(identifier, identifier)
                    .orElseThrow(() -> new BadRequestException("Invalid email/institutional ID or password"));
        }

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenException("Account is " + user.getStatus() + ". Please contact administrator.");
        }

        // Strict role boundary enforcement
        if ("ADMIN".equalsIgnoreCase(request.getTargetApp()) && user.getRole() == Role.STUDENT) {
            throw new ForbiddenException("Access Denied: Student accounts are not permitted to access the HostelDesk Admin application.");
        }
        if ("STUDENT".equalsIgnoreCase(request.getTargetApp()) && user.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Please use the HostelDesk Admin application to log in with administrative or staff credentials.");
        }
        if ("WARDEN_PORTAL".equalsIgnoreCase(request.getTargetApp()) &&
                (user.getRole() == Role.INSTITUTE_ADMIN || user.getRole() == Role.SUPER_ADMIN)) {
            throw new ForbiddenException("Access Denied: Institute Administrators must sign in exclusively via the Executive Portal. Tap 'Executive Portal' below.");
        }
        if ("EXECUTIVE_PORTAL".equalsIgnoreCase(request.getTargetApp())) {
            if (user.getRole() != Role.INSTITUTE_ADMIN && user.getRole() != Role.SUPER_ADMIN && user.getRole() != Role.ADMIN) {
                throw new ForbiddenException("Access Denied: The Executive Portal requires Institute Administrator credentials.");
            }
            String reqPasscode = request.getSecurityPasscode();
            if (reqPasscode == null || reqPasscode.trim().isEmpty()) {
                throw new BadRequestException("Executive Security Passcode / PIN is required for Executive Portal login.");
            }
            String expected = (user.getInstitute() != null && user.getInstitute().getSecurityPasscode() != null)
                    ? user.getInstitute().getSecurityPasscode().trim()
                    : "112233";
            if (!expected.equalsIgnoreCase(reqPasscode.trim())) {
                throw new ForbiddenException("Invalid Executive Security Passcode / Secret PIN. Access Denied.");
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        user.setLastLoginAt(ZonedDateTime.now());
        userRepository.save(user);

        String jwt = tokenProvider.generateToken(authentication);
        return new LoginResponse(jwt, appProperties.getJwt().getExpirationMs() / 1000, UserDto.fromEntity(user));
    }

    @Transactional
    public LoginResponse registerInstitute(RegisterInstituteRequest request) {
        String code = request.getInstituteCode();
        if (code == null || code.trim().isEmpty()) {
            code = "INST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } else {
            code = code.trim().toUpperCase();
        }

        if (instituteRepository.existsByCode(code)) {
            throw new BadRequestException("Institute ID '" + code + "' is already registered. Please choose another.");
        }
        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new BadRequestException("Administrator email is already registered: " + request.getAdminEmail());
        }

        String passcode = request.getSecurityPasscode();
        if (passcode == null || passcode.trim().isEmpty()) {
            passcode = String.format("%06d", (int)(Math.random() * 900000) + 100000);
        } else {
            passcode = passcode.trim();
        }

        Institute institute = new Institute();
        institute.setCode(code);
        institute.setName(request.getInstituteName());
        institute.setType(request.getInstituteType() != null ? request.getInstituteType() : "UNIVERSITY");
        institute.setEmail(request.getInstituteEmail());
        institute.setContactNumber(request.getContactNumber() != null && !request.getContactNumber().trim().isEmpty() ? request.getContactNumber().trim() : "+91 11 2766 7722");
        institute.setStatus("ACTIVE");
        institute.setSecurityPasscode(passcode);
        Institute savedInstitute = instituteRepository.save(institute);

        // Create default campus
        Campus campus = new Campus();
        campus.setInstitute(savedInstitute);
        campus.setCode("MAIN");
        campus.setName("Main Campus");
        campusRepository.save(campus);

        // Create default hostel for campus
        Hostel defaultHostel = new Hostel(null, savedInstitute, campus, "Main Residence Hall", "Campus Wing A", "Primary residence hostel", true);
        hostelRepository.save(defaultHostel);

        // Create default maintenance departments for newly registered institute
        departmentRepository.save(new Department(null, "PLUMBING", "Plumbing Crew", "Plumbing and water fixtures", true));
        departmentRepository.save(new Department(null, "ELECTRICAL", "Electrical Facilities", "Power, lights and electrical safety", true));
        departmentRepository.save(new Department(null, "GENERAL", "Facilities & Maintenance", "General repairs and carpentry", true));

        // Create Institute Administrator with guaranteed unique institutionalId
        String adminInstId = request.getAdminId();
        if (adminInstId == null || adminInstId.trim().isEmpty() || "ADM-001".equalsIgnoreCase(adminInstId)) {
            adminInstId = code + "-ADMIN";
        }
        if (userRepository.existsByInstitutionalId(adminInstId)) {
            adminInstId = code + "-ADMIN-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }

        User admin = new User();
        admin.setFullName(request.getAdminName());
        admin.setEmail(request.getAdminEmail());
        admin.setInstitutionalId(adminInstId);
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.INSTITUTE_ADMIN);
        admin.setStatus(AccountStatus.ACTIVE);
        admin.setInstitute(savedInstitute);
        admin.setCampus(campus);
        admin.setHostel(defaultHostel);
        admin.setNeedsPasswordChange(false);
        try {
            userRepository.save(admin);
        } catch (Exception ex) {
            throw new BadRequestException("Registration conflict: could not provision administrator account. " + ex.getMessage());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(admin.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return new LoginResponse(jwt, appProperties.getJwt().getExpirationMs() / 1000, UserDto.fromEntity(admin));
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        String instituteCode = request.getInstituteCode();
        if (instituteCode == null || instituteCode.trim().isEmpty()) {
            throw new BadRequestException("Institute ID is required for password reset");
        }

        Institute institute = instituteRepository.findByCode(instituteCode.trim())
                .orElseThrow(() -> new BadRequestException("Institute not found: " + instituteCode));

        User user = userRepository.findByInstituteCodeAndInstitutionalId(institute.getCode(), request.getIdentifier())
                .or(() -> userRepository.findByInstituteCodeAndEmail(institute.getCode(), request.getIdentifier()))
                .orElseThrow(() -> new BadRequestException("User not found in institute: " + request.getIdentifier()));

        String phone = request.getContactPhone();
        if (phone == null || phone.trim().isEmpty()) {
            phone = user.getPhone();
        }

        PasswordResetRequest resetRequest = new PasswordResetRequest(
                institute,
                user,
                user.getRole().name(),
                request.getReason() != null ? request.getReason() : "Account recovery requested"
        );
        resetRequest.setContactPhone(phone != null ? phone.trim() : null);
        resetRequest.setAssignedHandler("Institute IT Helpdesk");
        resetRequest.setAssignedDepartment("IT_SUPPORT");
        passwordResetRequestRepository.save(resetRequest);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (request.getOldPassword() != null && !request.getOldPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
                throw new BadRequestException("Current password does not match");
            }
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setNeedsPasswordChange(false);
        userRepository.save(user);
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }
        if (userRepository.existsByInstitutionalId(request.getInstitutionalId())) {
            throw new BadRequestException("Institutional ID is already registered: " + request.getInstitutionalId());
        }

        Hostel hostel = null;
        if (request.getHostelId() != null) {
            hostel = hostelRepository.findById(request.getHostelId()).orElse(null);
        }

        Institute defaultInst = instituteRepository.findByCode("NCH-001").orElse(null);

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setInstitutionalId(request.getInstitutionalId());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.STUDENT);
        user.setStatus(AccountStatus.ACTIVE);
        user.setHostel(hostel);
        user.setInstitute(defaultInst != null ? defaultInst : (hostel != null ? hostel.getInstitute() : null));
        user.setRoomNumber(request.getRoomNumber());

        User saved = userRepository.save(user);
        return UserDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));
        return UserDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public InstitutePublicDto getInstitutePublicInfo(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BadRequestException("Institute code is required");
        }
        Institute institute = instituteRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with code: " + code));

        String campusName = campusRepository.findByInstituteId(institute.getId()).stream()
                .map(Campus::getName)
                .findFirst()
                .orElse("Main Campus");

        return new InstitutePublicDto(
                institute.getCode(),
                institute.getName(),
                campusName,
                institute.getContactNumber() != null ? institute.getContactNumber() : "+91 11 2766 7722",
                institute.getEmail(),
                institute.getStatus()
        );
    }

    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getRoomNumber() != null) {
            user.setRoomNumber(request.getRoomNumber().trim());
        }

        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }
}


