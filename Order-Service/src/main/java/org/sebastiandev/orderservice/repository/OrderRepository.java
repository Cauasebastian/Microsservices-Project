package org.sebastiandev.orderservice.repository;

import org.sebastiandev.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderLineItems oli WHERE oli.skuCode = :skuCode")
    List<Order> findBySkuCode(@Param("skuCode") String skuCode);
}

