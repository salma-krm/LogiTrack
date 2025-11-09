package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.supplier.SupplierRequest;
import com.smartusers.logitrackapi.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {

    Supplier create(SupplierRequest request);

    Supplier update(Long id, SupplierRequest request);

    Supplier getById(Long id);

    List<Supplier> getAll();

    Page<Supplier> getAll(Pageable pageable);

    List<Supplier> getActiveSuppliers();

    Supplier activate(Long id);

    Supplier deactivate(Long id);

    void delete(Long id);
}
