package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.entity.InventoryMovement;
import com.smartusers.logitrackapi.enums.MovementType;
import com.smartusers.logitrackapi.repository.InventoryMovementRepository;
import com.smartusers.logitrackapi.repository.InventoryRepository;
import com.smartusers.logitrackapi.repository.ProductRepository;
import com.smartusers.logitrackapi.repository.WarehouseRepository;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    @Override
    public Inventory create(Inventory inventory) {
        var product = productRepository.findById(inventory.getProduct().getId())
                .orElseThrow(() -> new BusinessException("Produit non trouvé"));
        var warehouse = warehouseRepository.findById(inventory.getWarehouse().getId())
                .orElseThrow(() -> new BusinessException("Entrepôt non trouvé"));

        if (!warehouse.getActive()) {
            throw new BusinessException("Entrepôt inactif");
        }

        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);

        List<Inventory> existingInventories = inventoryRepository
                .findAllByWarehouse_IdAndProduct_Id(warehouse.getId(), product.getId());

        if (!existingInventories.isEmpty()) {
            throw new BusinessException("Ce produit existe déjà dans cet entrepôt !");
        }

        if (inventory.getQuantityOnHand() == null) {
            inventory.setQuantityOnHand(0);
        }
        inventory.setQuantityReserved(0);

        Inventory savedInventory = inventoryRepository.save(inventory);

        if (savedInventory.getQuantityOnHand() > 0) {
            saveMovement(savedInventory, MovementType.INBOUND, savedInventory.getQuantityOnHand(),
                    "Initialisation du stock avec " + savedInventory.getQuantityOnHand() + " unités");
        }

        return savedInventory;
    }

    @Override
    public Inventory addStock(Long inventoryId, int quantity, String description) {
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive");
        }

        Inventory inventory = getById(inventoryId);

        if (!inventory.getWarehouse().getActive()) {
            throw new BusinessException("Entrepôt inactif");
        }

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        saveMovement(updatedInventory, MovementType.INBOUND, quantity,
                description != null ? description : "Réception de " + quantity + " unités");

        return updatedInventory;
    }

    @Override
    public Inventory sortieStock(Long inventoryId, int quantity, String description) {
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive");
        }

        Inventory inventory = getById(inventoryId);

        if (!inventory.getWarehouse().getActive()) {
            throw new BusinessException("Entrepôt inactif");
        }

        int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();

        if (available < quantity) {
            throw new BusinessException(
                    "Stock insuffisant dans l'entrepôt " + inventory.getWarehouse().getName() +
                            ". Disponible: " + available + ", Demandé: " + quantity
            );
        }

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        saveMovement(updatedInventory, MovementType.OUTBOUND, quantity,
                description != null ? description : "Sortie de " + quantity + " unités");

        return updatedInventory;
    }

    @Override
    public void reserveStock(Long inventoryId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive");
        }

        Inventory inventory = getById(inventoryId);
        int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();

        if (available < quantity) {
            throw new BusinessException("Stock disponible insuffisant pour réservation");
        }

        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void unreserveStock(Long inventoryId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive");
        }

        Inventory inventory = getById(inventoryId);

        if (inventory.getQuantityReserved() < quantity) {
            throw new BusinessException("Quantité réservée insuffisante");
        }

        inventory.setQuantityReserved(inventory.getQuantityReserved() - quantity);
        inventoryRepository.save(inventory);
    }

    private void saveMovement(Inventory inventory, MovementType type, int quantity, String description) {
        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .type(type)
                .quantity(quantity)
                .movementDate(LocalDateTime.now())
                .description(description)
                .build();
        inventoryMovementRepository.save(movement);
    }

    @Override
    public Integer checkAvailableByWarehouse(Long productId, Long warehouseId) {
        List<Inventory> inventories = inventoryRepository
                .findAllByWarehouse_IdAndProduct_Id(warehouseId, productId);

        return inventories.stream()
                .mapToInt(inv -> Math.max(0, inv.getQuantityOnHand() - inv.getQuantityReserved()))
                .sum();
    }

    @Override
    public Integer checkAvailableInAllWarehouses(Long productId) {
        List<Inventory> list = inventoryRepository.findAllAvailableByProduct(productId);

        return list.stream()
                .filter(inv -> inv.getWarehouse().getActive())
                .mapToInt(i -> Math.max(0, i.getQuantityOnHand() - i.getQuantityReserved()))
                .sum();
    }

    @Override
    public Inventory update(Long id, Inventory inventory) {
        Inventory existing = getById(id);
        existing.setQuantityOnHand(inventory.getQuantityOnHand());
        existing.setQuantityReserved(inventory.getQuantityReserved());
        return inventoryRepository.save(existing);
    }

    @Override
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory getById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Inventaire non trouvé avec ID " + id));
    }

    @Override
    public void delete(Long id) {
        Inventory inventory = getById(id);

        if (inventory.getQuantityReserved() > 0) {
            throw new BusinessException("Impossible de supprimer un inventaire avec stock réservé");
        }

        inventoryRepository.deleteById(id);
    }
}