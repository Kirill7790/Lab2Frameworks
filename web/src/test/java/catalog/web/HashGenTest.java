package catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class HashGenTest {
    @Test
    void generate() {
        var enc = new BCryptPasswordEncoder();
        System.out.println("admin -> " + enc.encode("admin"));
        System.out.println("user  -> " + enc.encode("user"));
    }
}