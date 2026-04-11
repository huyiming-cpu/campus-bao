package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 正常使用，无报错，无冲突
    List<Product> findByUser_Id(Integer userId);
    List<Product> findByStatus(Integer status);
    List<Product> findByUser_IdAndBuyerIdIsNull(Integer userId);
    List<Product> findByBuyerId(Integer buyerId);

    List<Product> findByUserId(Integer id);

    List<Product> findByUserIdAndStatus(Integer userId, Integer status);
}