package catalog.core.domain;

public class UserAuth {
    private final Long id;
    private final String username;
    private final String passwordHash;
    private final String role;
    private final boolean enabled;

    public UserAuth(Long id, String username, String passwordHash, String role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
}