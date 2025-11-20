package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.entity.Warehouse;
import com.smartusers.logitrackapi.enums.Role;
import com.smartusers.logitrackapi.repository.UserRepository;
import com.smartusers.logitrackapi.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private User manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        manager = new User();
        manager.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
        warehouse.setActive(true);
        warehouse.setManager(manager);
    }

    @Test
    void testCreateWarehouse_Success() {
        manager.setRole(Role.MANAGER);

        when(warehouseRepository.existsByName(warehouse.getName())).thenReturn(false);
        when(userRepository.findById(manager.getId())).thenReturn(Optional.of(manager));
        when(warehouseRepository.save(any())).thenReturn(warehouse);

        Warehouse result = warehouseService.create(warehouse);

        assertNotNull(result);
        assertEquals("Main Warehouse", result.getName());
        assertEquals(manager, result.getManager());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void testCreateWarehouse_NameExists() {
        when(warehouseRepository.existsByName(warehouse.getName())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> warehouseService.create(warehouse));
        assertTrue(ex.getMessage().contains("Le nom du warehouse existe déjà"));
    }

    @Test
    void testCreateWarehouse_ManagerNotFound() {
        when(warehouseRepository.existsByName(warehouse.getName())).thenReturn(false);
        when(userRepository.findById(manager.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> warehouseService.create(warehouse));
        assertTrue(ex.getMessage().contains("Le manager avec ID"));
    }

    @Test
    void testCreateWarehouse_ManagerNotValidRole() {
        // Ici on met un rôle invalide pour déclencher l'exception
        manager.setRole(Role.ADMIN); // rôle autre que MANAGER
        when(warehouseRepository.existsByName(warehouse.getName())).thenReturn(false);
        when(userRepository.findById(manager.getId())).thenReturn(Optional.of(manager));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> warehouseService.create(warehouse));
        assertTrue(ex.getMessage().contains("n'est pas un manager"));
    }

    @Test
    void testGetAllWarehouses() {
        Warehouse warehouse2 = new Warehouse();
        warehouse2.setId(2L);
        warehouse2.setName("Secondary Warehouse");

        when(warehouseRepository.findAll()).thenReturn(Arrays.asList(warehouse, warehouse2));

        List<Warehouse> list = warehouseService.getAll();
        assertEquals(2, list.size());
        assertEquals("Main Warehouse", list.get(0).getName());
        assertEquals("Secondary Warehouse", list.get(1).getName());
    }

    @Test
    void testGetById_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        Warehouse result = warehouseService.getById(1L);
        assertNotNull(result);
        assertEquals("Main Warehouse", result.getName());
    }

    @Test
    void testGetById_NotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> warehouseService.getById(1L));
        assertTrue(ex.getMessage().contains("Warehouse non trouvé"));
    }

    @Test
    void testUpdateWarehouse_Success() {
        Warehouse updateData = new Warehouse();
        updateData.setName("Updated Warehouse");
        updateData.setActive(false);
        updateData.setManager(manager);
        manager.setRole(Role.MANAGER);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByName(updateData.getName())).thenReturn(false);
        when(userRepository.findById(manager.getId())).thenReturn(Optional.of(manager));
        when(warehouseRepository.save(any())).thenReturn(updateData);

        Warehouse result = warehouseService.update(1L, updateData);

        assertEquals("Updated Warehouse", result.getName());
        assertFalse(result.getActive());
        assertEquals(manager, result.getManager());
    }

    @Test
    void testUpdateWarehouse_NameExists() {
        Warehouse updateData = new Warehouse();
        updateData.setName("Existing Warehouse");

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByName(updateData.getName())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> warehouseService.update(1L, updateData));
        assertTrue(ex.getMessage().contains("Le nom du warehouse existe déjà"));
    }

    @Test
    void testDeleteWarehouse() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseRepository).delete(warehouse);

        warehouseService.delete(1L);

        verify(warehouseRepository, times(1)).delete(warehouse);
    }
}
