package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Product;
import com.campus.campusbao.entity.ProductClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductClickRepository extends JpaRepository<ProductClick, Integer> {

    // 根据用户ID和商品ID查询
    ProductClick findByUserIdAndProductId(Integer userId, Integer productId);

    // 增加点击次数
    @Modifying
    @Transactional
    @Query("UPDATE ProductClick c SET c.clickCount = c.clickCount + 1, c.updateTime = CURRENT_TIMESTAMP WHERE c.userId = ?1 AND c.productId = ?2")
    int incrementClickCount(Integer userId, Integer productId);
    // 获取用户点击≥3次的商品ID
    @Query("SELECT c.productId FROM ProductClick c WHERE c.userId = ?1 AND c.clickCount >= 3")
    List<Integer> findFrequentClickProductIds(Integer userId);
    // 获取用户点击≥3次的商品类型
    @Query("SELECT DISTINCT p.type FROM ProductClick c JOIN Product p ON c.productId = p.id WHERE c.userId = ?1 AND c.clickCount >= 3")
    List<String> findFrequentClickTypes(Integer userId);
    List<ProductClick> findByUserId(Integer userId);

}