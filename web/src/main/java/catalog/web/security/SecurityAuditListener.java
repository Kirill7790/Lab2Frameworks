package catalog.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuditListener {

    private static final Logger audit = LoggerFactory.getLogger("SECURITY_AUDIT");

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Authentication a = event.getAuthentication();
        audit.info("LOGIN SUCCESS user={} authorities={}", a.getName(), a.getAuthorities());
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        audit.warn("LOGIN FAILURE user={} reason=bad-credentials",
                event.getAuthentication().getName());
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        audit.info("LOGOUT user={}", event.getAuthentication().getName());
    }
}