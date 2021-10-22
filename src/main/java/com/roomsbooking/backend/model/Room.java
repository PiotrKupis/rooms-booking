package com.roomsbooking.backend.model;

import com.roomsbooking.backend.enums.RoomAmenity;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class responsible for keeping information about single room.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(exclude = "resort")
@ToString(exclude = "resort")
@Entity(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    @NotNull
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @NotNull
    @Column(precision = 12, scale = 3)
    private BigDecimal price;

    @NotBlank
    @Column(name = "price_currency", length = 3)
    private String priceCurrency;

    @ElementCollection(targetClass = RoomAmenity.class)
    @CollectionTable(name = "room_amenity", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "amenity")
    @Enumerated(EnumType.STRING)
    private Set<RoomAmenity> roomAmenities = new HashSet<>();

    @NotNull
    @Column(name = "single_bed_quantity", nullable = false)
    private Integer singleBedQuantity;

    @NotNull
    @Column(name = "double_bed_quantity", nullable = false)
    private Integer doubleBedQuantity;

    @NotNull
    @Column(name = "king_size_bed_quantity", nullable = false)
    private Integer kingSizeBedQuantity;

    @NotNull
    @Column(name = "max_residents_number", nullable = false)
    private Integer maxResidentsNumber;
}
