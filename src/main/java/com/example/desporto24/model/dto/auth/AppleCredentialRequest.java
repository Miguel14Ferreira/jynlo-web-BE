package com.example.desporto24.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Apple sends the name only on the very first sign-in, from the device, never in the token. */
public record AppleCredentialRequest(
        @NotBlank String credential,
        @Size(max = 100) String fullName
) {
}
