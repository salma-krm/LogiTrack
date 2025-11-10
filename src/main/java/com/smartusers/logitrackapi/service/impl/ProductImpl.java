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
import com.smartusers.logitrackapi.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper productMapper;

    private final SalesOrderLineRepository salesOrderLineRepository;
    private final SalesOrderRepository salesOrderRepository;

    @Override


    public ProductResponse desactivitedProduct(String SKU) {
        Product product = productRepository.findBySku(SKU);
        if (product == null) {
            throw new RuntimeException("Produit non trouvé avec le SKU : " + SKU);
        }
        List<Inventory> inventories = inventoryRepository.findAllByProduct_Id(product.getId());
        for (Inventory inventory : inventories) {
            if (inventory.getQuantityReserved() > 0) {
                throw new RuntimeException("Impossible de désactiver le produit car il y a des quantités réservées en stock.");

            }
        }

        List<SalesOrderLine> orderLines = salesOrderLineRepository.findAllByProduct_Id(product.getId());
        for (SalesOrderLine line : orderLines) {
            Optional<SalesOrder> salesOrderOpt = salesOrderRepository.findById(line.getSalesOrder().getId());


            if (salesOrderOpt.isPresent()) {
                SalesOrder salesOrder = salesOrderOpt.get();
                OrderStatus status = salesOrder.getStatus();


                if (status != OrderStatus.RESERVED && status != OrderStatus.CREATED) {
                    throw new RuntimeException("Impossible de désactiver le produit car il est associé à des commandes non livrées.");
                }
            }


        }
        product.setActive(false);
        productRepository.save(product);
        ProductResponse response = new ProductResponse();
        response.setActive(product.getActive());
        return response;
    }



    @Override
    public Product createProduct(Product product) {

        if (productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Le SKU existe déjà : " + product.getSku());
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);

        // Vérifier si le nouveau SKU est utilisé par un autre produit
        if (!existing.getSku().equals(product.getSku()) && productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException("Le SKU existe déjà : " + product.getSku());
        }

        existing.setName(product.getName());
        existing.setSku(product.getSku());
        existing.setCategory(product.getCategory());
        existing.setOriginalPrice(product.getOriginalPrice());
        existing.setProfit(product.getProfit());
        existing.setUnit(product.getUnit());
        existing.setActive(product.getActive());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

}
