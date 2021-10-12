package com.roomsbooking.backend.model;

import com.roomsbooking.backend.enums.ResortAmenity;
import com.roomsbooking.backend.enums.ResortType;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Class responsible for keeping information about single resort.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@Entity(name = "resort")
public class Resort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id",
        referencedColumnName = "id")
    private User owner;

    @NotBlank
    @Column(name = "resort_name", nullable = false)
    private String resortName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resort_type", nullable = false)
    private ResortType resortType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(
        name = "address_id",
        referencedColumnName = "id")
    private Address address;

    @ElementCollection(targetClass = ResortAmenity.class)
    @CollectionTable(name = "resort_amenity", joinColumns = @JoinColumn(name = "resort_id"))
    @Column(name = "amenity")
    @Enumerated(EnumType.STRING)
    private Set<ResortAmenity> resortAmenities = new HashSet<>();

    @NotNull
    @Column(name = "smoking_permitted", nullable = false)
    private Boolean smokingPermitted;

    @NotNull
    @Column(name = "animals_permitted", nullable = false)
    private Boolean animalsPermitted;

    @NotNull
    @Column(name = "party_permitted", nullable = false)
    private Boolean partyPermitted;

    @NotNull
    @Column(name = "hotel_day_start", columnDefinition = "TIME", nullable = false)
    private LocalTime hotelDayStart;

    @NotNull
    @Column(name = "hotel_day_end", columnDefinition = "TIME", nullable = false)
    private LocalTime hotelDayEnd;

    @NotNull
    @Column(name = "days_of_free_cancel", nullable = false)
    private Integer daysOfFreeCancel;

    @NotNull
    @Column(name = "is_parking_available", nullable = false)
    private Boolean isParkingAvailable;

    @NotNull
    @Column(name = "parking_fee", precision = 12, scale = 3)
    private BigDecimal parkingFee;

    @NotNull
    @Column(name = "parking_fee_currency", length = 3)
    private String parkingFeeCurrency;
}
