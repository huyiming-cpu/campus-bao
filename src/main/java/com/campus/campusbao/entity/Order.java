package com.campus.campusbao.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_no", unique = true)
    private String orderNo;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "buyer_id")
    private Integer buyerId;

    @Column(name = "seller_id")
    private Integer sellerId;

    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "pickup_point")
    private String pickupPoint;

    @Column(name = "trade_type")
    private String tradeType;

    @Column(name = "pay_type")
    private String payType;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "pay_status")
    private String payStatus;

    @Column(name = "delivery_status")
    private String deliveryStatus;

    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "ship_time")
    private LocalDateTime shipTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;
    @Column(name = "user_coupon_id")
    private Integer userCouponId;

    public Integer getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Integer userCouponId) { this.userCouponId = userCouponId; }

    // 关联商品（非数据库字段）
    @Transient
    private Product product;

    // 关联买家（非数据库字段）
    @Transient
    private User buyer;

    // 关联卖家（非数据库字段）
    @Transient
    private User seller;

    // 关联地址（非数据库字段）
    @Transient
    private Address address;
    @Column(name = "discount_amount")
    private BigDecimal discountAmount;
    @Column(name = "refund_status", columnDefinition = "varchar(20) default 'none'")
    private String refundStatus;

    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    // getter/setter
    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public LocalDateTime getRefundTime() { return refundTime; }
    public void setRefundTime(LocalDateTime refundTime) { this.refundTime = refundTime; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getBuyerId() { return buyerId; }
    public void setBuyerId(Integer buyerId) { this.buyerId = buyerId; }

    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }

    public LocalDateTime getShipTime() { return shipTime; }
    public void setShipTime(LocalDateTime shipTime) { this.shipTime = shipTime; }

    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public Address getAddress() { return address; }

    public void setAddress(Address address) {
        this.address = address;  // 添加这行
    }

    public void setBuyerName(String username) {
    }

    public void setSellerName(String username) {
    }
}