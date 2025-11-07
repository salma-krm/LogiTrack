package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {

}