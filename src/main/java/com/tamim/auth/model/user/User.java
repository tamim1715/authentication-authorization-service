package com.tamim.auth.model.user;
import com.tamim.auth.enums.UserType;
import com.tamim.auth.model.base.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User extends BaseEntity {

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private String phone;

    private String address;

    private UserType userType;

    private boolean enabled;

    private Instant lastLoginAt;
}
