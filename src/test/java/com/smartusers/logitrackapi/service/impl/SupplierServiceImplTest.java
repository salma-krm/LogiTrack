package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.dto.supplier.SupplierRequest;
import com.smartusers.logitrackapi.entity.Supplier;
import com.smartusers.logitrackapi.mapper.SupplierMapper;
import com.smartusers.logitrackapi.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;
    private SupplierRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        supplier.setActive(true);

        request = new SupplierRequest();
        request.setName("Supplier A");
        request.setActive(true);
    }

    @Test
    void testCreateSupplier() {
        when(supplierRepository.existsByName("Supplier A")).thenReturn(false);
        when(supplierMapper.toEntity(request)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier created = supplierService.create(request);

        assertNotNull(created);
        assertEquals("Supplier A", created.getName());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void testCreateSupplierAlreadyExists() {
        when(supplierRepository.existsByName("Supplier A")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> supplierService.create(request));
        assertEquals("Un fournisseur avec ce nom existe déjà !", ex.getMessage());
    }

    @Test
    void testUpdateSupplier() {
        SupplierRequest updateRequest = new SupplierRequest();
        updateRequest.setName("Supplier B");
        updateRequest.setActive(false);

        Supplier updatedSupplier = new Supplier();
        updatedSupplier.setId(1L);
        updatedSupplier.setName("Supplier B");
        updatedSupplier.setActive(false);

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByName("Supplier B")).thenReturn(false);
        doAnswer(invocation -> {
            supplier.setName("Supplier B");
            supplier.setActive(false);
            return null;
        }).when(supplierMapper).updateEntityFromRequest(updateRequest, supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier result = supplierService.update(1L, updateRequest);

        assertEquals("Supplier B", result.getName());
        assertFalse(result.getActive());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void testGetByIdFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        Supplier found = supplierService.getById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetByIdNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> supplierService.getById(1L));
        assertEquals("Fournisseur non trouvé avec ID: 1", ex.getMessage());
    }

    @Test
    void testGetAll() {
        Supplier s2 = new Supplier();
        s2.setId(2L);
        s2.setName("Supplier B");

        when(supplierRepository.findAll()).thenReturn(Arrays.asList(supplier, s2));

        List<Supplier> list = supplierService.getAll();

        assertEquals(2, list.size());
        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    void testGetAllWithPageable() {
        Page<Supplier> page = new PageImpl<>(List.of(supplier));
        when(supplierRepository.findAll(Pageable.unpaged())).thenReturn(page);

        Page<Supplier> result = supplierService.getAll(Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        verify(supplierRepository, times(1)).findAll(Pageable.unpaged());
    }

    @Test
    void testGetActiveSuppliers() {
        when(supplierRepository.findByActiveTrue()).thenReturn(List.of(supplier));

        List<Supplier> active = supplierService.getActiveSuppliers();

        assertEquals(1, active.size());
        verify(supplierRepository, times(1)).findByActiveTrue();
    }

    @Test
    void testActivateSupplier() {
        supplier.setActive(false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier result = supplierService.activate(1L);

        assertTrue(result.getActive());
    }

    @Test
    void testDeactivateSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier result = supplierService.deactivate(1L);

        assertFalse(result.getActive());
    }

    @Test
    void testDeleteSupplier() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(1L);

        supplierService.delete(1L);

        verify(supplierRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSupplierNotFound() {
        when(supplierRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> supplierService.delete(1L));
        assertEquals("Fournisseur non trouvé avec ID: 1", ex.getMessage());
    }
}
