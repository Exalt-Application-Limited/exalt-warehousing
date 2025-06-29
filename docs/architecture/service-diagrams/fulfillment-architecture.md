# Fulfillment Service Architecture (`com.exalt.warehousing.fulfillment`)

## Container Diagram
```plantuml
@startuml
!include C4_Context.puml

System_Boundary(fulfillment, "Fulfillment Domain") {
    Container(fulfillment_api, "Fulfillment API", "Java/Spring Boot", "Orchestrates order processing")
    ContainerDb(fulfillment_db, "Fulfillment DB", "PostgreSQL", "Stores order states")
    Container(picking_svc, "Picking Service", "Java", "Manages warehouse picking operations")
    Container(packing_svc, "Packing Service", "Java", "Handles order packaging")
}

System_Ext(inventory_svc, "Inventory Service", "com.exalt.warehousing.inventory")
System_Ext(shipping_svc, "Shipping Service", "com.exalt.warehousing.shipping")

Rel(fulfillment_api, fulfillment_db, "Reads/Writes", "JDBC")
Rel(fulfillment_api, inventory_svc, "Reserve inventory", "gRPC")
Rel(fulfillment_api, shipping_svc, "Initiate shipments", "REST")
Rel(picking_svc, fulfillment_api, "Receive pick lists", "Kafka")
Rel(packing_svc, fulfillment_api, "Confirm packaging", "Kafka")
@enduml
```

## Key Features
- Order lifecycle management
- Real-time inventory synchronization
- Multi-warehouse routing logic
- Packing optimization algorithms