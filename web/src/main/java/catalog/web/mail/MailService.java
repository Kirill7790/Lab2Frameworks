package catalog.web.mail;

import catalog.core.domain.Book;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    public MailService(JavaMailSender mailSender, Configuration freemarkerConfig) {
        this.mailSender = mailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    public void sendNewBookEmail(Book book) {
        try {
            int year = book.getYear() != null ? book.getYear() : LocalDateTime.now().getYear();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDate = LocalDateTime.now().format(formatter);

            Map<String, Object> model = new HashMap<>();
            model.put("title", book.getTitle());
            model.put("author", book.getAuthor());
            model.put("year", year);
            model.put("added", formattedDate);

            Template template = freemarkerConfig.getTemplate("new_book.ftl");
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            String html = writer.toString();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo("test@example.com");
            helper.setSubject("Нова книга: " + book.getTitle());
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Лист для книги '{}' успішно відправлено", book.getTitle());

        } catch (Exception e) {
            log.error(">>> ПОМИЛКА ВІДПРАВКИ ЛИСТА", e);
        }
    }
}