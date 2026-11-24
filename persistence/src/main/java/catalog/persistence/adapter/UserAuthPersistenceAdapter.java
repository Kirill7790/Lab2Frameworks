package catalog.persistence.adapter;

import catalog.core.domain.UserAuth;
import catalog.core.port.UserAuthPort;
import catalog.persistence.entity.UserEntity;
import catalog.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuthPersistenceAdapter implements UserAuthPort {

    private final UserRepository repo;

    public UserAuthPersistenceAdapter(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<UserAuth> findAuthByUsername(String username) {
        return repo.findByUsername(username).map(this::toAuth);
    }

    @Override
    public Optional<UserAuth> findAuthByConfirmationCode(String code) {
        return repo.findByConfirmationCode(code).map(this::toAuth);
    }

    private UserAuth toAuth(UserEntity e) {
        return new UserAuth(e.getId(), e.getUsername(), e.getPassword(),
                e.getRole(), e.isEnabled());
    }
}