package catalog.web;

import catalog.core.domain.User;
import catalog.core.exception.ConflictException;
import catalog.core.exception.ValidationException;
import catalog.core.service.UserService;
import catalog.web.mail.MailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          MailService mailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("email") String email,
                           Model model) {
        try {
            if (username == null || username.isBlank()) throw new ValidationException("Логін обов'язковий");
            if (password == null || password.length() < 4) throw new ValidationException("Пароль має бути ≥ 4 символів");
            if (email == null || !email.contains("@")) throw new ValidationException("Некоректний email");

            String hash = passwordEncoder.encode(password);
            User created = userService.register(username, hash, "USER");

            // надсилаємо лист із кодом підтвердження
            mailService.sendConfirmationEmail(email, created.getUsername(),
                    confirmationCodeOf(created));

            model.addAttribute("message", "Реєстрацію прийнято. Перевірте пошту для підтвердження.");
            return "auth/confirm-result";
        } catch (ValidationException | ConflictException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/register";
        }
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("code") String code, Model model) {
        try {
            userService.confirm(code);
            model.addAttribute("message", "Акаунт підтверджено. Тепер можете увійти.");
        } catch (Exception e) {
            model.addAttribute("error", "Невірний або прострочений код підтвердження.");
        }
        return "auth/confirm-result";
    }

    /**
     * Отримує код підтвердження, який adapter згенерував і зберіг.
     * Щоб не «текла» ця інформація через доменний User, ми беремо її
     * напряму з UserPersistencePort через окремий метод (див. нижче).
     */
    private String confirmationCodeOf(User user) {
        // Тимчасово: код зберігається в адаптері; додайте метод у UserService:
        //   public String getConfirmationCode(Long userId)
        return userService.getConfirmationCode(user.getId());
    }
}