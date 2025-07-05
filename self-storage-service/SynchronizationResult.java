package com.gogidix.warehousing.selfstorage.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SynchronizationResult {
    private Boolean success;
    private String message;
}
