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
    private AddressRepository addressRepository;
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
        for (Order order : orders) {
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
            User buyer = userRepository.findById(order.getBuyerId()).orElse(null);
            User seller = userRepository.findById(order.getSellerId()).orElse(null);
            if (buyer != null) order.setBuyerName(buyer.getUsername());  // ✅ 这里设置用户名
            if (seller != null) order.setSellerName(seller.getUsername()); // ✅ 这里设置用户名

            if (order.getAddressId() != null && order.getAddressId() > 0) {
                Address address = addressRepository.findById(order.getAddressId()).orElse(null);
                order.setAddress(address);}
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
        order.setRefundStatus("none");//初始设退款状态为none

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

        for (Order order : orders) {
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
            // ✅ 添加地址填充
            if (order.getAddressId() != null && order.getAddressId() > 0) {
                order.setAddress(addressRepository.findById(order.getAddressId()).orElse(null));
            }
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

        for (Order order : orders) {
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
            // ✅ 卖家看订单也能看到买家的收货地址
            if (order.getAddressId() != null && order.getAddressId() > 0) {
                order.setAddress(addressRepository.findById(order.getAddressId()).orElse(null));
            }
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
// 已退款的也不能再申请
        if ("refunded".equals(order.getOrderStatus())) {
            return Result.error("订单已退款，无法再次申请");
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
    //15.买家取消订单
    // 买家取消订单（仅待付款状态）
    @PostMapping("/cancel")
    public Result cancelOrder(@RequestParam Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 只有买家可以取消
        if (!order.getBuyerId().equals(loginUser.getId())) {
            return Result.error("无权操作");
        }

        // 只有待付款可以取消
        if (!"pending".equals(order.getOrderStatus())) {
            return Result.error("当前订单状态不可取消");
        }

        order.setOrderStatus("cancelled");
        orderRepository.save(order);

        // 恢复优惠券
        if (order.getUserCouponId() != null) {
            Optional<UserCoupon> ucOpt = userCouponRepository.findById(order.getUserCouponId());
            if (ucOpt.isPresent()) {
                UserCoupon userCoupon = ucOpt.get();
                userCoupon.setStatus("unused");
                userCoupon.setUseTime(null);
                userCouponRepository.save(userCoupon);
            }
        }

        return Result.success("取消成功");
    }
    // 16. 管理员取消订单（任意订单）
    @PostMapping("/admin/cancel/{orderId}")
    public Result adminCancelOrder(@PathVariable Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 只有待付款可以取消
        if (!"pending".equals(order.getOrderStatus())) {
            return Result.error("当前订单状态不可取消");
        }

        order.setOrderStatus("cancelled");
        orderRepository.save(order);

        // 恢复优惠券
        if (order.getUserCouponId() != null) {
            Optional<UserCoupon> ucOpt = userCouponRepository.findById(order.getUserCouponId());
            if (ucOpt.isPresent()) {
                UserCoupon userCoupon = ucOpt.get();
                userCoupon.setStatus("unused");
                userCoupon.setUseTime(null);
                userCouponRepository.save(userCoupon);
            }
        }

        return Result.success("取消成功");
    }

    // 17. 管理员强制发货
    @PostMapping("/admin/ship/{orderId}")
    public Result adminShipOrder(@PathVariable Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!"paid".equals(order.getOrderStatus())) {
            return Result.error("只有待发货订单可以发货");
        }

        order.setOrderStatus("shipped");
        order.setDeliveryStatus("shipped");
        order.setShipTime(LocalDateTime.now());
        orderRepository.save(order);

        return Result.success("发货成功");
    }

    // 18. 管理员强制确认收货
    @PostMapping("/admin/confirm/{orderId}")
    public Result adminConfirmOrder(@PathVariable Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!"shipped".equals(order.getOrderStatus())) {
            return Result.error("只有待收货订单可以确认收货");
        }

        // 线下交易：确认收货时扣款
        if ("offline".equals(order.getTradeType())) {
            int result = walletRepository.deductBalance(order.getBuyerId(), order.getTotalAmount());
            if (result == 0) {
                return Result.error("买家余额不足，无法确认收货");
            }
            // 给卖家加钱
            BigDecimal originalAmount = order.getPrice().multiply(new BigDecimal(order.getQuantity()));
            walletRepository.addBalance(order.getSellerId(), originalAmount);

            // 流水记录...
        }

        order.setOrderStatus("completed");
        order.setDeliveryStatus("received");
        order.setCompleteTime(LocalDateTime.now());
        orderRepository.save(order);

        return Result.success("确认收货成功");
    }

    // 19. 管理员按条件搜索订单
    @GetMapping("/admin/search")
    public Result adminSearchOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) String sellerName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String orderStatus,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        List<Order> orders = orderRepository.findAll();
        List<Order> result = new ArrayList<>();

        for (Order order : orders) {
            // 填充商品和用户信息
            order.setProduct(productRepository.findById(order.getProductId()).orElse(null));
            User buyer = userRepository.findById(order.getBuyerId()).orElse(null);
            User seller = userRepository.findById(order.getSellerId()).orElse(null);
            if (buyer != null) order.setBuyerName(buyer.getUsername());
            if (seller != null) order.setSellerName(seller.getUsername());

            // 筛选条件
            boolean match = true;
            if (orderNo != null && !orderNo.isEmpty() && !order.getOrderNo().contains(orderNo)) {
                match = false;
            }
            if (buyerName != null && !buyerName.isEmpty() && (buyer == null || !buyer.getUsername().contains(buyerName))) {
                match = false;
            }
            if (sellerName != null && !sellerName.isEmpty() && (seller == null || !seller.getUsername().contains(sellerName))) {
                match = false;
            }
            if (productName != null && !productName.isEmpty()) {
                Product p = order.getProduct();
                if (p == null || !p.getName().contains(productName)) {
                    match = false;
                }
            }
            if (orderStatus != null && !orderStatus.isEmpty() && !orderStatus.equals(order.getOrderStatus())) {
                match = false;
            }

            if (match) {
                result.add(order);
            }
        }

        return Result.success(result);
    }

    // 20. 管理员获取订单统计
    @GetMapping("/admin/statistics")
    public Result getOrderStatistics(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        List<Order> orders = orderRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", orders.size());
        stats.put("pending", orders.stream().filter(o -> "pending".equals(o.getOrderStatus())).count());
        stats.put("paid", orders.stream().filter(o -> "paid".equals(o.getOrderStatus())).count());
        stats.put("shipped", orders.stream().filter(o -> "shipped".equals(o.getOrderStatus())).count());
        stats.put("completed", orders.stream().filter(o -> "completed".equals(o.getOrderStatus())).count());
        stats.put("cancelled", orders.stream().filter(o -> "cancelled".equals(o.getOrderStatus())).count());
        stats.put("refunded", orders.stream().filter(o -> "refunded".equals(o.getOrderStatus())).count());

        // 计算总金额
        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", totalAmount);

        return Result.success(stats);
    }
    //21.管理员删除订单
    // 管理员删除订单（任意订单）
    @DeleteMapping("/admin/delete/{orderId}")
    public Result adminDeleteOrder(@PathVariable Integer orderId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        orderRepository.delete(order);
        return Result.success("删除成功");
    }
    //22.买家修改收货地址
    // 修改订单收货地址（只有待发货状态可以修改）
    @PostMapping("/updateAddress")
    public Result updateOrderAddress(@RequestParam Integer orderId, @RequestParam Integer addressId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 只能修改自己的订单
        if (!order.getBuyerId().equals(loginUser.getId())) {
            return Result.error("无权操作");
        }

        // ✅ 只有待发货状态可以修改地址
        if (!"paid".equals(order.getOrderStatus())) {
            return Result.error("只有待发货状态的订单才能修改地址");
        }

        // 检查地址是否存在且属于当前用户
        Address address = addressRepository.findById(addressId).orElse(null);
        if (address == null || !address.getUserId().equals(loginUser.getId())) {
            return Result.error("地址不存在");
        }

        order.setAddressId(addressId);
        orderRepository.save(order);

        return Result.success("地址修改成功");
    }
}