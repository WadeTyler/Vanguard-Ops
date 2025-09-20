package tech.vanguardops.vanguardops.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for managing user details.
 * @param firstName the user's first name
 * @param lastName the user's last name
 * @param authority the user's authority/role, must be one of the predefined roles
 */
public record ManageUserRequest(
        @NotBlank
        @Size(max = 255)
        String firstName,

        @NotBlank
        @Size(max = 255)
        String lastName,

        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "ROLE_ADMIN|ROLE_TECHNICIAN|ROLE_OPERATOR|ROLE_PLANNER|ROLE_ANALYST",
                message = "Authority must be one of: ROLE_ADMIN, ROLE_TECHNICIAN, ROLE_OPERATOR, ROLE_PLANNER, ROLE_ANALYST")
        String authority
) {
}