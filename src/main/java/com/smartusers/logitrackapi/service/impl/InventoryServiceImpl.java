package com.smartusers.logitrackapi.service.impl;

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
import java.util.Optional;

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
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        var warehouse = warehouseRepository.findById(inventory.getWarehouse().getId())
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé"));
        if (!warehouse.getActive()) throw new RuntimeException("Entrepôt inactif");

        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);

        Optional<Inventory> existingInventory = inventoryRepository.findByWarehouse_IdAndProduct_Id(
                warehouse.getId(), product.getId()
        );
        if (existingInventory.isPresent())
            throw new RuntimeException("Ce produit existe déjà dans cet entrepôt !");

        if (inventory.getQuantityOnHand() == null) inventory.setQuantityOnHand(0);
        inventory.setQuantityReserved(0);

        Inventory savedInventory = inventoryRepository.save(inventory);

        if (savedInventory.getQuantityOnHand() > 0) {
            InventoryMovement movement = InventoryMovement.builder()
                    .inventory(savedInventory)
                    .type(MovementType.INBOUND)
                    .quantity(savedInventory.getQuantityOnHand())
                    .movementDate(LocalDateTime.now())
                    .description("Initialisation du stock avec " + savedInventory.getQuantityOnHand() + " unités")
                    .build();
            inventoryMovementRepository.save(movement);
        }

        return savedInventory;
    }

    @Override
    public Inventory addStock(Long inventoryId, int quantity, String description) {
        Inventory inventory = getById(inventoryId);
        if (!inventory.getWarehouse().getActive())
            throw new RuntimeException("Entrepôt inactif");

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(updatedInventory)
                .type(MovementType.INBOUND)
                .quantity(quantity)
                .movementDate(LocalDateTime.now())
                .description(description != null ? description : "Réception de " + quantity + " unités")
                .build();
        inventoryMovementRepository.save(movement);

        return updatedInventory;
    }

    @Override
    public Inventory sortieStock(Long inventoryId, int quantity, String description) {
        Inventory inventory = getById(inventoryId);
        if (!inventory.getWarehouse().getActive())
            throw new RuntimeException("Entrepôt inactif");

        int availableStock = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
        if (availableStock < quantity)
            throw new RuntimeException("Stock insuffisant : disponible = " + availableStock + ", demandé = " + quantity);

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(updatedInventory)
                .type(MovementType.OUTBOUND)
                .quantity(quantity)
                .movementDate(LocalDateTime.now())
                .description(description != null ? description : "Sortie de " + quantity + " unités")
                .build();
        inventoryMovementRepository.save(movement);

        return updatedInventory;
    }

    @Override
    public Integer checkAvailableByWarehouse(Long productId, Long warehouseId) {
        Inventory inv = inventoryRepository.findByWarehouse_IdAndProduct_Id(warehouseId, productId)
                .orElse(null);
        if (inv == null) return 0;
        return inv.getQuantityOnHand() - inv.getQuantityReserved();
    }

    @Override
    public Integer checkAvailableInAllWarehouses(Long productId) {
        List<Inventory> list = inventoryRepository.findAllAvailableByProduct(productId);
        return list.stream().mapToInt(i -> i.getQuantityOnHand() - i.getQuantityReserved()).sum();
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
                .orElseThrow(() -> new RuntimeException("Inventaire non trouvé avec ID " + id));
    }

    @Override
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}
