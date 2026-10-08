package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // 查询买家订单
    List<Order> findByBuyerIdOrderByCreateTimeDesc(Integer buyerId);

    // 查询卖家订单
    List<Order> findBySellerIdOrderByCreateTimeDesc(Integer sellerId);

    // 根据状态查询买家订单
    List<Order> findByBuyerIdAndOrderStatusOrderByCreateTimeDesc(Integer buyerId, String status);

    // 根据状态查询卖家订单
    List<Order> findBySellerIdAndOrderStatusOrderByCreateTimeDesc(Integer sellerId, String status);

    // 根据订单号查询
    Order findByOrderNo(String orderNo);
    // 查询用户最近购买的订单
    List<Order> findTop5ByBuyerIdOrderByCreateTimeDesc(Integer buyerId);
    List<Order> findByBuyerIdAndOrderStatus(Integer buyerId, String orderStatus);
}