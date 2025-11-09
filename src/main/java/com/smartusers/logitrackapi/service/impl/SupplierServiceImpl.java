package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.dto.supplier.SupplierRequest;
import com.smartusers.logitrackapi.entity.Supplier;
import com.smartusers.logitrackapi.mapper.SupplierMapper;
import com.smartusers.logitrackapi.repository.SupplierRepository;
import com.smartusers.logitrackapi.service.interfaces.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public Supplier create(SupplierRequest request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new RuntimeException("Un fournisseur avec ce nom existe déjà !");
        }
        Supplier supplier = supplierMapper.toEntity(request);
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier update(Long id, SupplierRequest request) {
        Supplier existing = getById(id);
        if (!existing.getName().equals(request.getName()) && supplierRepository.existsByName(request.getName())) {
            throw new RuntimeException("Un fournisseur avec ce nom existe déjà !");
        }
        supplierMapper.updateEntityFromRequest( request,existing);
        return supplierRepository.save(existing);
    }

    @Override
    public Supplier getById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec ID: " + id));
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Page<Supplier> getAll(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    @Override
    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue();
    }

    @Override
    public Supplier activate(Long id) {
        Supplier supplier = getById(id);
        supplier.setActive(true);
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier deactivate(Long id) {
        Supplier supplier = getById(id);
        supplier.setActive(false);
        return supplierRepository.save(supplier);
    }

    @Override
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Fournisseur non trouvé avec ID: " + id);
        }
        supplierRepository.deleteById(id);
    }
}
