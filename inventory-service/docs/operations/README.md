# inventory-service - Operations Guide

## Table of Contents
1. [Service Overview](#service-overview)
2. [Monitoring](#monitoring)
3. [Alerting](#alerting)
4. [Scaling](#scaling)
5. [Backup & Recovery](#backup--recovery)
6. [Maintenance](#maintenance)
7. [Security](#security)
8. [Troubleshooting](#troubleshooting)
9. [Performance Tuning](#performance-tuning)
10. [Disaster Recovery](#disaster-recovery)

## Service Overview
[Brief description of the service and its role in the system]

## Monitoring

### Key Metrics

| Metric | Description | Normal Range | Alert Threshold |
|--------|-------------|--------------|-----------------|
| `http.server.requests` | HTTP request rate | Varies | > 1000 req/s |
| `jvm.memory.used` | JVM memory usage | < 80% of max | > 90% of max |
| `process.cpu.usage` | CPU usage | < 70% | > 90% |
| `hikaricp.connections.active` | Active DB connections | < 80% of max | > 90% of max |
| `kafka.consumer.lag` | Consumer lag | < 1000 | > 5000 |

### Monitoring Setup

#### Prometheus Configuration
```yaml
# prometheus.yml
scrape_configs:
  - job_name: '[service-name]'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['[service-name]:[port]']
    scrape_interval: 15s
    scrape_timeout: 10s
```

## Alerting

### Critical Alerts

| Alert Name | Condition | Severity | Notification Channel |
|------------|-----------|----------|----------------------|
| HighErrorRate | `rate(http_server_errors_total[5m]) > 5` | Critical | PagerDuty, Email |
| HighLatency | `histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket[1m])) by (le)) > 1` | Warning | Email |
| ServiceDown | `up{job="[service-name]"} == 0` | Critical | PagerDuty, SMS |
| HighMemoryUsage | `jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100 > 90` | Warning | Email |
| DatabaseConnectionHigh | `hikaricp_connections_active / hikaricp_connections_max * 100 > 80` | Warning | Email |

### Alert Manager Configuration

```yaml
# alertmanager.yml
route:
  group_by: ['alertname', 'service', 'severity']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 3h
  receiver: 'team-email'
  routes:
  - match:
      service: '[service-name]'
      severity: 'critical'
    receiver: 'pager-duty'
  - match:
      service: '[service-name]'
      severity: 'warning'
    receiver: 'team-email'

receivers:
- name: 'pager-duty'
  pagerduty_configs:
  - service_key: '${PAGERDUTY_KEY}'
    severity: 'critical'
    
- name: 'team-email'
  email_configs:
  - to: 'team@example.com'
    send_resolved: true
```

## Scaling

### Horizontal Scaling

```bash
# Kubernetes
kubectl scale deployment [service-name] --replicas=3 -n [namespace]

# Docker Swarm
docker service scale [service-name]=3
```

### Vertical Scaling

#### JVM Memory Settings
```bash
# Recommended JVM settings for production
JAVA_OPTS="-Xms2g -Xmx4g -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

## Backup & Recovery

### Database Backup

#### Daily Backup Script
```bash
#!/bin/bash
BACKUP_DIR="/backups/[service-name]-db/$(date +%Y%m%d)"
mkdir -p $BACKUP_DIR

# Dump database
PGPASSWORD=$DB_PASSWORD pg_dump -h $DB_HOST -U $DB_USER -F c -b -v -f "$BACKUP_DIR/[service-name]_db_$(date +%Y%m%d_%H%M%S).dump" [db_name]

# Rotate backups older than 30 days
find /backups/[service-name]-db -type d -mtime +30 -exec rm -rf {} \;
```

## Maintenance

### Database Maintenance

#### Vacuum and Analyze
```sql
-- Manual vacuum
VACUUM (VERBOSE, ANALYZE) [table_name];

-- Schedule autovacuum
ALTER TABLE [table_name] SET (
  autovacuum_vacuum_scale_factor = 0.05,
  autovacuum_analyze_scale_factor = 0.02
);
```

## Security

### Access Control

#### API Authentication
```yaml
# application-security.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com/realms/[realm]
          jwk-set-uri: https://auth.example.com/realms/[realm]/protocol/openid-connect/certs
```

## Troubleshooting

### Common Issues

#### High CPU Usage
```bash
# Get thread dump
jstack -l <pid> > thread_dump_$(date +%Y%m%d_%H%M%S).txt

# Check CPU usage by thread
top -H -p <pid>
```

#### Memory Leaks
```bash
# Generate heap dump
jmap -dump:live,format=b,file=heap_dump.hprof <pid>

# Analyze with Eclipse MAT or similar tool
```

## Performance Tuning

### JVM Tuning
```bash
# Recommended JVM options for production
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=70"
```

### Database Tuning
```sql
-- Create appropriate indexes
CREATE INDEX idx_column_name ON table_name(column_name);

-- Update statistics
ANALYZE table_name;
```

## Disaster Recovery

### Recovery Point Objective (RPO)
- **Critical Data**: 5 minutes
- **Standard Data**: 1 hour

### Recovery Time Objective (RTO)
- **Critical Services**: 15 minutes
- **Non-Critical Services**: 4 hours

### Recovery Procedures

#### Database Recovery
```bash
# Stop the application
systemctl stop [service-name]

# Restore from backup
pg_restore -h localhost -U postgres -d [db_name] -c -v "/backups/[service-name]-db/[backup_file].dump"

# Restart the application
systemctl start [service-name]
```

#### Configuration Recovery
```bash
# Restore from version control
git checkout [commit-hash] -- config/

# Apply configuration changes
./apply-configuration.sh
```

## Rollback Procedures

### Application Rollback
```bash
# Kubernetes rollback
kubectl rollout undo deployment/[service-name] -n [namespace]

# Verify rollback
kubectl rollout status deployment/[service-name] -n [namespace]
```

### Database Rollback
```sql
-- If using transactions
ROLLBACK TO SAVEPOINT before_changes;

-- If using migrations
./mvnw flyway:repair -Dflyway.locations=filesystem:./db/migration -Dflyway.url=jdbc:postgresql://localhost:5432/[db_name] -Dflyway.user=[user] -Dflyway.password=[password]
```

## Incident Response

### Severity Levels
1. **SEV-1 (Critical)**: Complete service outage
2. **SEV-2 (High)**: Major functionality impacted
3. **SEV-3 (Medium)**: Minor issues with workarounds
4. **SEV-4 (Low)**: Cosmetic or non-critical issues

### Communication Plan
- **SEV-1/SEV-2**: Immediate notification to team leads and stakeholders
- **SEV-3/SEV-4**: Notification during business hours
- **Status Updates**: Every 30 minutes for SEV-1/SEV-2

### Post-Incident Review
1. Document timeline of events
2. Identify root cause
3. Document resolution steps
4. Create action items to prevent recurrence
5. Update runbooks and documentation

## Maintenance Schedule

### Weekly Tasks
- [ ] Review and rotate logs
- [ ] Check disk space
- [ ] Verify backup integrity
- [ ] Review monitoring alerts

### Monthly Tasks
- [ ] Review security patches
- [ ] Update dependencies
- [ ] Test restore procedures
- [ ] Review capacity planning

## Support Contacts

| Role | Name | Contact | Availability |
|------|------|---------|--------------|
| Primary On-Call | [Name] | @slack / phone | 24/7 |
| Secondary On-Call | [Name] | @slack / phone | Business Hours |
| Database Admin | [Name] | email | Business Hours |
| Security Contact | [Name] | @security-team | 24/7 |

## Related Documentation
- [API Documentation](../api/README.md)
- [Architecture](../architecture/README.md)
- [Setup Guide](../setup/README.md)
