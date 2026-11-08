package catalog.core.port;

import catalog.core.domain.User;
import java.util.Optional;
import java.util.List;

public interface UserPersistencePort {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
}