package catalog.persistence.adapter;

import catalog.core.domain.User;
import catalog.core.port.UserAuthPort;
import catalog.core.port.UserPersistencePort;
import catalog.persistence.entity.UserEntity;
import catalog.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository repo;

    public UserPersistenceAdapter(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private User toDomain(UserEntity e) {
        return new User(e.getId(), e.getUsername(), e.getRole());
    }

    @Override
    public boolean existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    @Override
    public User save(String username, String passwordHash, String role,
                     boolean enabled, String confirmationCode) {
        UserEntity e = new UserEntity(username, passwordHash, role);
        e.setEnabled(enabled);
        e.setConfirmationCode(confirmationCode);
        return toDomain(repo.save(e));
    }

    @Override
    public Optional<User> confirmByCode(String code) {
        return repo.findByConfirmationCode(code).map(e -> {
            e.setEnabled(true);
            e.setConfirmationCode(null);
            return toDomain(repo.save(e));
        });
    }

    @Override
    public Optional<String> findConfirmationCodeByUserId(Long userId) {
        return repo.findById(userId).map(UserEntity::getConfirmationCode);
    }
}