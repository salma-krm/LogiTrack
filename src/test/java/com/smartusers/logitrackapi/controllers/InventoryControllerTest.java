package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.inventory.InventoryRequest;
import com.smartusers.logitrackapi.dto.inventory.InventoryResponse;
import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.entity.Warehouse;
import com.smartusers.logitrackapi.mapper.InventoryMapper;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory inventory;
    private InventoryRequest inventoryRequest;
    private InventoryResponse inventoryResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Product product = new Product();
        product.setId(1L);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(10);
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);

        inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);
        inventoryRequest.setWarehouseId(1L);
        inventoryRequest.setQuantityOnHand(100);
        inventoryRequest.setQuantityReserved(10);

        inventoryResponse = new InventoryResponse();
        inventoryResponse.setId(1L);
        inventoryResponse.setQuantityOnHand(100);
        inventoryResponse.setQuantityReserved(10);
        inventoryResponse.setProductId(1L);
        inventoryResponse.setWarehouseId(1L);
    }

    @Test
    void testCreateInventory() {
        when(inventoryMapper.toEntity(inventoryRequest)).thenReturn(inventory);
        when(inventoryService.create(inventory)).thenReturn(inventory);
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        InventoryResponse response = inventoryController.create(inventoryRequest);

        assertNotNull(response);
        assertEquals(100, response.getQuantityOnHand());
        assertEquals(10, response.getQuantityReserved());
        verify(inventoryService, times(1)).create(inventory);
    }

    @Test
    void testUpdateInventory() {
        when(inventoryMapper.toEntity(inventoryRequest)).thenReturn(inventory);
        when(inventoryService.update(1L, inventory)).thenReturn(inventory);
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        InventoryResponse response = inventoryController.update(1L, inventoryRequest);

        assertNotNull(response);
        assertEquals(100, response.getQuantityOnHand());
        verify(inventoryService, times(1)).update(1L, inventory);
    }

    @Test
    void testGetAllInventories() {
        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setQuantityOnHand(50);
        inventory2.setQuantityReserved(5);

        InventoryResponse response2 = new InventoryResponse();
        response2.setId(2L);
        response2.setQuantityOnHand(50);
        response2.setQuantityReserved(5);

        when(inventoryService.getAll()).thenReturn(Arrays.asList(inventory, inventory2));
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);
        when(inventoryMapper.toResponse(inventory2)).thenReturn(response2);

        List<InventoryResponse> list = inventoryController.getAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void testGetById() {
        when(inventoryService.getById(1L)).thenReturn(inventory);
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        InventoryResponse response = inventoryController.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(inventoryService, times(1)).getById(1L);
    }

    @Test
    void testDeleteInventory() {
        doNothing().when(inventoryService).delete(1L);
        inventoryController.delete(1L);
        verify(inventoryService, times(1)).delete(1L);
    }
}
