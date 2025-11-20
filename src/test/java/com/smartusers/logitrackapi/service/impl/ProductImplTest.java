package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.dto.Product.ProductResponse;
import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.entity.SalesOrderLine;
import com.smartusers.logitrackapi.enums.OrderStatus;
import com.smartusers.logitrackapi.mapper.ProductMapper;
import com.smartusers.logitrackapi.repository.InventoryRepository;
import com.smartusers.logitrackapi.repository.ProductRepository;
import com.smartusers.logitrackapi.repository.SalesOrderLineRepository;
import com.smartusers.logitrackapi.repository.SalesOrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @InjectMocks
    private ProductImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProductSuccess() {
        Product product = new Product();
        product.setSku("SKU123");

        when(productRepository.existsBySku("SKU123")).thenReturn(false);
        when(productRepository.save(product)).thenReturn(product);

        Product created = productService.createProduct(product);

        assertNotNull(created);
        assertEquals("SKU123", created.getSku());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testCreateProductDuplicateSku() {
        Product product = new Product();
        product.setSku("SKU123");

        when(productRepository.existsBySku("SKU123")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(product);
        });

        assertEquals("Le SKU existe déjà : SKU123", ex.getMessage());
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product(); p1.setName("A");
        Product p2 = new Product(); p2.setName("B");

        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductByIdFound() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void testUpdateProductSuccess() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSku("OLD");

        Product updated = new Product();
        updated.setSku("NEW");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("NEW")).thenReturn(false);
        when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.updateProduct(1L, updated);

        assertEquals("NEW", result.getSku());
        verify(productRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateProductDuplicateSku() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSku("OLD");

        Product updated = new Product();
        updated.setSku("NEW");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("NEW")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(1L, updated);
        });

        assertEquals("Le SKU existe déjà : NEW", ex.getMessage());
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchProductsByName() {
        Product p = new Product();
        p.setName("Phone");

        when(productRepository.findByNameContainingIgnoreCase("pho"))
                .thenReturn(List.of(p));

        List<Product> result = productService.searchProductsByName("pho");

        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getName());
    }

    @Test
    void testDesactivateProductSuccess() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU1");
        product.setActive(true);

        Inventory inv = new Inventory();
        inv.setQuantityReserved(0);

        when(productRepository.findBySku("SKU1")).thenReturn(product);
        when(inventoryRepository.findAllByProduct_Id(1L)).thenReturn(List.of(inv));
        when(salesOrderLineRepository.findAllByProduct_Id(1L)).thenReturn(List.of());

        ProductResponse response = productService.desactivitedProduct("SKU1");

        assertFalse(response.getActive());
    }

    @Test
    void testDesactivateProductReservedStock() {
        Product product = new Product();
        product.setId(1L);

        Inventory inv = new Inventory();
        inv.setQuantityReserved(5);

        when(productRepository.findBySku("SKU1")).thenReturn(product);
        when(inventoryRepository.findAllByProduct_Id(1L)).thenReturn(List.of(inv));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.desactivitedProduct("SKU1");
        });

        assertEquals("Impossible de désactiver le produit car il y a des quantités réservées en stock.", ex.getMessage());
    }

    @Test
    void testDesactivateProductWithUndeliveredOrder() {
        Product product = new Product();
        product.setId(1L);

        SalesOrder order = new SalesOrder();
        order.setId(10L);
        order.setStatus(OrderStatus.DELIVERED);

        SalesOrderLine line = new SalesOrderLine();
        line.setSalesOrder(order);

        when(productRepository.findBySku("SKU1")).thenReturn(product);
        when(inventoryRepository.findAllByProduct_Id(1L)).thenReturn(List.of());
        when(salesOrderLineRepository.findAllByProduct_Id(1L)).thenReturn(List.of(line));
        when(salesOrderRepository.findById(10L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.desactivitedProduct("SKU1");
        });

        assertEquals("Impossible de désactiver le produit car il est associé à des commandes non livrées.", ex.getMessage());
    }
}
