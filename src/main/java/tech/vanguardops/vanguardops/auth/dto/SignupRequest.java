package tech.vanguardops.vanguardops.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank
        @Email
        @Size(max = 255)
        String username,

        @NotBlank
        @Size(min = 1, max = 60)
        String password,

        @NotBlank
        @Size(max = 255)
        String firstName,

        @NotBlank
        @Size(max = 255)
        String lastName
) {

}