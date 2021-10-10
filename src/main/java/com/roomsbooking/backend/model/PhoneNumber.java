package com.roomsbooking.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class responsible for keeping information about phone number.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "phone_number")
public class PhoneNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 4)
    @Column(nullable = false, name = "country_code", length = 4)
    private String countryCode;

    @NotBlank
    @Size(min = 6, max = 12)
    @Column(nullable = false, name = "phone_number", length = 12)
    private String phoneNumber;
}
