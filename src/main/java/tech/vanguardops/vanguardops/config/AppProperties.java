package tech.vanguardops.vanguardops.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class AppProperties {

    private String environment;


    /**
     * Check if the current environment is production.
     * @return true if production, false otherwise.
     */
    public boolean isProduction() {
        return environment.equalsIgnoreCase("production");
    }

}