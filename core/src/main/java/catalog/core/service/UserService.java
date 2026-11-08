package catalog.core.service;

import catalog.core.domain.User;
import catalog.core.exception.NotFoundException;
import catalog.core.port.UserPersistencePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserPersistencePort port;

    public UserService(UserPersistencePort port) {
        this.port = port;
    }

    public User findById(Long id) {
        return port.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    public List<User> findAll() {
        return port.findAll();
    }
}