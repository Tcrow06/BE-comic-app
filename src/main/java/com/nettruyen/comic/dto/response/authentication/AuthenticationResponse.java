package com.nettruyen.comic.dto.response.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticationResponse {
    Integer isActive;
    String accessToken;
    String refreshToken;

    String message;

    String firstName;
    String lastName;
    String picture;

}
