# Billing Service Architecture (`com.gogidix.warehousing.billing`)

## Container Diagram
```plantuml
@startuml
!include C4_Context.puml

System_Boundary(billing, "Billing Domain") {
    Container(billing_api, "Billing API", "Java/Spring Boot", "Handles all billing operations")
    ContainerDb(billing_db, "Billing Database", "PostgreSQL", "Stores transaction records")
    Container(billing_engine, "Billing Engine", "Java", "Calculates charges and generates invoices")
}

System_Ext(payment_gateway, "Payment Gateway", "Third-party payment processor")
System_Ext(warehousing_core, "Warehousing Core", "Main warehouse management system")

Rel(billing_api, billing_db, "Reads/Writes", "JDBC")
Rel(billing_api, payment_gateway, "Process payments", "REST/HTTPS")
Rel(billing_api, warehousing_core, "Get usage metrics", "gRPC")
Rel(billing_engine, billing_api, "Submit invoices", "Kafka")
@enduml
```

## Key Features
- Multi-currency support
- Automated invoice generation
- Payment gateway integrations
- Usage-based billing calculations