package catalog.core.security;

import catalog.core.domain.UserAuth;
import catalog.core.port.UserAuthPort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CatalogUserDetailsService implements UserDetailsService {

    private final UserAuthPort port;

    public CatalogUserDetailsService(UserAuthPort port) {
        this.port = port;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAuth auth = port.findAuthByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(auth.getUsername())
                .password(auth.getPasswordHash())
                .roles(auth.getRole())
                .disabled(!auth.isEnabled())
                .build();
    }
}