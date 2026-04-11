package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.*;
import com.campus.campusbao.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    @Autowired
    private final WalletRepository walletRepository;
    public OrderController(OrderRepository orderRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }
    // 1.管理员获取所有订单
    @GetMapping("/admin/all")
    public Result getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        // 填充商品和用户信息
        for (Order order : orders) {
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
            User buyer = userRepository.findById(order.getBuyerId()).orElse(null);
            User seller = userRepository.findById(order.getSellerId()).orElse(null);
            if (buyer != null) order.setBuyerName(buyer.getUsername());
            if (seller != null) order.setSellerName(seller.getUsername());
        }
        return Result.success(orders);
    }

    //2.创建订单
    @PostMapping("/create")
    public Result createOrder(@RequestBody Order order, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // ✅ 线上交易且钱包支付时，检查余额
        if ("online".equals(order.getTradeType()) && "wallet".equals(order.getPayType())) {
            Wallet wallet = walletRepository.findByUserId(loginUser.getId()).orElse(null);
            BigDecimal balance = wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
            if (balance.compareTo(order.getTotalAmount()) < 0) {
                return Result.error("余额不足，请先充值");
            }
        }

        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
        order.setOrderNo(orderNo);
        order.setBuyerId(loginUser.getId());

        // 如果没传卖家ID，从商品获取
        if (order.getSellerId() == null) {
            Product product = productRepository.findById(order.getProductId()).orElse(null);
            if (product != null && product.getUser() != null) {
                order.setSellerId(product.getUser().getId());
                System.out.println("从商品获取卖家ID: " + order.getSellerId());
            }
        }

        order.setCreateTime(LocalDateTime.now());
        order.setOrderStatus("pending");
        order.setPayStatus("unpaid");
        order.setDeliveryStatus("pending");

        // ✅ 计算优惠金额 = (原价 × 数量) - 实付总价
        BigDecimal originalTotal = order.getPrice().multiply(new BigDecimal(order.getQuantity()));
        BigDecimal discountAmount = originalTotal.subtract(order.getTotalAmount());
        order.setDiscountAmount(discountAmount);

        Order savedOrder = orderRepository.save(order);
        System.out.println("订单保存成功，卖家ID: " + savedOrder.getSellerId());

        // ✅ 使用优惠券（更新状态）
        if (order.getUserCouponId() != null) {
            // 直接更新优惠券状态
            Optional<UserCoupon> ucOpt = userCouponRepository.findById(order.getUserCouponId());
            if (ucOpt.isPresent()) {
                UserCoupon userCoupon = ucOpt.get();
                userCoupon.setStatus("used");
                userCoupon.setUseTime(LocalDateTime.now());
                userCouponRepository.save(userCoupon);
            }
        }
        return Result.success(savedOrder);
    }

    // 3.买家获取我的订单
    @GetMapping("/myBuy")
    public Result getMyBuyOrders(HttpSession session, @RequestParam(required = false) String status) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByBuyerIdAndOrderStatusOrderByCreateTimeDesc(loginUser.getId(), status);
        } else {
            orders = orderRepository.findByBuyerIdOrderByCreateTimeDesc(loginUser.getId());
        }

        // 填充商品信息
        for (Order order : orders) {
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
        }

        return Result.success(orders);
    }

    // 4.卖家获取我卖出的订单
    @GetMapping("/mySell")
    public Result getMySellOrders(HttpSession session, @RequestParam(required = false) String status) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findBySellerIdAndOrderStatusOrderByCreateTimeDesc(loginUser.getId(), status);
        } else {
            orders = orderRepository.findBySellerIdOrderByCreateTimeDesc(loginUser.getId());
        }

        // 填充商品信息
        for (Order order : orders) {
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
        }

        return Result.success(orders);
    }


    // 5.支付接口

    @PostMapping("/pay")
    public Result payOrder(@RequestParam Integer orderId, @RequestParam String payType, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 检查订单状态
        if (!"pending".equals(order.getOrderStatus())) {
            return Result.error("订单状态不正确，无法支付");
        }
        // 检查是否是自己的订单
        if (!order.getBuyerId().equals(loginUser.getId())) {
            return Result.error("无权操作此订单");
        }
        // 钱包支付
        if ("wallet".equals(payType)) {
            int result = walletRepository.deductBalance(loginUser.getId(), order.getTotalAmount());
            if (result == 0) {
                return Result.error("余额不足");
            }
        } else {
            return Result.error("不支持的支付方式");
        }

        // 更新订单状态
        order.setPayStatus("paid");
        order.setOrderStatus("paid");
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        orderRepository.save(order);
// 计算原价
        BigDecimal originalAmount = order.getPrice().multiply(new BigDecimal(order.getQuantity()));

// 给卖家加钱（原价）
        walletRepository.addBalance(order.getSellerId(), originalAmount);
        // 买家扣款流水（实付金额）
        TransactionLog buyerLog = new TransactionLog();
        buyerLog.setOrderId(order.getId());
        buyerLog.setUserId(loginUser.getId());
        buyerLog.setAmount(order.getTotalAmount());
        buyerLog.setType("pay");
        buyerLog.setPayType(payType);
        buyerLog.setStatus("success");
        buyerLog.setCreateTime(LocalDateTime.now());
        transactionLogRepository.save(buyerLog);

        // 卖家收款流水（实付金额）
        TransactionLog sellerLog = new TransactionLog();
        sellerLog.setOrderId(order.getId());
        sellerLog.setUserId(order.getSellerId());
        sellerLog.setAmount(originalAmount);
        sellerLog.setType("income");
        sellerLog.setPayType(payType);
        sellerLog.setStatus("success");
        sellerLog.setCreateTime(LocalDateTime.now());
        transactionLogRepository.save(sellerLog);

        Product product = productRepository.findById(order.getProductId()).orElse(null);
        if (product != null) {
            product.setStatus(1);
            product.setBuyerId(order.getBuyerId());
            productRepository.save(product);
        }
        return Result.success("支付成功");
    }


    // 6.获取钱包余额
    @GetMapping("/wallet/balance")
    public Result getBalance(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Wallet wallet = walletRepository.findByUserId(loginUser.getId()).orElse(null);
        BigDecimal balance = wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
        return Result.success(balance);
    }
    // 卖家发货
    @PostMapping("/ship")
    public Result shipOrder(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 检查是否是卖家
        if (!order.getSellerId().equals(loginUser.getId())) {
            return Result.error("无权操作");
        }

        // 检查订单状态
        if (!"paid".equals(order.getOrderStatus())) {
            return Result.error("订单状态不正确");
        }

        order.setOrderStatus("shipped");
        order.setDeliveryStatus("shipped");
        order.setShipTime(LocalDateTime.now());
        orderRepository.save(order);

        return Result.success("发货成功");
    }

    // 7.买家确认收货
    @PostMapping("/confirm")
    public Result confirmOrder(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!order.getBuyerId().equals(loginUser.getId())) {
            return Result.error("无权操作");
        }

        if (!"shipped".equals(order.getOrderStatus())) {
            return Result.error("订单状态不正确");
        }

        // 线下交易：确认收货时扣款
        if ("offline".equals(order.getTradeType())) {
            int result = walletRepository.deductBalance(loginUser.getId(), order.getTotalAmount());
            if (result == 0) {
                return Result.error("余额不足，请先充值");
            }
            // 给卖家加钱
            // ✅ 卖家收款 = 原价（price × quantity）
            BigDecimal originalAmount = order.getPrice().multiply(new BigDecimal(order.getQuantity()));
            walletRepository.addBalance(order.getSellerId(), originalAmount);

            // 买家扣款流水
            TransactionLog buyerLog = new TransactionLog();
            buyerLog.setOrderId(order.getId());
            buyerLog.setUserId(loginUser.getId());
            buyerLog.setAmount(order.getTotalAmount());
            buyerLog.setType("pay");
            buyerLog.setPayType("offline");
            buyerLog.setStatus("success");
            buyerLog.setCreateTime(LocalDateTime.now());
            transactionLogRepository.save(buyerLog);

            // 卖家收款流水
            TransactionLog sellerLog = new TransactionLog();
            sellerLog.setOrderId(order.getId());
            sellerLog.setUserId(order.getSellerId());
            sellerLog.setAmount(originalAmount);
            sellerLog.setType("income");  // 收入类型
            sellerLog.setPayType("offline");
            sellerLog.setStatus("success");
            sellerLog.setCreateTime(LocalDateTime.now());
            transactionLogRepository.save(sellerLog);
        }

        order.setOrderStatus("completed");
        order.setDeliveryStatus("received");
        order.setCompleteTime(LocalDateTime.now());
        orderRepository.save(order);

        return Result.success("确认收货成功");
    }
    // 8.卖家设置自提点（针对线下订单）
    @PostMapping("/setPickupPoint")
    public Result setPickupPoint(@RequestParam Integer orderId, @RequestParam String pickupPoint, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 检查是否是卖家
        if (!order.getSellerId().equals(loginUser.getId())) {
            return Result.error("无权操作");
        }

        // 检查是否是线下交易
        if (!"offline".equals(order.getTradeType())) {
            return Result.error("只有线下交易可以设置自提点");
        }

        // 检查订单状态
        if (!"pending".equals(order.getOrderStatus())) {
            return Result.error("订单状态不正确");
        }

        order.setPickupPoint(pickupPoint);
        order.setOrderStatus("shipped");  // 设置自提点后变为待收货
        order.setShipTime(LocalDateTime.now());
        orderRepository.save(order);

        return Result.success("自提点设置成功");
    }
    //9.钱包流水
    @GetMapping("/wallet/transactions")
    public Result getTransactions(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 查询流水表的所有记录
        List<TransactionLog> logs = transactionLogRepository.findByUserIdOrderByCreateTimeDesc(loginUser.getId());

        List<Map<String, Object>> transactions = new ArrayList<>();
        for (TransactionLog log : logs) {
            Map<String, Object> trans = new HashMap<>();
            trans.put("id", log.getId());
            trans.put("type", log.getType());
            trans.put("amount", log.getAmount());
            trans.put("payType", log.getPayType());
            trans.put("createTime", log.getCreateTime());

            // 获取商品名称
            String productName = "";
            if (log.getOrderId() != null) {
                Order order = orderRepository.findById(log.getOrderId()).orElse(null);
                if (order != null && order.getProductId() != null) {
                    Product product = productRepository.findById(order.getProductId()).orElse(null);
                    if (product != null) {
                        productName = product.getName();
                    }
                }
            }

            // 根据类型设置备注
            if ("recharge".equals(log.getPayType())) {
                trans.put("remark", "充值");
            } else if ("pay".equals(log.getType())) {
                trans.put("remark", "消费 - " + productName);
            } else if ("income".equals(log.getType())) {
                trans.put("remark", "收款 - " + productName);
            } else if ("refund".equals(log.getType())) {
                trans.put("remark", "退款 - " + productName);
            } else if ("deduct".equals(log.getType())) {
                trans.put("remark", "退款扣款 - " + productName);
            }

            transactions.add(trans);
        }

        return Result.success(transactions);
    }

    // 10.钱包充值
    @PostMapping("/wallet/recharge")
    public Result recharge(@RequestParam BigDecimal amount, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("充值金额必须大于0");
        }

        // 增加余额
        walletRepository.addBalance(loginUser.getId(), amount);

        // 添加充值流水（
        TransactionLog log = new TransactionLog();
        log.setUserId(loginUser.getId());
        log.setAmount(amount);
        log.setType("income");  // 收入
        log.setPayType("recharge");  // 充值
        log.setStatus("success");
        log.setCreateTime(LocalDateTime.now());
        transactionLogRepository.save(log);

        return Result.success("充值成功");
    }
    // 11. 买家申请退款
    @PostMapping("/refund/apply")
    public Result applyRefund(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 只有买家可以申请退款
        if (!order.getBuyerId().equals(loginUser.getId())) {
            return Result.error("无权操作");
        }

        // 只有待付款和已取消不能退款，其他都可以
        if ("pending".equals(order.getOrderStatus()) || "cancelled".equals(order.getOrderStatus())) {
            return Result.error("当前订单状态不可退款");
        }
        // 检查是否已申请过
        if (!"none".equals(order.getRefundStatus())) {
            return Result.error("已申请过退款，请等待处理");
        }

        order.setRefundStatus("pending");
        order.setRefundTime(LocalDateTime.now());
        orderRepository.save(order);

        return Result.success("退款申请已提交，请等待卖家处理");
    }

    // 12. 卖家/管理员同意退款
    @PostMapping("/refund/approve")
    public Result approveRefund(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 只有卖家或管理员可以同意退款
        boolean isSeller = order.getSellerId().equals(loginUser.getId());
        boolean isAdmin = "admin".equals(loginUser.getUsername());
        if (!isSeller && !isAdmin) {
            return Result.error("无权操作");
        }

        if (!"pending".equals(order.getRefundStatus())) {
            return Result.error("退款申请不存在或已处理");
        }

        // 1. 买家退款（加回实付金额）
        walletRepository.addBalance(order.getBuyerId(), order.getTotalAmount());

        // 2. 卖家扣款（扣回原价）
        BigDecimal originalAmount = order.getPrice().multiply(new BigDecimal(order.getQuantity()));
        Wallet sellerWallet = walletRepository.findByUserId(order.getSellerId()).orElse(null);
        if (sellerWallet == null) {
            return Result.error("卖家钱包不存在");
        }
        BigDecimal newBalance = sellerWallet.getBalance().subtract(originalAmount);
        sellerWallet.setBalance(newBalance);
        walletRepository.save(sellerWallet);
        System.out.println("卖家扣款成功，新余额: " + newBalance);

        // 3. 恢复优惠券（如果有）
        if (order.getUserCouponId() != null) {
            Optional<UserCoupon> ucOpt = userCouponRepository.findById(order.getUserCouponId());
            if (ucOpt.isPresent()) {
                UserCoupon userCoupon = ucOpt.get();
                userCoupon.setStatus("unused");
                userCoupon.setUseTime(null);
                userCouponRepository.save(userCoupon);
            }
        }

        // 4. 恢复商品状态
        Product product = productRepository.findById(order.getProductId()).orElse(null);
        if (product != null) {
            product.setStatus(0);
            product.setBuyerId(null);
            productRepository.save(product);
        }

        // 5. 更新订单状态
        order.setOrderStatus("refunded");
        order.setRefundStatus("approved");
        order.setRefundAmount(order.getTotalAmount());
        order.setCompleteTime(null);
        orderRepository.save(order);

        // 6. 退款流水
        TransactionLog refundLog = new TransactionLog();
        refundLog.setOrderId(order.getId());
        refundLog.setUserId(order.getBuyerId());
        refundLog.setAmount(order.getTotalAmount());
        refundLog.setType("refund");
        refundLog.setPayType(order.getPayType());
        refundLog.setStatus("success");
        refundLog.setCreateTime(LocalDateTime.now());
        transactionLogRepository.save(refundLog);
        // 7.卖家扣款流水
        TransactionLog sellerDeductLog = new TransactionLog();
        sellerDeductLog.setOrderId(order.getId());
        sellerDeductLog.setUserId(order.getSellerId());
        sellerDeductLog.setAmount(originalAmount);
        sellerDeductLog.setType("deduct");  // 扣款类型
        sellerDeductLog.setPayType(order.getPayType());
        sellerDeductLog.setStatus("success");
        sellerDeductLog.setCreateTime(LocalDateTime.now());
        transactionLogRepository.save(sellerDeductLog);
        return Result.success("退款成功");
    }


    // 13. 卖家拒绝退款
    @PostMapping("/refund/reject")
    public Result rejectRefund(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        boolean isSeller = order.getSellerId().equals(loginUser.getId());
        boolean isAdmin = "admin".equals(loginUser.getUsername());
        if (!isSeller && !isAdmin) {
            return Result.error("无权操作");
        }

        if (!"pending".equals(order.getRefundStatus())) {
            return Result.error("退款申请不存在或已处理");
        }

        order.setRefundStatus("rejected");
        orderRepository.save(order);

        return Result.success("已拒绝退款申请");
    }

    // 14. 获取订单退款状态
    @GetMapping("/refund/status")
    public Result getRefundStatus(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("refundStatus", order.getRefundStatus());
        result.put("refundTime", order.getRefundTime());
        result.put("refundAmount", order.getRefundAmount());

        return Result.success(result);
    }
}