package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.entity.*;
import com.smartusers.logitrackapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;
    private WarehouseRepository warehouseRepository;
    private InventoryMovementRepository movementRepository;

    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setup() {
        inventoryRepository = mock(InventoryRepository.class);
        productRepository = mock(ProductRepository.class);
        warehouseRepository = mock(WarehouseRepository.class);
        movementRepository = mock(InventoryMovementRepository.class);

        inventoryService = new InventoryServiceImpl(
                inventoryRepository,
                productRepository,
                warehouseRepository,
                movementRepository
        );
    }

    @Test
    void testCreateInventory_Success() {
        Product product = Product.builder().id(1L).build();
        Warehouse warehouse = Warehouse.builder().id(1L).active(true).build();

        Inventory inv = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .quantityOnHand(10)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findAllByWarehouse_IdAndProduct_Id(1L, 1L)).thenReturn(List.of());
        when(inventoryRepository.save(inv)).thenReturn(inv);

        Inventory result = inventoryService.create(inv);

        assertEquals(10, result.getQuantityOnHand());
        verify(movementRepository, times(1)).save(any(InventoryMovement.class));
    }

    @Test
    void testCreateInventory_WarehouseInactive() {
        Product p = Product.builder().id(1L).build();
        Warehouse w = Warehouse.builder().id(1L).active(false).build();

        Inventory inv = Inventory.builder().product(p).warehouse(w).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(w));

        assertThrows(BusinessException.class, () -> inventoryService.create(inv));
    }

    @Test
    void testCreateInventory_Duplicate_ThrowsException() {
        Product p = Product.builder().id(1L).build();
        Warehouse w = Warehouse.builder().id(1L).active(true).build();

        Inventory inv = Inventory.builder().product(p).warehouse(w).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(w));
        when(inventoryRepository.findAllByWarehouse_IdAndProduct_Id(1L, 1L))
                .thenReturn(List.of(Inventory.builder().build()));

        assertThrows(BusinessException.class, () -> inventoryService.create(inv));
    }

    @Test
    void testAddStock_Success() {
        Warehouse w = Warehouse.builder().active(true).build();
        Inventory inv = Inventory.builder().id(1L).quantityOnHand(5).warehouse(w).build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventoryRepository.save(any())).thenReturn(inv);

        Inventory result = inventoryService.addStock(1L, 5, "Ajout");

        assertEquals(10, result.getQuantityOnHand());
        verify(movementRepository, times(1)).save(any());
    }

    @Test
    void testAddStock_InvalidQuantity() {
        assertThrows(BusinessException.class, () -> inventoryService.addStock(1L, -2, null));
    }

    @Test
    void testSortieStock_Success() {
        Warehouse w = Warehouse.builder().active(true).build();
        Inventory inv = Inventory.builder()
                .id(1L)
                .quantityOnHand(10)
                .quantityReserved(2)
                .warehouse(w)
                .build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventoryRepository.save(any())).thenReturn(inv);

        Inventory result = inventoryService.sortieStock(1L, 5, "Sortie");

        assertEquals(5, result.getQuantityOnHand());
        verify(movementRepository, times(1)).save(any());
    }

    @Test
    void testSortieStock_InsufficientStock() {
        Warehouse w = Warehouse.builder().active(true).build();
        Inventory inv = Inventory.builder()
                .quantityOnHand(5)
                .quantityReserved(3)
                .warehouse(w)
                .build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));

        assertThrows(BusinessException.class,
                () -> inventoryService.sortieStock(1L, 5, null));
    }

    @Test
    void testReserveStock_Success() {
        Inventory inv = Inventory.builder().quantityOnHand(10).quantityReserved(3).build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));

        inventoryService.reserveStock(1L, 5);

        assertEquals(8, inv.getQuantityReserved());
    }

    @Test
    void testReserveStock_Insufficient() {
        Inventory inv = Inventory.builder().quantityOnHand(5).quantityReserved(4).build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));

        assertThrows(BusinessException.class,
                () -> inventoryService.reserveStock(1L, 3));
    }

    @Test
    void testDeleteInventory_WithReservedStock_Throws() {
        Inventory inv = Inventory.builder().quantityReserved(5).build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));

        assertThrows(BusinessException.class, () -> inventoryService.delete(1L));
    }

    @Test
    void testDeleteInventory_Success() {
        Inventory inv = Inventory.builder().quantityReserved(0).build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));

        inventoryService.delete(1L);

        verify(inventoryRepository, times(1)).deleteById(1L);
    }
}
