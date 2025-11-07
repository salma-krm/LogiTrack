package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    List<SalesOrder> findByClientId(Long clientId);

    Page<SalesOrder> findByClientId(Long clientId, Pageable pageable);

    List<SalesOrder> findByStatus(SalesOrderStatus status);

    Page<SalesOrder> findByStatus(SalesOrderStatus status, Pageable pageable);

    @Query("SELECT so FROM SalesOrder so WHERE so.client.id = :clientId AND so.status = :status")
    Page<SalesOrder> findByClientIdAndStatus(@Param("clientId") Long clientId,
                                             @Param("status") SalesOrderStatus status,
                                             Pageable pageable);

    @Query("SELECT so FROM SalesOrder so WHERE so.createdAt >= :startDate AND so.createdAt <= :endDate")
    List<SalesOrder> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT so FROM SalesOrder so WHERE so.status = :status AND so.reservedAt < :expiryDate")
    List<SalesOrder> findExpiredReservations(@Param("status") SalesOrderStatus status,
                                             @Param("expiryDate") LocalDateTime expiryDate);

    @Query("SELECT COUNT(so) FROM SalesOrder so WHERE so.status = :status")
    Long countByStatus(@Param("status") SalesOrderStatus status);

    @Query("SELECT so FROM SalesOrder so WHERE so.warehouse.id = :warehouseId")
    List<SalesOrder> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    boolean existsByOrderNumber(String orderNumber);
}