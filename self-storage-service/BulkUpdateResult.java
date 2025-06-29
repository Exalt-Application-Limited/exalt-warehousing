package com.exalt.warehousing.selfstorage.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString  
public class BulkUpdateResult {
    private Integer totalProcessed;
    private Integer successful;
    private Integer failed;
}
