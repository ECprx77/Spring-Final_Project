package com.TZ.TechZone.repositories;

import com.TZ.TechZone.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByUser_Id(Integer userId, Pageable pageable);
    
    Optional<Order> findByIdAndUser_Id(Integer orderId, Integer userId);
}
