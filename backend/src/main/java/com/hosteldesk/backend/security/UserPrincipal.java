package com.hosteldesk.backend.security;

import com.hosteldesk.backend.entity.AccountStatus;
import com.hosteldesk.backend.entity.Role;
import com.hosteldesk.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String institutionalId;
    private final String password;
    private final Role role;
    private final AccountStatus status;
    private final Long instituteId;
    private final String instituteCode;
    private final Long hostelId;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String fullName, String email, String institutionalId,
                         String password, Role role, AccountStatus status,
                         Long instituteId, String instituteCode, Long hostelId,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.institutionalId = institutionalId;
        this.password = password;
        this.role = role;
        this.status = status;
        this.instituteId = instituteId;
        this.instituteCode = instituteCode;
        this.hostelId = hostelId;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        if (user.getRole() == Role.INSTITUTE_ADMIN || user.getRole() == Role.SUPER_ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_INSTITUTE_ADMIN"));
        } else if (user.getRole() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_INSTITUTE_ADMIN"));
        }

        if (user.getRole() == Role.STAFF || user.getRole() == Role.MAINTENANCE_STAFF) {
            authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));
            authorities.add(new SimpleGrantedAuthority("ROLE_MAINTENANCE_STAFF"));
        }

        Long instId = user.getInstitute() != null ? user.getInstitute().getId() : null;
        String instCode = user.getInstitute() != null ? user.getInstitute().getCode() : null;
        Long hId = user.getHostel() != null ? user.getHostel().getId() : null;

        return new UserPrincipal(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getInstitutionalId(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus(),
                instId,
                instCode,
                hId,
                authorities
        );
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public Long getHostelId() { return hostelId; }
    public String getEmail() { return email; }
    public String getInstitutionalId() { return institutionalId; }
    public Role getRole() { return role; }
    public Long getInstituteId() { return instituteId; }
    public String getInstituteCode() { return instituteCode; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return status == AccountStatus.ACTIVE; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return status == AccountStatus.ACTIVE; }
}
