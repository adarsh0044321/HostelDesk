package com.hosteldesk.backend.config;

import com.hosteldesk.backend.entity.*;
import com.hosteldesk.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final InstituteRepository instituteRepository;
    private final CampusRepository campusRepository;
    private final HostelRepository hostelRepository;
    private final BlockRepository blockRepository;
    private final RoomRepository roomRepository;
    private final DepartmentRepository departmentRepository;
    private final RoutingRuleRepository routingRuleRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final IssueAiAnalysisRepository aiAnalysisRepository;
    private final IssueActivityRepository activityRepository;
    private final NotificationRepository notificationRepository;
    private final InfrastructureInsightRepository insightRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-demo-data:true}")
    private boolean seedDemoData;

    public DataInitializer(InstituteRepository instituteRepository,
                           CampusRepository campusRepository,
                           HostelRepository hostelRepository,
                           BlockRepository blockRepository,
                           RoomRepository roomRepository,
                           DepartmentRepository departmentRepository,
                           RoutingRuleRepository routingRuleRepository,
                           UserRepository userRepository,
                           IssueRepository issueRepository,
                           IssueAiAnalysisRepository aiAnalysisRepository,
                           IssueActivityRepository activityRepository,
                           NotificationRepository notificationRepository,
                           InfrastructureInsightRepository insightRepository,
                           PasswordEncoder passwordEncoder) {
        this.instituteRepository = instituteRepository;
        this.campusRepository = campusRepository;
        this.hostelRepository = hostelRepository;
        this.blockRepository = blockRepository;
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
        this.routingRuleRepository = routingRuleRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.activityRepository = activityRepository;
        this.notificationRepository = notificationRepository;
        this.insightRepository = insightRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and initializing database seed data...");

        // 0. Primary Institute & Campus
        Institute defaultInstitute = instituteRepository.findByCode("NCH-001")
                .orElseGet(() -> {
                    Institute inst = new Institute(
                            null, "NCH-001", "North Campus Housing Institute",
                            "UNIVERSITY", "admin@campus.edu", "+91 11 2766 7722", "ACTIVE"
                    );
                    return instituteRepository.save(inst);
                });
        if (defaultInstitute.getContactNumber() == null || defaultInstitute.getContactNumber().contains("1-800")) {
            defaultInstitute.setContactNumber("+91 11 2766 7722");
            instituteRepository.save(defaultInstitute);
        }
        if (defaultInstitute.getSecurityPasscode() == null || defaultInstitute.getSecurityPasscode().isEmpty()) {
            defaultInstitute.setSecurityPasscode("112233");
            instituteRepository.save(defaultInstitute);
        }



        Campus northCampus = campusRepository.findByInstituteId(defaultInstitute.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Campus c = new Campus(null, defaultInstitute, "NC", "North Campus");
                    return campusRepository.save(c);
                });

        // 1. Hostels
        Hostel tagoreHall = hostelRepository.findByName("Tagore Hall")
                .orElseGet(() -> {
                    Hostel h = new Hostel(null, defaultInstitute, northCampus, "Tagore Hall", "North Campus", "Primary undergraduate residence", true);
                    return hostelRepository.save(h);
                });
        if (tagoreHall.getInstitute() == null) {
            tagoreHall.setInstitute(defaultInstitute);
            tagoreHall.setCampus(northCampus);
            hostelRepository.save(tagoreHall);
        }

        Hostel shastriHall = hostelRepository.findByName("Shastri Hall")
                .orElseGet(() -> {
                    Hostel h = new Hostel(null, defaultInstitute, northCampus, "Shastri Hall", "North Campus", "Postgraduate residence", true);
                    return hostelRepository.save(h);
                });
        if (shastriHall.getInstitute() == null) {
            shastriHall.setInstitute(defaultInstitute);
            shastriHall.setCampus(northCampus);
            hostelRepository.save(shastriHall);
        }

        // 2. Blocks
        Block blockA = blockRepository.findByHostelIdAndName(tagoreHall.getId(), "Block A")
                .orElseGet(() -> blockRepository.save(new Block(null, tagoreHall, "Block A")));
        Block blockB = blockRepository.findByHostelIdAndName(tagoreHall.getId(), "Block B")
                .orElseGet(() -> blockRepository.save(new Block(null, tagoreHall, "Block B")));

        // 3. Rooms
        roomRepository.findByBlockIdAndRoomNumber(blockB.getId(), "204")
                .orElseGet(() -> roomRepository.save(new Room(null, blockB, "204", 2)));

        // 4. Departments
        Department plumbing = getOrCreateDepartment("PLUMBING", "Plumbing & Water Supply", "Water leaks and sanitation", defaultInstitute);
        Department electrical = getOrCreateDepartment("ELECTRICAL", "Electrical & Power Operations", "Power and wiring safety", defaultInstitute);
        Department carpentry = getOrCreateDepartment("CARPENTRY", "Carpentry & Furniture", "Locks and furniture", defaultInstitute);
        Department cleaning = getOrCreateDepartment("CLEANING", "Housekeeping & Sanitation", "Corridor and washroom sanitation", defaultInstitute);
        Department internet = getOrCreateDepartment("INTERNET", "IT & Campus Network", "Wi-Fi and LAN connectivity", defaultInstitute);
        Department civil = getOrCreateDepartment("CIVIL", "Civil Infrastructure", "Masonry and dampness", defaultInstitute);
        Department general = getOrCreateDepartment("GENERAL", "General Operations & Warden Desk", "General hostel complaints", defaultInstitute);

        // 5. Routing Rules
        createRoutingRuleIfNotExists("PLUMBING", plumbing, "P2_HIGH", defaultInstitute);
        createRoutingRuleIfNotExists("ELECTRICAL", electrical, "P1_URGENT", defaultInstitute);
        createRoutingRuleIfNotExists("CARPENTRY", carpentry, "P3_MEDIUM", defaultInstitute);
        createRoutingRuleIfNotExists("CLEANING", cleaning, "P3_MEDIUM", defaultInstitute);
        createRoutingRuleIfNotExists("INTERNET", internet, "P3_MEDIUM", defaultInstitute);
        createRoutingRuleIfNotExists("CIVIL", civil, "P3_MEDIUM", defaultInstitute);
        createRoutingRuleIfNotExists("GENERAL", general, "P3_MEDIUM", defaultInstitute);

        // 6. Users
        User student = userRepository.findByEmail("aarav@campus.edu")
                .orElseGet(() -> {
                    User u = new User(
                            null, "Aarav Patel", "aarav@campus.edu", "+91 98765 43210", "ST-8819",
                            passwordEncoder.encode("student123"), Role.STUDENT, AccountStatus.ACTIVE,
                            tagoreHall, null, "204"
                    );
                    u.setInstitute(defaultInstitute);
                    u.setCampus(northCampus);
                    return userRepository.save(u);
                });
        if (student.getInstitute() == null) {
            student.setInstitute(defaultInstitute);
            userRepository.save(student);
        }

        User warden = userRepository.findByEmail("warden.sharma@campus.edu")
                .orElseGet(() -> {
                    User u = new User(
                            null, "Warden R. Sharma", "warden.sharma@campus.edu", "+91 98765 00001", "WR-1001",
                            passwordEncoder.encode("warden123"), Role.WARDEN, AccountStatus.ACTIVE,
                            tagoreHall, null, null
                    );
                    u.setInstitute(defaultInstitute);
                    u.setCampus(northCampus);
                    return userRepository.save(u);
                });
        if (warden.getInstitute() == null) {
            warden.setInstitute(defaultInstitute);
            userRepository.save(warden);
        }

        User staff = userRepository.findByEmail("suresh@campus.edu")
                .orElseGet(() -> {
                    User u = new User(
                            null, "Suresh Kumar", "suresh@campus.edu", "+91 98765 11112", "STF-201",
                            passwordEncoder.encode("staff123"), Role.STAFF, AccountStatus.ACTIVE,
                            tagoreHall, plumbing, null
                    );
                    u.setInstitute(defaultInstitute);
                    u.setCampus(northCampus);
                    return userRepository.save(u);
                });
        if (staff.getInstitute() == null) {
            staff.setInstitute(defaultInstitute);
            userRepository.save(staff);
        }

        User admin = userRepository.findByEmail("admin@campus.edu")
                .orElseGet(() -> {
                    User u = new User(
                            null, "System Administrator", "admin@campus.edu", "+91 98765 99999", "ADM-001",
                            passwordEncoder.encode("admin123"), Role.INSTITUTE_ADMIN, AccountStatus.ACTIVE,
                            null, null, null
                    );
                    u.setInstitute(defaultInstitute);
                    u.setCampus(northCampus);
                    return userRepository.save(u);
                });
        if (admin.getInstitute() == null) {
            admin.setInstitute(defaultInstitute);
            userRepository.save(admin);
        }

        // 7. Custom JAI Institute & Profile
        Institute jaiInstitute = instituteRepository.findByCode("JAI")
                .orElseGet(() -> {
                    Institute inst = new Institute(
                            null, "JAI", "JAI Campus Institute",
                            "UNIVERSITY", "adminjai@campus.edu", "+91 98765 00001", "ACTIVE"
                    );
                    inst.setSecurityPasscode("998877");
                    return instituteRepository.save(inst);
                });
        if (jaiInstitute.getSecurityPasscode() == null || jaiInstitute.getSecurityPasscode().isEmpty()) {
            jaiInstitute.setSecurityPasscode("998877");
            instituteRepository.save(jaiInstitute);
        }

        // Backfill any remaining institutes with default passcode
        for (Institute inst : instituteRepository.findAll()) {
            if (inst.getSecurityPasscode() == null || inst.getSecurityPasscode().isEmpty()) {
                inst.setSecurityPasscode("112233");
                instituteRepository.save(inst);
            }
        }

        Campus jaiCampus = campusRepository.findByInstituteId(jaiInstitute.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Campus c = new Campus(null, jaiInstitute, "MAIN", "JAI Main Campus");
                    return campusRepository.save(c);
                });

        Hostel jaiHostel = hostelRepository.findByInstituteId(jaiInstitute.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Hostel h = new Hostel(null, jaiInstitute, jaiCampus, "JAI Residence Hall 1", "Main Quad", "Primary campus hostel", true);
                    return hostelRepository.save(h);
                });

        Department jaiPlumbing = getOrCreateDepartment("PLUMBING", "Plumbing & Sanitation", "Water supply and fittings", jaiInstitute);
        Department jaiElectrical = getOrCreateDepartment("ELECTRICAL", "Electrical & Facilities", "Power and fixtures", jaiInstitute);
        Department jaiGeneral = getOrCreateDepartment("GENERAL", "Facilities & Maintenance", "General maintenance operations", jaiInstitute);

        createRoutingRuleIfNotExists("PLUMBING", jaiPlumbing, "P2_HIGH", jaiInstitute);
        createRoutingRuleIfNotExists("ELECTRICAL", jaiElectrical, "P1_URGENT", jaiInstitute);
        createRoutingRuleIfNotExists("GENERAL", jaiGeneral, "P3_MEDIUM", jaiInstitute);

        User adminJai = userRepository.findByEmail("adminjai@campus.edu")
                .or(() -> userRepository.findByInstituteCodeAndInstitutionalId("JAI", "adminjai"))
                .orElseGet(() -> {
                    User u = new User(
                            null, "Admin Jai", "adminjai@campus.edu", "+91 98765 00001", "adminjai",
                            passwordEncoder.encode("adminjai"), Role.INSTITUTE_ADMIN, AccountStatus.ACTIVE,
                            jaiHostel, null, null
                    );
                    u.setInstitute(jaiInstitute);
                    u.setCampus(jaiCampus);
                    u.setNeedsPasswordChange(false);
                    return userRepository.save(u);
                });
        if (adminJai.getInstitute() == null) {
            adminJai.setInstitute(jaiInstitute);
            adminJai.setCampus(jaiCampus);
            userRepository.save(adminJai);
        }

        // Development-only seed accounts (admin / admin, student / student)
        if (seedDemoData) {
            userRepository.findByInstitutionalId("admin")
                    .orElseGet(() -> {
                        User devAdmin = new User();
                        devAdmin.setFullName("Dev Admin");
                        devAdmin.setEmail("admin.dev@campus.edu");
                        devAdmin.setInstitutionalId("admin");
                        devAdmin.setPasswordHash(passwordEncoder.encode("admin"));
                        devAdmin.setRole(Role.INSTITUTE_ADMIN);
                        devAdmin.setStatus(AccountStatus.ACTIVE);
                        devAdmin.setInstitute(defaultInstitute);
                        devAdmin.setCampus(northCampus);
                        return userRepository.save(devAdmin);
                    });

            userRepository.findByInstitutionalId("student")
                    .orElseGet(() -> {
                        User devStudent = new User();
                        devStudent.setFullName("Dev Student");
                        devStudent.setEmail("student.dev@campus.edu");
                        devStudent.setInstitutionalId("student");
                        devStudent.setPasswordHash(passwordEncoder.encode("student"));
                        devStudent.setRole(Role.STUDENT);
                        devStudent.setStatus(AccountStatus.ACTIVE);
                        devStudent.setInstitute(defaultInstitute);
                        devStudent.setCampus(northCampus);
                        devStudent.setHostel(tagoreHall);
                        devStudent.setRoomNumber("204");
                        return userRepository.save(devStudent);
                    });
        }

        // 7. Seed Initial Sample Issues
        if (issueRepository.count() == 0) {
            log.info("Seeding initial reference issues (#HD-1042, #HD-1038, #HD-4819)...");

            Issue issue1 = new Issue();
            issue1.setTicketNumber("HD-1042");
            issue1.setReportedBy(student);
            issue1.setInstitute(defaultInstitute);
            issue1.setCampus(northCampus);
            issue1.setHostel(tagoreHall);
            issue1.setBlockName("Block B");
            issue1.setRoomNumber("204");
            issue1.setCategory("PLUMBING");
            issue1.setTitle("Bathroom ceiling water leakage");
            issue1.setDescription("There is water leaking heavily from the bathroom ceiling near the light fixture. Plaster is damp.");
            issue1.setPriority(IssuePriority.P1_URGENT);
            issue1.setStatus(IssueStatus.AWAITING_VERIFICATION);
            issue1.setAssignedDepartment(plumbing);
            issue1.setAssignedStaff(staff);
            issue1.setTechnicianNotes("Replaced faulty drainage seal on upper floor connection. Tested water flow for 15 minutes, sealed and dry.");
            issue1.setCreatedAt(ZonedDateTime.now().minusHours(3));
            issue1 = issueRepository.save(issue1);

            IssueAiAnalysis ai1 = new IssueAiAnalysis(
                    null, issue1, "PLUMBING", "P1_URGENT", "PLUMBING",
                    "Water leakage detected near electrical fixture, poses potential hazard.",
                    "Water dripping near ceiling light fitting", new BigDecimal("0.940"), false
            );
            aiAnalysisRepository.save(ai1);

            activityRepository.save(new IssueActivity(null, issue1, student, "REPORTED", "Issue #HD-1042 submitted by Aarav Patel"));
            activityRepository.save(new IssueActivity(null, issue1, warden, "ASSIGNED", "Assigned to Plumbing (Tech: Suresh Kumar)"));
            activityRepository.save(new IssueActivity(null, issue1, staff, "IN_PROGRESS", "Tech Suresh Kumar arrived on site and inspected upper floor connection."));
            activityRepository.save(new IssueActivity(null, issue1, staff, "RESOLVED", "Replaced faulty drainage seal on upper floor connection. Tested water flow for 15 minutes, sealed and dry."));

            notificationRepository.save(new Notification(
                    null, student, "Verification Required: HD-1042",
                    "Plumbing repair completed. Please verify if the water leakage in Room 204 has been resolved.",
                    "ISSUE_RESOLVED", issue1, false
            ));
        }

        log.info("Database seed initialization completed successfully.");
    }

    private Department getOrCreateDepartment(String name, String displayName, String description, Institute institute) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> {
                    Department d = new Department(null, name, displayName, description, true);
                    d.setInstitute(institute);
                    return departmentRepository.save(d);
                });
    }

    private void createRoutingRuleIfNotExists(String category, Department department, String defaultPriority, Institute institute) {
        routingRuleRepository.findByCategory(category)
                .orElseGet(() -> {
                    RoutingRule rule = new RoutingRule(null, category, department, defaultPriority, true);
                    rule.setInstitute(institute);
                    return routingRuleRepository.save(rule);
                });
    }
}
