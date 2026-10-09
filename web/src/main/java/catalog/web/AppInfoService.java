package catalog.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppInfoService {

    @Value("${catalog.app-name}")
    private String appName;

    @Value("${catalog.version}")
    private String version;

    public String describe() {
        return appName + " v" + version;
    }
}