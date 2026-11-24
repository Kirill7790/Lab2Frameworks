package catalog.core.port;

import catalog.core.domain.UserAuth;
import java.util.Optional;

public interface UserAuthPort {
    Optional<UserAuth> findAuthByUsername(String username);
    Optional<UserAuth> findAuthByConfirmationCode(String code);
}