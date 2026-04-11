package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUserIdAndProductId(Integer userId, Integer productId);
    List<Cart> findByUserId(Integer userId);
}