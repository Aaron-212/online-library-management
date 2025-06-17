package com.aaron212.onlinelibrarymanagement.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Username is required")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password hash is required")
    private String passwordHash;

    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "TINYINT")
    @NotNull(message = "Role is required")
    @ColumnDefault("1")
    private Role role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp lastUpdateTime;

    public UserDetails toUserDetails() {
        return new org.springframework.security.core.userdetails.User(
                this.username,
                this.passwordHash,
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name())));
    }

    @Getter
    public enum Role {
        USER(1),
        ADMIN(2),
        LIBRARIAN(3);

        private final int value;

        Role(int value) {
            this.value = value;
        }
    }
}
