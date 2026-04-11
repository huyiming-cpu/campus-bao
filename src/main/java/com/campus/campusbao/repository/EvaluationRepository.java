package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    // 查询某个订单的评价
    List<Evaluation> findByOrderId(Integer orderId);

    // 查询用户收到的评价
    List<Evaluation> findByToUserId(Integer toUserId);

    // 查询用户给出的评价
    List<Evaluation> findByFromUserId(Integer fromUserId);

    // 查询商品评价
    List<Evaluation> findByProductId(Integer productId);

    // 查询用户是否已评价某个订单
    boolean existsByOrderIdAndFromUserId(Integer orderId, Integer fromUserId);
}