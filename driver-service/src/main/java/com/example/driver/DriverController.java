package com.example.driver;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class DriverController {
  @Autowired private WebServerApplicationContext webServerContext;
  @Value("${spring.application.name}") private String serviceName;
  @GetMapping({"/drivers", "/drivers/"})
  public Map<String, Object> drivers() {
    int actualPort = webServerContext.getWebServer().getPort();
    return Map.of("message", "Driver service response", "service", serviceName,
      "instancePort", actualPort);
  }
  @GetMapping("/health-check")
  public Map<String, String> health() {
    return Map.of("status", "UP", "service", serviceName,
      "port", String.valueOf(webServerContext.getWebServer().getPort()));
  }
}
