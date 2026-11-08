package catalog.persistence.adapter;

import catalog.core.domain.User;
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
}