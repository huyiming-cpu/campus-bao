package com.campus.campusbao.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String image;
    private String info;
    private BigDecimal price;
    private String type;
    private Integer status;
    private Integer hot;
    private Integer buyerId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Product() {}


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getHot() { return hot; }
    public void setHot(Integer hot) { this.hot = hot; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getBuyerId() { return buyerId; }
    public void setBuyerId(Integer buyerId) { this.buyerId = buyerId; }
}