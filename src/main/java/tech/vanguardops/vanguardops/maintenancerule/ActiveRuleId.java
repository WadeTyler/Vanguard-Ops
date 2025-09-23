package tech.vanguardops.vanguardops.maintenancerule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor @NoArgsConstructor @Data
public class ActiveRuleId implements Serializable {

    // Must match the @Id property names in ActiveRule entity
    private Long aircraft;
    private Long rule;

}