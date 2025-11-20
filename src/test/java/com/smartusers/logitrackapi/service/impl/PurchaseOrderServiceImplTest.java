package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderLineRequest;
import com.smartusers.logitrackapi.entity.*;
import com.smartusers.logitrackapi.enums.POStatus;
import com.smartusers.logitrackapi.repository.*;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseOrderServiceImplTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private POLineRepository poLineRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private PurchaseOrderServiceImpl poService;

    private Supplier supplier;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setActive(true);

        product = new Product();
        product.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setActive(true);
    }

    @Test
    void testCreatePurchaseOrder() {
        PurchaseOrderRequest request = new PurchaseOrderRequest();
        request.setSupplierId(1L);

        PurchaseOrderLineRequest lineReq = new PurchaseOrderLineRequest(
                1L,                     // productId
                10,                     // quantity
                new BigDecimal("100.0") // unitPrice
        );
        request.setOrderLines(Collections.singletonList(lineReq));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        when(poLineRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseOrder po = poService.create(request);

        assertNotNull(po);
        assertEquals(POStatus.CREATED, po.getStatus());
        assertEquals(1, po.getLines().size());
    }

    @Test
    void testCreatePurchaseOrderSupplierInactive() {
        supplier.setActive(false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        PurchaseOrderRequest request = new PurchaseOrderRequest();
        request.setSupplierId(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> poService.create(request));
        assertEquals("Fournisseur inactif", ex.getMessage());
    }

    @Test
    void testApprovePurchaseOrder() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.CREATED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseOrder result = poService.approve(1L);
        assertEquals(POStatus.APPROVED, result.getStatus());
    }

    @Test
    void testApprovePurchaseOrderInvalidStatus() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.APPROVED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));

        BusinessException ex = assertThrows(BusinessException.class, () -> poService.approve(1L));
        assertTrue(ex.getMessage().contains("Seules les commandes en statut CREATED peuvent être approuvées"));
    }

    @Test
    void testReceivePurchaseOrderWithExistingInventory() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.APPROVED);

        POLine line = new POLine();
        line.setProduct(product);
        line.setQuantityOrdered(10);
        po.setLines(Collections.singletonList(line));

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);
        inventory.setQuantityOnHand(5);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouse_IdAndProduct_Id(1L, 1L)).thenReturn(Optional.of(inventory));
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseOrder result = poService.receive(1L, 1L);

        assertEquals(POStatus.RECEIVED, result.getStatus());
        verify(inventoryService, times(1)).addStock(1L, 10, "Réception commande achat #1");
    }

    @Test
    void testCancelPurchaseOrder() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.CREATED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseOrder result = poService.cancel(1L);
        assertEquals(POStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testDeletePurchaseOrderAllowed() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.CREATED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        doNothing().when(purchaseOrderRepository).deleteById(1L);

        poService.delete(1L);

        verify(purchaseOrderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePurchaseOrderNotAllowed() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.RECEIVED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));

        BusinessException ex = assertThrows(BusinessException.class, () -> poService.delete(1L));
        assertTrue(ex.getMessage().contains("Seules les commandes en statut CREATED ou CANCELLED peuvent être supprimées"));
    }
}
