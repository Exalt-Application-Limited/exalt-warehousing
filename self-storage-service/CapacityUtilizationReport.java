package com.exalt.warehousing.selfstorage.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NearbyStorageLocation {
    private String locationId;
    private String locationName;
    private Double distanceKm;
    private Boolean isOptimal;
}
