package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {


    List<Product> findByUser_Id(Integer userId);
    List<Product> findByStatus(Integer status);
    List<Product> findByUser_IdAndBuyerIdIsNull(Integer userId);
    List<Product> findByBuyerId(Integer buyerId);

    List<Product> findByUserId(Integer id);

    List<Product> findByUserIdAndStatus(Integer userId, Integer status);

    List<Product> findByTypeAndStatusAndUserIdNot(String type, int i, Integer id);
    // 模糊搜索商品名称
    List<Product> findByNameContaining(String keyword);
}