package com.gogidix.warehousing.operations.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Equipment Type Enumeration
 * 
 * Defines the different types of equipment used in warehouse
 * and vendor facility operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum EquipmentType {

    FORKLIFT("Forklift", "Powered industrial vehicle for lifting and moving materials"),
    PALLET_JACK("Pallet Jack", "Manual or powered device for moving pallets"),
    ORDER_PICKER("Order Picker", "Vehicle for picking items at height"),
    REACH_TRUCK("Reach Truck", "Vehicle for reaching high storage locations"),
    CONVEYOR("Conveyor", "System for moving materials continuously"),
    SCANNER("Scanner", "Barcode or RFID scanning device"),
    PRINTER("Printer", "Label or document printer"),
    SCALE("Scale", "Weighing device"),
    PACKING_STATION("Packing Station", "Workspace for packing orders"),
    MOBILE_COMPUTER("Mobile Computer", "Handheld computer for warehouse tasks"),
    TABLET("Tablet", "Tablet device for warehouse tasks"),
    PACKING_MACHINE("Packing Machine", "Automated packing equipment"),
    WRAPPING_MACHINE("Wrapping Machine", "Machine for wrapping pallets or packages"),
    SORTATION_SYSTEM("Sortation System", "System for sorting items"),
    ROBOT("Robot", "Automated robotic system"),
    CART("Cart", "Manual cart for moving items"),
    BIN("Bin", "Storage container"),
    RACK("Rack", "Storage rack or shelving"),
    DOCK_EQUIPMENT("Dock Equipment", "Equipment used at loading docks"),
    SAFETY_EQUIPMENT("Safety Equipment", "Equipment for ensuring safety");

    private final String displayName;
    private final String description;

    public boolean requiresCertification() {
        return this == FORKLIFT || this == ORDER_PICKER || 
               this == REACH_TRUCK;
    }

    public boolean isPowered() {
        return this == FORKLIFT || this == ORDER_PICKER || 
               this == REACH_TRUCK || this == CONVEYOR || 
               this == PACKING_MACHINE || this == WRAPPING_MACHINE || 
               this == SORTATION_SYSTEM || this == ROBOT;
    }

    public boolean isPortable() {
        return this == SCANNER || this == PRINTER || 
               this == MOBILE_COMPUTER || this == TABLET || 
               this == SCALE || this == CART || this == BIN;
    }

    public boolean isMajorEquipment() {
        return this == FORKLIFT || this == ORDER_PICKER || 
               this == REACH_TRUCK || this == CONVEYOR || 
               this == PACKING_MACHINE || this == WRAPPING_MACHINE || 
               this == SORTATION_SYSTEM || this == ROBOT;
    }

    public boolean isVehicle() {
        return this == FORKLIFT || this == ORDER_PICKER || 
               this == REACH_TRUCK;
    }

    public boolean requiresBatteryManagement() {
        return this == FORKLIFT || this == ORDER_PICKER || 
               this == REACH_TRUCK || this == MOBILE_COMPUTER || 
               this == TABLET || this == ROBOT;
    }

    public boolean requiresRegularMaintenance() {
        return isPowered() || isVehicle();
    }

    public boolean isFixedAsset() {
        return this == CONVEYOR || this == PACKING_STATION || 
               this == PACKING_MACHINE || this == WRAPPING_MACHINE || 
               this == SORTATION_SYSTEM || this == RACK || 
               this == DOCK_EQUIPMENT;
    }

    public boolean canBeAssignedToStaff() {
        return this == FORKLIFT || this == PALLET_JACK || 
               this == ORDER_PICKER || this == REACH_TRUCK || 
               this == SCANNER || this == MOBILE_COMPUTER || 
               this == TABLET || this == CART;
    }
}
