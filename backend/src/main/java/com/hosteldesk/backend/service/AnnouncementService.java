package com.hosteldesk.backend.service;

import com.hosteldesk.backend.dto.AnnouncementDto;
import com.hosteldesk.backend.dto.CreateAnnouncementRequest;
import com.hosteldesk.backend.entity.*;
import com.hosteldesk.backend.exception.BadRequestException;
import com.hosteldesk.backend.exception.ForbiddenException;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.AnnouncementRepository;
import com.hosteldesk.backend.repository.HostelRepository;
import com.hosteldesk.backend.repository.InstituteRepository;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final InstituteRepository instituteRepository;
    private final HostelRepository hostelRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                               InstituteRepository instituteRepository,
                               HostelRepository hostelRepository,
                               UserRepository userRepository,
                               NotificationService notificationService) {
        this.announcementRepository = announcementRepository;
        this.instituteRepository = instituteRepository;
        this.hostelRepository = hostelRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getStudentAnnouncements(Long instituteId, Long hostelId) {
        if (instituteId == null) {
            return java.util.Collections.emptyList();
        }
        return announcementRepository.findActiveForStudent(instituteId, hostelId, ZonedDateTime.now())
                .stream()
                .map(AnnouncementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getInstituteAnnouncements(Long instituteId) {
        if (instituteId == null) {
            return java.util.Collections.emptyList();
        }
        return announcementRepository.findByInstituteIdOrderByCreatedAtDesc(instituteId)
                .stream()
                .map(AnnouncementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnnouncementDto createAnnouncement(Long instituteId, User author, CreateAnnouncementRequest request) {
        if (instituteId == null) {
            throw new BadRequestException("Institute ID is required to post an announcement");
        }

        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found: " + instituteId));

        Hostel hostel = null;
        if (request.getHostelId() != null) {
            hostel = hostelRepository.findById(request.getHostelId()).orElse(null);
        } else if (author.getRole() == Role.WARDEN && author.getHostel() != null) {
            hostel = author.getHostel();
        }

        ZonedDateTime expiresAt = null;
        if (request.getDurationHours() != null && request.getDurationHours() > 0) {
            expiresAt = ZonedDateTime.now().plusHours(request.getDurationHours());
        }

        String authorRole = author.getRole() == Role.WARDEN ? "WARDEN" : "INSTITUTE_ADMIN";

        Announcement announcement = new Announcement();
        announcement.setInstitute(institute);
        announcement.setHostel(hostel);
        announcement.setAuthor(author);
        announcement.setAuthorName(author.getFullName());
        announcement.setAuthorRole(authorRole);
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent().trim());
        announcement.setPinned(request.getPinned() != null ? request.getPinned() : false);
        announcement.setExpiresAt(expiresAt);

        announcement = announcementRepository.save(announcement);

        // Notify students
        List<User> targetStudents;
        if (hostel != null) {
            targetStudents = userRepository.findByHostelIdAndRole(hostel.getId(), Role.STUDENT);
        } else {
            targetStudents = userRepository.findByInstituteIdAndRole(instituteId, Role.STUDENT);
        }

        String notifPrefix = author.getRole() == Role.WARDEN ? "Hostel Notice: " : "Campus Bulletin: ";
        for (User student : targetStudents) {
            notificationService.createNotification(
                    student,
                    notifPrefix + announcement.getTitle(),
                    announcement.getContent().length() > 120 ? announcement.getContent().substring(0, 117) + "..." : announcement.getContent(),
                    "ANNOUNCEMENT",
                    null
            );
        }

        return AnnouncementDto.fromEntity(announcement);
    }

    @Transactional
    public AnnouncementDto updateAnnouncement(Long id, CreateAnnouncementRequest request, UserPrincipal principal) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id));

        boolean isInstituteAdmin = principal.getRole() == Role.INSTITUTE_ADMIN ||
                principal.getRole() == Role.SUPER_ADMIN ||
                principal.getRole() == Role.ADMIN;

        if (!isInstituteAdmin) {
            // Warden user
            if ("INSTITUTE_ADMIN".equalsIgnoreCase(announcement.getAuthorRole()) || announcement.getHostel() == null) {
                throw new ForbiddenException("Wardens cannot modify institutional announcements posted by Institute Administration.");
            }
            if (announcement.getAuthor() != null && !announcement.getAuthor().getId().equals(principal.getId())) {
                if (principal.getHostelId() == null || announcement.getHostel() == null ||
                        !principal.getHostelId().equals(announcement.getHostel().getId())) {
                    throw new ForbiddenException("You can only modify announcements posted for your hostel.");
                }
            }
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            announcement.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            announcement.setContent(request.getContent().trim());
        }
        if (request.getPinned() != null) {
            announcement.setPinned(request.getPinned());
        }

        if (request.getDurationHours() != null) {
            if (request.getDurationHours() > 0) {
                announcement.setExpiresAt(ZonedDateTime.now().plusHours(request.getDurationHours()));
            } else {
                announcement.setExpiresAt(null);
            }
        }

        announcement = announcementRepository.save(announcement);
        return AnnouncementDto.fromEntity(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id, UserPrincipal principal) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id));

        boolean isInstituteAdmin = principal.getRole() == Role.INSTITUTE_ADMIN ||
                principal.getRole() == Role.SUPER_ADMIN ||
                principal.getRole() == Role.ADMIN;

        if (!isInstituteAdmin) {
            // Warden user
            if ("INSTITUTE_ADMIN".equalsIgnoreCase(announcement.getAuthorRole()) || announcement.getHostel() == null) {
                throw new ForbiddenException("Wardens cannot delete institutional announcements posted by Institute Administration.");
            }
            if (announcement.getAuthor() != null && !announcement.getAuthor().getId().equals(principal.getId())) {
                if (principal.getHostelId() == null || announcement.getHostel() == null ||
                        !principal.getHostelId().equals(announcement.getHostel().getId())) {
                    throw new ForbiddenException("You can only delete announcements posted for your hostel.");
                }
            }
        }

        announcementRepository.delete(announcement);
    }
}

