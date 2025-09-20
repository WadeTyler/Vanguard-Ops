package tech.vanguardops.vanguardops.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authority entity representing a role or permission in the system.
 * Current Valid Authorities:
 * - ROLE_ADMIN
 * - ROLE_TECHNICIAN
 * - ROLE_OPERATOR
 * - ROLE_PLANNER
 * - ROLE_ANALYST
 */
@Entity
@Table(name = "authorities")
@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class Authority {

    @Id
    @Column(length = 50)
    private String authority;
}