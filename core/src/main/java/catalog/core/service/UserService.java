package catalog.core.service;

import catalog.core.domain.User;
import catalog.core.exception.ConflictException;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.port.UserPersistencePort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    public User findByUsername(String username) {
        return port.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User " + username + " not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {
        return port.findAll();
    }

    public String getConfirmationCode(Long userId) {
        return port.findConfirmationCodeByUserId(userId)
                .orElseThrow(() -> new NotFoundException("No confirmation code for user " + userId));
    }

    /**
     * Реєстрація: пароль УЖЕ захешований у web-шарі (PasswordEncoder).
     * Роль за замовчуванням — USER, enabled=false до підтвердження.
     */
    @Transactional
    public User register(String username, String passwordHash, String role) {
        if (username == null || username.isBlank() || username.length() > 100) {
            throw new ValidationException("Username must be 1..100 chars");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new ValidationException("Password hash is required");
        }
        if (port.existsByUsername(username.trim())) {
            throw new ConflictException("Username already taken: " + username);
        }
        String code = UUID.randomUUID().toString();
        return port.save(username.trim(), passwordHash,
                (role == null || role.isBlank()) ? "USER" : role,
                false, code);
    }

    @Transactional
    public User confirm(String code) {
        if (code == null || code.isBlank()) {
            throw new ValidationException("Confirmation code is required");
        }
        return port.confirmByCode(code)
                .orElseThrow(() -> new NotFoundException("Invalid confirmation code"));
    }
}