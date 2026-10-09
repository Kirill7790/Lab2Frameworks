package catalog.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BeanReporter {
    private static final Logger log = LoggerFactory.getLogger(BeanReporter.class);

    private final ApplicationContext ctx;

    public BeanReporter(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void reportCatalogBeans() {
        log.info("=== Beans in 'catalog' package ===");
        Arrays.stream(ctx.getBeanDefinitionNames())
                .filter(name -> name.toLowerCase().contains("book")
                        || name.toLowerCase().contains("comment")
                        || name.toLowerCase().contains("catalog")
                        || name.toLowerCase().contains("app")
                        || name.toLowerCase().contains("database"))
                .sorted()
                .forEach(name -> log.info("  - {} -> {}",
                        name, ctx.getBean(name).getClass().getSimpleName()));
    }
}