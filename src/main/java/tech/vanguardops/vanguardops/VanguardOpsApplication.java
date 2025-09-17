package tech.vanguardops.vanguardops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tech.vanguardops.vanguardops.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class VanguardOpsApplication {

    static void main(String[] args) {
        SpringApplication.run(VanguardOpsApplication.class, args);
    }

}