package catalog.core.port;

import catalog.core.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();

    boolean existsByUsername(String username);

    User save(String username, String passwordHash, String role,
              boolean enabled, String confirmationCode);

    Optional<User> confirmByCode(String code);

    Optional<String> findConfirmationCodeByUserId(Long userId);
}