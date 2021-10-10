package com.roomsbooking.backend.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class responsible for keeping information about single user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 60)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(min = 2, max = 60)
    @Column(nullable = false)
    private String surname;

    @NotBlank
    @Size(min = 5, max = 200)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = 2, max = 60)
    @Email(regexp = ".+@.+\\..+")
    @Column(nullable = false)
    private String email;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(
        name = "phone_number_id",
        referencedColumnName = "id")
    private PhoneNumber phoneNumber;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @NotNull
    @Column(nullable = false, name = "is_active")
    private Boolean isActive;
}
