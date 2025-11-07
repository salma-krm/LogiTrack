package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.entity.Category;
import com.smartusers.logitrackapi.entity.Warehouse;

import java.util.List;

public interface WarehouseService {  Warehouse create(Warehouse warehouse);
    List<Warehouse> getAll();
    Warehouse getById(Long id);
    Warehouse update(Long id, Warehouse warehouse);
    void delete(Long id);

}
