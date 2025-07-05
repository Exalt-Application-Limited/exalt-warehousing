package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealthStatus {
    
    private String status; // HEALTHY, DEGRADED, DOWN
    private String version;
    private LocalDateTime timestamp;
    private boolean databaseConnected;
    private boolean paymentServiceConnected;
    private boolean emailServiceConnected;
    private Map<String, String> componentStatus;
    private long uptime;
    private double cpuUsage;
    private double memoryUsage;
    private int activeConnections;
    private String message;
}
