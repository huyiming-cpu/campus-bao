package com.campus.campusbao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "need")
public class Need {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String type;
    private String title;
    private String content;

    @Column(name = "createtime")
    private LocalDateTime createTime;
    @Column(name = "category")
    private String category;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    // 关联用户（非数据库字段）
    @Transient
    private User user;
    private String image;

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}