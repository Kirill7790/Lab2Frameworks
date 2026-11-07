package catalog.web.config;

import freemarker.template.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@org.springframework.context.annotation.Configuration
public class EmailFreemarkerConfig {

    @Bean
    @Primary
    public Configuration freemarkerEmailConfig() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        cfg.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "/mail-templates/"
        );
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        return cfg;
    }
}