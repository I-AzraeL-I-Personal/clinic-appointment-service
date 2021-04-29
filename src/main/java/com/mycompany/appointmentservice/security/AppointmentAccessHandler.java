package com.mycompany.appointmentservice.security;

import com.mycompany.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("appointmentAccess")
@RequiredArgsConstructor
public class AppointmentAccessHandler {

    private final AppointmentRepository appointmentRepository;

    public boolean isOwner(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticationValid(authentication)) {
            return isAdmin(authentication) || isUserUuidEqual(authentication, id);
        }
        return false;
    }

    public boolean isOwnerOrDoctor(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticationValid(authentication)) {
            return isAdmin(authentication) || isDoctor(authentication) || isUserUuidEqual(authentication, id);
        }
        return false;
    }

    private boolean isUserUuidEqual(Authentication authentication, Long appointmentId) {
        @SuppressWarnings("unchecked")
        var credentials = (Map<String, Object>) authentication.getCredentials();
        var appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            return false;
        }
        return appointment.get().getPatientUUID().equals(credentials.get(JwtProperties.TOKEN_CLAIM_UUID));
    }

    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && authentication.getCredentials() != null && authentication.getCredentials().toString().length() != 0;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.withPrefix()));
    }

    private boolean isDoctor(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.DOCTOR.withPrefix()));
    }
}
