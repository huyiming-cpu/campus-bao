package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Evaluation;
import com.campus.campusbao.entity.Order;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.EvaluationRepository;
import com.campus.campusbao.repository.OrderRepository;
import com.campus.campusbao.repository.ProductRepository;
import com.campus.campusbao.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 评分对应增加的信用分
    private int getCreditIncrease(int rating) {
        if (rating >= 5) return 3;
        if (rating >= 4) return 2;
        if(rating >= 3)  return 1;
        if(rating >= 2)  return 0;
        return -1;
    }

    // 创建评价
    @PostMapping("/create")
    public Result createEvaluation(@RequestBody Evaluation evaluation, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 检查订单是否存在
        Order order = orderRepository.findById(evaluation.getOrderId()).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 检查订单是否已完成
        if (!"completed".equals(order.getOrderStatus())) {
            return Result.error("只有已完成订单才能评价");
        }

        // 检查是否已经评价过
        if (evaluationRepository.existsByOrderIdAndFromUserId(evaluation.getOrderId(), loginUser.getId())) {
            return Result.error("您已经评价过该订单");
        }

        // 设置评价信息
        evaluation.setFromUserId(loginUser.getId());
        evaluation.setCreateTime(LocalDateTime.now());

        // 确定被评价人
        if (loginUser.getId().equals(order.getBuyerId())) {
            evaluation.setToUserId(order.getSellerId());
        } else {
            evaluation.setToUserId(order.getBuyerId());
        }

        evaluation.setProductId(order.getProductId());
        evaluationRepository.save(evaluation);

        // 更新被评价人的信用分
        User toUser = userRepository.findById(evaluation.getToUserId()).orElse(null);
        if (toUser != null) {
            int addScore = getCreditIncrease(evaluation.getRating());
            toUser.setCreditScore(toUser.getCreditScore() + addScore);

            // 更新信用等级
            int score = toUser.getCreditScore();
            if (score < 40) toUser.setCreditLevel("较差");
            else if (score < 60) toUser.setCreditLevel("一般");
            else if (score < 80) toUser.setCreditLevel("良好");
            else if (score < 100) toUser.setCreditLevel("优秀");
            else toUser.setCreditLevel("极好");

            userRepository.save(toUser);
        }

        return Result.success("评价成功");
    }

    // 卖家回复评价
    @PostMapping("/reply")
    public Result replyEvaluation(@RequestParam Integer evaluationId, @RequestParam String reply, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Evaluation evaluation = evaluationRepository.findById(evaluationId).orElse(null);
        if (evaluation == null) {
            return Result.error("评价不存在");
        }

        // 检查是否是卖家
        if (!loginUser.getId().equals(evaluation.getToUserId())) {
            return Result.error("无权回复");
        }

        evaluation.setReply(reply);
        evaluation.setReplyTime(LocalDateTime.now());
        evaluationRepository.save(evaluation);

        return Result.success("回复成功");
    }

    // 获取订单的评价状态
    @GetMapping("/order/status")
    public Result getOrderEvaluationStatus(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        boolean hasEvaluated = evaluationRepository.existsByOrderIdAndFromUserId(orderId, loginUser.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("hasEvaluated", hasEvaluated);
        return Result.success(result);
    }

    // 获取用户收到的评价列表
    @GetMapping("/received")
    public Result getReceivedEvaluations(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<Evaluation> evaluations = evaluationRepository.findByToUserId(loginUser.getId());

        // 填充商品和评价人信息
        for (Evaluation ev : evaluations) {
            ev.setProduct(productRepository.findById(ev.getProductId()).orElse(null));
            ev.setFromUser(userRepository.findById(ev.getFromUserId()).orElse(null));
        }

        return Result.success(evaluations);
    }

    // 获取用户给出的评价列表
    @GetMapping("/given")
    public Result getGivenEvaluations(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<Evaluation> evaluations = evaluationRepository.findByFromUserId(loginUser.getId());

        for (Evaluation ev : evaluations) {
            ev.setProduct(productRepository.findById(ev.getProductId()).orElse(null));
            ev.setToUser(userRepository.findById(ev.getToUserId()).orElse(null));
        }

        return Result.success(evaluations);
    }

    // 获取卖家主页的评价
    @GetMapping("/seller/{sellerId}")
    public Result getSellerEvaluations(@PathVariable Integer sellerId) {
        List<Evaluation> evaluations = evaluationRepository.findByToUserId(sellerId);

        // 计算好评率
        long total = evaluations.size();
        long goodCount = evaluations.stream().filter(e -> e.getRating() >= 4).count();
        double goodRate = total > 0 ? (double) goodCount / total * 100 : 0;

        for (Evaluation ev : evaluations) {
            ev.setProduct(productRepository.findById(ev.getProductId()).orElse(null));
            ev.setFromUser(userRepository.findById(ev.getFromUserId()).orElse(null));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("goodRate", Math.round(goodRate));
        result.put("list", evaluations);

        return Result.success(result);
    }
}