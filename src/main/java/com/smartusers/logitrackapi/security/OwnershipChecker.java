package com.smartusers.logitrackapi.security;

import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.entity.Shipment;
import com.smartusers.logitrackapi.repository.SalesOrderRepository;
import com.smartusers.logitrackapi.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Service pour vérifier l'ownership (propriété) des ressources
 * Un CLIENT ne peut accéder qu'à ses propres commandes et expéditions
 */
@Component("ownershipChecker")
@RequiredArgsConstructor
@Slf4j
public class OwnershipChecker {

    private final SalesOrderRepository salesOrderRepository;
    private final ShipmentRepository shipmentRepository;


    public boolean isOrderOwner(Long orderId, Authentication auth) {
        if (auth == null || orderId == null) {
            log.warn("Authentication or orderId is null");
            return false;
        }

        String email = auth.getName();
        return salesOrderRepository.findById(orderId)
                .map(order -> {
                    boolean isOwner = order.getClient().getEmail().equals(email);
                    log.debug("User '{}' ownership check for order {}: {}", email, orderId, isOwner);
                    return isOwner;
                })
                .orElse(false);
    }


    public boolean isShipmentOwner(Long shipmentId, Authentication auth) {
        if (auth == null || shipmentId == null) {
            log.warn("Authentication or shipmentId is null");
            return false;
        }

        String email = auth.getName();
        return shipmentRepository.findById(shipmentId)
                .map(shipment -> {
                    boolean isOwner = shipment.getSalesOrder().getClient().getEmail().equals(email);
                    log.debug("User '{}' ownership check for shipment {}: {}", email, shipmentId, isOwner);
                    return isOwner;
                })
                .orElse(false);
    }
}

