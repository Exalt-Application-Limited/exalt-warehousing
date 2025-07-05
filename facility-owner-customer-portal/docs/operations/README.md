# Operations Documentation

This document provides operational guidelines and procedures for running and maintaining the Facility Owner Customer Portal in production environments.

## 🚀 Deployment Operations

### Production Deployment

```bash
# Build production image
docker build -t facility-portal:latest .

# Tag for registry
docker tag facility-portal:latest registry.gogidix-storage.com/facility-portal:v1.0.0

# Push to registry
docker push registry.gogidix-storage.com/facility-portal:v1.0.0

# Deploy to Kubernetes
kubectl apply -f k8s/

# Verify deployment
kubectl get pods -n warehousing -l app=facility-portal
```

### Blue-Green Deployment

```bash
# Create new deployment version
kubectl set image deployment/facility-portal facility-portal=registry.gogidix-storage.com/facility-portal:v1.0.1 -n warehousing

# Monitor rollout
kubectl rollout status deployment/facility-portal -n warehousing

# Rollback if needed
kubectl rollout undo deployment/facility-portal -n warehousing
```

## 📊 Monitoring & Observability

### Health Checks

```bash
# Application health
curl -f http://facility-portal.warehousing.svc.cluster.local:3202/health

# Kubernetes readiness probe
kubectl describe pod <pod-name> -n warehousing

# Check logs
kubectl logs -f deployment/facility-portal -n warehousing
```

### Key Metrics to Monitor

| Metric | Threshold | Action |
|--------|-----------|--------|
| **Response Time** | > 2000ms | Scale up replicas |
| **Error Rate** | > 5% | Check backend services |
| **Memory Usage** | > 80% | Increase memory limits |
| **CPU Usage** | > 70% | Scale horizontally |
| **Disk Usage** | > 85% | Clean up logs |

### Alerting Configuration

```yaml
# Prometheus alerts
groups:
  - name: facility-portal
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
```

## 🔧 Maintenance Operations

### Regular Maintenance Tasks

#### Daily
- [ ] Check application logs for errors
- [ ] Verify health check endpoints
- [ ] Monitor resource usage
- [ ] Review alert notifications

#### Weekly
- [ ] Analyze performance metrics
- [ ] Review security scan reports
- [ ] Update dependencies (if needed)
- [ ] Backup configuration files

#### Monthly
- [ ] Performance optimization review
- [ ] Security vulnerability assessment
- [ ] Capacity planning review
- [ ] Disaster recovery testing

### Log Management

```bash
# View application logs
kubectl logs -f deployment/facility-portal -n warehousing

# View logs from specific time
kubectl logs --since=1h deployment/facility-portal -n warehousing

# Export logs for analysis
kubectl logs deployment/facility-portal -n warehousing > facility-portal.log

# Filter error logs
kubectl logs deployment/facility-portal -n warehousing | grep ERROR
```

### Log Retention Policy

| Log Level | Retention Period | Storage Location |
|-----------|------------------|------------------|
| **ERROR** | 90 days | ELK Stack |
| **WARN** | 30 days | ELK Stack |
| **INFO** | 7 days | ELK Stack |
| **DEBUG** | 1 day | Local only |

## 🔒 Security Operations

### SSL/TLS Certificate Management

```bash
# Check certificate expiration
openssl x509 -in /etc/ssl/certs/facility-portal.crt -text -noout

# Renew Let's Encrypt certificate
certbot renew --cert-name portal.gogidix-storage.com

# Update Kubernetes secret
kubectl create secret tls facility-portal-tls \
  --cert=path/to/cert.pem \
  --key=path/to/key.pem \
  -n warehousing --dry-run=client -o yaml | kubectl apply -f -
```

### Security Scanning

```bash
# Container vulnerability scan
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  -v $HOME/Library/Caches:/root/.cache/ \
  aquasec/trivy image facility-portal:latest

# Dependencies vulnerability scan
npm audit
npm audit fix

# OWASP ZAP security testing
docker run -t owasp/zap2docker-stable zap-baseline.py \
  -t https://portal.gogidix-storage.com
```

## 📈 Performance Operations

### Performance Monitoring

```bash
# Load testing with Apache Bench
ab -n 1000 -c 10 https://portal.gogidix-storage.com/api/v1/dashboard

# Memory profiling
kubectl top pods -n warehousing -l app=facility-portal

# Network monitoring
kubectl exec -it <pod-name> -n warehousing -- netstat -tulpn
```

### Performance Optimization

```bash
# Enable gzip compression
kubectl patch configmap nginx-config -n warehousing --patch='
{
  "data": {
    "gzip": "on",
    "gzip_types": "text/plain application/json application/javascript text/css"
  }
}'

# Configure CDN caching
# Update Cloudflare page rules for static assets
```

## 🚨 Incident Response

### Incident Response Playbook

#### Step 1: Detection
- Monitor alerts from Prometheus/Grafana
- Check application logs for errors
- Verify external monitoring (Pingdom/StatusPage)

#### Step 2: Assessment
- Determine severity level (P0-P3)
- Identify affected components
- Estimate impact on users

#### Step 3: Response
- Notify stakeholders (if P0/P1)
- Begin troubleshooting
- Document timeline

#### Step 4: Resolution
- Apply fix or rollback
- Verify service restoration
- Update monitoring

#### Step 5: Post-Incident
- Conduct post-mortem
- Update documentation
- Implement preventive measures

### Common Issues & Solutions

| Issue | Symptoms | Solution |
|-------|----------|----------|
| **High Memory Usage** | Pod restarts, slow response | Increase memory limits, optimize code |
| **Database Connection Errors** | 500 errors, timeouts | Check DB connectivity, increase pool size |
| **Authentication Failures** | 401 errors | Verify JWT configuration, check auth service |
| **Static Asset Loading** | Missing CSS/JS | Check CDN, verify nginx config |

## 🔄 Backup & Recovery

### Configuration Backup

```bash
# Backup Kubernetes configurations
kubectl get all -n warehousing -o yaml > facility-portal-backup.yaml

# Backup ConfigMaps and Secrets
kubectl get configmap,secret -n warehousing -o yaml > facility-portal-config-backup.yaml

# Store in version control
git add facility-portal-backup.yaml
git commit -m "Backup facility-portal configuration"
```

### Disaster Recovery

```bash
# Restore from backup
kubectl apply -f facility-portal-backup.yaml

# Verify restoration
kubectl get pods -n warehousing -l app=facility-portal
kubectl logs -f deployment/facility-portal -n warehousing

# Test functionality
curl -f https://portal.gogidix-storage.com/health
```

## 📞 On-Call Procedures

### Escalation Matrix

| Severity | Response Time | Escalation Path |
|----------|---------------|----------------|
| **P0 - Critical** | 15 minutes | Lead Developer → Engineering Manager → CTO |
| **P1 - High** | 30 minutes | Developer → Lead Developer |
| **P2 - Medium** | 2 hours | Developer |
| **P3 - Low** | Next business day | Developer |

### Contact Information

- **Primary On-Call**: +1-555-DEV-ONCALL
- **Secondary On-Call**: +1-555-OPS-BACKUP
- **Engineering Manager**: manager@gogidix-storage.com
- **Slack Channel**: #facility-portal-alerts

## 📋 Runbooks

### Application Won't Start

1. Check pod status: `kubectl get pods -n warehousing`
2. Check pod logs: `kubectl logs <pod-name> -n warehousing`
3. Check resource constraints: `kubectl describe pod <pod-name> -n warehousing`
4. Verify ConfigMap/Secret: `kubectl get configmap,secret -n warehousing`
5. Check node resources: `kubectl top nodes`

### Database Connection Issues

1. Test database connectivity: `kubectl exec -it <pod-name> -n warehousing -- nc -zv db-host 5432`
2. Check database service: `kubectl get svc -n database`
3. Verify connection string in ConfigMap
4. Check database logs: `kubectl logs -f deployment/postgres -n database`
5. Restart application if needed: `kubectl rollout restart deployment/facility-portal -n warehousing`

### High CPU/Memory Usage

1. Check current usage: `kubectl top pods -n warehousing`
2. Review application logs for errors
3. Check for memory leaks in monitoring
4. Scale horizontally: `kubectl scale deployment facility-portal --replicas=5 -n warehousing`
5. Consider vertical scaling: Update resource limits in deployment

---

**Last Updated**: 2025-06-30  
**Operations Version**: 1.0.0  
**Maintained By**: DevOps Team