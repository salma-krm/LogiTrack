package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.enums.Role;
import com.smartusers.logitrackapi.entity.Warehouse;
import com.smartusers.logitrackapi.enums.Role;
import com.smartusers.logitrackapi.repository.UserRepository;
import com.smartusers.logitrackapi.repository.WarehouseRepository;
import com.smartusers.logitrackapi.service.interfaces.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Override
    public Warehouse create(Warehouse warehouse) {
        if (warehouseRepository.existsByName(warehouse.getName())) {
            throw new RuntimeException("Le nom du warehouse existe déjà : " + warehouse.getName());
        }

        if (warehouse.getManager() != null) {
            Long managerId = warehouse.getManager().getId();
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Le manager avec ID " + managerId + " n'existe pas."));

            if (manager.getRole() != Role.MANAGER) {
                throw new RuntimeException("L'utilisateur avec ID " + managerId + " n'est pas un manager.");
            }

            warehouse.setManager(manager);
        }

        return warehouseRepository.save(warehouse);
    }

    @Override
    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse non trouvé avec l'ID : " + id));
    }

    @Override
    public Warehouse update(Long id, Warehouse warehouse) {
        Warehouse existing = getById(id);

        if (warehouseRepository.existsByName(warehouse.getName()) &&
                !existing.getName().equalsIgnoreCase(warehouse.getName())) {
            throw new RuntimeException("Le nom du warehouse existe déjà : " + warehouse.getName());
        }

        existing.setName(warehouse.getName());
        existing.setActive(warehouse.getActive());

        if (warehouse.getManager() != null) {
            Long managerId = warehouse.getManager().getId();
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Le manager avec ID " + managerId + " n'existe pas."));

            if (manager.getRole() != Role.MANAGER) {
                throw new RuntimeException("L'utilisateur avec ID " + managerId + " n'est pas un manager.");
            }

            existing.setManager(manager);
        } else {
            existing.setManager(null);
        }

        return warehouseRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Warehouse existing = getById(id);
        warehouseRepository.delete(existing);
    }
}
