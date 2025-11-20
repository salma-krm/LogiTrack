package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderLineRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderLineRequest;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.entity.*;
import com.smartusers.logitrackapi.enums.OrderStatus;
import com.smartusers.logitrackapi.repository.*;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import com.smartusers.logitrackapi.service.interfaces.PurchaseOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesOrderServiceImplTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SalesOrderServiceImpl salesOrderService;

    private User client;
    private Warehouse warehouse;
    private Product product;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new User();
        client.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setActive(true);

        product = new Product();
        product.setId(1L);

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setActive(true);
    }

    @Test
    void testCreateSalesOrder() {
        SalesOrderRequest request = new SalesOrderRequest();
        request.setClientId(1L);
        request.setWarehouseId(1L);

        SalesOrderLineRequest lineReq = new SalesOrderLineRequest(
                1L, 5, new BigDecimal("50.0")
        );
        request.setOrderLines(Collections.singletonList(lineReq));

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(salesOrderRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        when(salesOrderLineRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(salesOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SalesOrder order = salesOrderService.create(request);

        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(1, order.getLines().size());
        assertEquals(product.getId(), order.getLines().get(0).getProduct().getId());
    }

    @Test
    void testConfirmerOrderByClient_AllStockAvailable() {
        SalesOrder order = new SalesOrder();
        order.setId(1L);
        order.setWarehouse(warehouse);
        order.setLines(new ArrayList<>());

        SalesOrderLine line = new SalesOrderLine();
        line.setProduct(product);
        line.setQtyOrdered(5);
        line.setQtyReserved(0);
        line.setPrice(new BigDecimal("50.0"));
        order.getLines().add(line);

        when(salesOrderRepository.findWithDetailsById(1L)).thenReturn(Optional.of(order));

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);
        inventory.setQuantityOnHand(10);
        inventory.setQuantityReserved(0);

        when(inventoryRepository.findAllByWarehouse_IdAndProduct_Id(1L, 1L))
                .thenReturn(Collections.singletonList(inventory));

        SalesOrder confirmedOrder = salesOrderService.confirmerOrderByClient(1L);

        assertEquals(OrderStatus.RESERVED, confirmedOrder.getStatus());
        assertEquals(5, confirmedOrder.getLines().get(0).getQtyReserved());
        verify(inventoryService, times(1))
                .sortieStock(inventory.getId(), 5, "Réservation commande 1 - produit " + product.getName());
    }

    @Test
    void testConfirmerOrderByClient_NotEnoughStock_CreatesPurchaseOrder() {
        SalesOrder order = new SalesOrder();
        order.setId(1L);
        order.setWarehouse(warehouse);
        order.setLines(new ArrayList<>());

        SalesOrderLine line = new SalesOrderLine();
        line.setProduct(product);
        line.setQtyOrdered(10);
        line.setQtyReserved(0);
        line.setPrice(new BigDecimal("50.0"));
        order.getLines().add(line);

        when(salesOrderRepository.findWithDetailsById(1L)).thenReturn(Optional.of(order));

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);
        inventory.setQuantityOnHand(5);
        inventory.setQuantityReserved(0);

        when(inventoryRepository.findAllByWarehouse_IdAndProduct_Id(1L, 1L))
                .thenReturn(Collections.singletonList(inventory));

        when(supplierRepository.findByActiveTrue())
                .thenReturn(Collections.singletonList(supplier));

        when(purchaseOrderService.create(any(PurchaseOrderRequest.class)))
                .thenReturn(null);

        SalesOrder confirmedOrder = salesOrderService.confirmerOrderByClient(1L);

        assertEquals(OrderStatus.RESERVED, confirmedOrder.getStatus());
        assertEquals(10, confirmedOrder.getLines().get(0).getQtyReserved());
        verify(purchaseOrderService, times(1)).create(any(PurchaseOrderRequest.class));
        verify(inventoryService, times(2)).sortieStock(anyLong(), anyInt(), anyString());
        verify(inventoryService, times(1)).addStock(anyLong(), anyInt(), anyString());
    }

    @Test
    void testCreateSalesOrder_ClientNotFound() {
        SalesOrderRequest request = new SalesOrderRequest();
        request.setClientId(99L);
        request.setWarehouseId(1L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> salesOrderService.create(request));
        assertEquals("Client non trouvé", ex.getMessage());
    }
}
