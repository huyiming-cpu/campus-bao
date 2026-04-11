package com.campus.campusbao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;    // 账户
    private String password;    // 密码
    private String studentId;   // 学号
    private String cardId;      // 一卡通号
    private String phone;       // 电话
    private String university;  // 大学
    private String avatar;       // 头像
    private Integer creditScore; // 信用分
    private String creditLevel;  // 信用等级
    private Integer defaultAddressId; // 默认地址ID
    private String description; // 个人描述
    private String gender;  // 性别
    private LocalDate birth; // 生日

    // 生成 getter、setter
    @Column(columnDefinition = "varchar(255) default ''")
    private String userTags;

    // getter、toString 记得加上
    public String getUserTags() { return userTags; }
    public void setUserTags(String userTags) { this.userTags = userTags; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getBirth() { return birth; }
    public void setBirth(LocalDate birth) { this.birth = birth; }
}