package com.gogidix.warehousing.selfstorage.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationHealthCheckResult {
    private Boolean healthy;
    private String status;
}
