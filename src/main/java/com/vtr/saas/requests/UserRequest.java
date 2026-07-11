package com.vtr.saas.requests;


import com.vtr.saas.entities.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String password;
}
