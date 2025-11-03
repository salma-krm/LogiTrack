package com.smartusers.logitrackapi.repository;



import com.smartusers.logitrackapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

