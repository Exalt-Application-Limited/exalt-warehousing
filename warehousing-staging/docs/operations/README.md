# Warehousing Staging - Operations Guide

## Table of Contents
1. [Monitoring](#monitoring)
2. [Alerting](#alerting)
3. [Scaling](#scaling)
4. [Backup & Recovery](#backup--recovery)
5. [Maintenance](#maintenance)
6. [Troubleshooting](#troubleshooting)
7. [Performance Tuning](#performance-tuning)
8. [Security](#security)
9. [Disaster Recovery](#disaster-recovery)
10. [Incident Response](#incident-response)
11. [Support Contacts](#support-contacts)

## Monitoring

### Key Metrics
| Metric Name | Type | Description | Alert Threshold |
|-------------|------|-------------|-----------------|
| `http.server.requests` | Counter | HTTP request count | N/A |
| `jvm.memory.used` | Gauge | JVM memory usage | >80% of max |
| `jvm.gc.pause` | Timer | GC pause duration | >1s |
| `hikaricp.connections.active` | Gauge | Active DB connections | >80% of max |
| `kafka.consumer.lag` | Gauge | Kafka consumer lag | >1000 |

### Prometheus Configuration
```yaml
scrape_configs:
  - job_name: 'warehousing-staging'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['warehousing-staging:8080']
```

### Grafana Dashboard
Import the following dashboard JSON:
```json
{
  "dashboard": {
    "title": "Warehousing Staging Service",
    "panels": [
      {
        "title": "HTTP Requests",
        "type": "graph",
        "datasource": "Prometheus",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count[5m])",
            "legendFormat": "{{method}} {{status}}"
          }
        ]
      }
    ]
  }
}
```

## Alerting

### Alert Rules
```yaml
groups:
- name: warehousing-staging
  rules:
  - alert: HighErrorRate
    expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) / rate(http_server_requests_seconds_count[5m]) > 0.01
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "High error rate on {{ $labels.instance }}"
      description: "Error rate is {{ $value }}% for {{ $labels.path }}"

  - alert: HighJvmMemoryUsage
    expr: jvm_memory_used_bytes / jvm_memory_max_bytes * 100 > 80
    for: 10m
    labels:
      severity: warning
```

### Alert Destinations
| Alert Level | Notification Channel | Escalation Path |
|-------------|----------------------|-----------------|
| Critical | PagerDuty, Slack | Immediate escalation |
| Warning | Email, Slack | Review within 24h |
| Info | Slack | Weekly report |

## Scaling

### Horizontal Scaling
```bash
# Scale to 3 instances
kubectl scale deployment/warehousing-staging --replicas=3 -n staging
```

### Auto-scaling
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: warehousing-staging
  namespace: staging
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: warehousing-staging
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## Backup & Recovery

### Database Backup
```bash
# Daily backup script
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > backup_$(date +%Y%m%d).sql

# Upload to S3
aws s3 cp backup_$(date +%Y%m%d).sql s3://warehousing-backups/staging/db/
```

### Restore from Backup
```bash
# Download from S3
aws s3 cp s3://warehousing-backups/staging/db/backup_20230625.sql .

# Restore database
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME < backup_20230625.sql
```

## Maintenance

### Database Maintenance
```sql
-- Optimize tables
OPTIMIZE TABLE large_table;

-- Analyze query performance
ANALYZE TABLE important_table;

-- Check for long-running transactions
SELECT * FROM information_schema.innodb_trx WHERE TIME_TO_SEC(TIMEDIFF(NOW(), trx_started)) > 60;
```

### Log Rotation
```bash
# Configure logrotate
/var/log/warehousing-staging/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0640 appuser appuser
    postrotate
        kill -HUP `cat /var/run/warehousing-staging.pid`
    endscript
}
```

## Troubleshooting

### Common Issues

#### High CPU Usage
```bash
# Get thread dump
jcmd <pid> Thread.print -l > thread_dump_$(date +%Y%m%d_%H%M%S).txt

# Check CPU usage by thread
top -H -p <pid>
```

#### Memory Leak
```bash
# Generate heap dump
jmap -dump:live,format=b,file=heap_dump.hprof <pid>

# Analyze with Eclipse MAT or similar tool
```

### Log Analysis
```bash
# Search for errors
grep -i error /var/log/warehousing-staging/application.log

# Monitor logs in real-time
tail -f /var/log/warehousing-staging/application.log | grep -i error
```

## Performance Tuning

### JVM Options
```
-XX:InitialRAMPercentage=50.0
-XX:MaxRAMPercentage=75.0
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:ParallelGCThreads=4
-XX:ConcGCThreads=2
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/warehousing-staging/heapdump.hprof
-XX:+ExitOnOutOfMemoryError
```

### Database Tuning
```ini
# MySQL my.cnf
[mysqld]
innodb_buffer_pool_size = 4G
innodb_log_file_size = 1G
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT
innodb_read_io_threads = 8
innodb_write_io_threads = 8
```

## Security

### Security Hardening
```bash
# Run security audit
audit2allow -a

# Check for open ports
netstat -tuln

# Verify file permissions
find /opt/warehousing-staging -type f -perm -o+w -ls
```

### Security Headers
```yaml
# Spring Security configuration
security:
  headers:
    frame-options: DENY
    content-security-policy: default-src 'self'
    xss-protection: 1; mode=block
    content-type-options: nosniff
    referrer-policy: strict-origin-when-cross-origin
```

## Disaster Recovery

### Recovery Time Objective (RTO)
- **Critical Services**: 15 minutes
- **Non-Critical Services**: 4 hours

### Recovery Point Objective (RPO)
- **Critical Data**: 5 minutes
- **Non-Critical Data**: 1 hour

### Recovery Procedures
1. **Database Failure**
   - Failover to standby replica
   - Restore from latest backup if needed

2. **Service Outage**
   - Redirect traffic to secondary region
   - Scale up additional instances

## Incident Response

### Severity Levels
| Level | Description | Response Time |
|-------|-------------|---------------|
| SEV-1 | Complete outage | 15 minutes |
| SEV-2 | Major degradation | 1 hour |
| SEV-3 | Minor issues | 4 hours |

### Communication Plan
1. **Internal**
   - Immediate notification to on-call engineer
   - Create incident channel in Slack
   - Update status page

2. **External**
   - Customer notifications if needed
   - Regular status updates
   - Post-mortem report

## Support Contacts

### Engineering
- **Primary**: John Doe (johndoe@example.com, @johndoe)
- **Backup**: Jane Smith (janesmith@example.com, @janesmith)

### Operations
- **24/7 Support**: ops@example.com
- **Emergency Pager**: +1-555-123-4567

### Vendor Contacts
- **Cloud Provider**: AWS Support
- **Database**: Enterprise Support (support@mysql.com)
