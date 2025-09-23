package tech.vanguardops.vanguardops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import tech.vanguardops.vanguardops.config.AppProperties;
import tech.vanguardops.vanguardops.config.security.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, JwtProperties.class})
@EnableScheduling
public class VanguardOpsApplication {

    static void main(String[] args) {
        SpringApplication.run(VanguardOpsApplication.class, args);
    }

}