package catalog.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupInfoLogger {
    private static final Logger log = LoggerFactory.getLogger(StartupInfoLogger.class);

    @Autowired
    private AppInfoService appInfoService;

    @Value("${catalog.app-name}")
    private String appName;

    @Value("${catalog.page.default-size}")
    private int defaultPageSize;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("=== Application started ===");
        log.info("App name: {}", appName);
        log.info("Description: {}", appInfoService.describe());
        log.info("Default page size: {}", defaultPageSize);
    }
}