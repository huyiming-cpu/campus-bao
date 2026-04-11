package com.campus.campusbao.service;

import com.campus.campusbao.entity.User;
import com.campus.campusbao.entity.Wallet;
import com.campus.campusbao.repository.UserRepository;
import com.campus.campusbao.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Override
    public User login(String username, String password) {
        User loginUser = userRepository.findByUsernameAndPassword(username, password);
        // 如再查一次完整信息
        if (loginUser != null) {
            loginUser = userRepository.findById(loginUser.getId()).orElse(loginUser);
        }
        return loginUser;
    }
    @Override
    public User getById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void updateById(User user) {
        userRepository.save(user);
    }

    @Override
    public void removeById(Integer id) {
        userRepository.deleteById(id);
    }
    // 计算信用等级
    public void updateCreditLevel(User user) {
        int score = user.getCreditScore();
        if (score < 40) {
            user.setCreditLevel("较差");
        } else if (score < 60) {
            user.setCreditLevel("一般");
        } else if (score < 80) {
            user.setCreditLevel("良好");
        } else if (score < 100) {
            user.setCreditLevel("优秀");
        } else {
            user.setCreditLevel("极好");
        }
        userRepository.save(user);
    }
//注册功能
    @Override
    public String register(User user) {
        // 判断学号是否已存在
        if (userRepository.existsByStudentId(user.getStudentId())) {
            return "该学号已注册";
        }
        // 判断一卡通是否已存在
        if (userRepository.existsByCardId(user.getCardId())) {
            return "该一卡通号已注册";
        }
        // 保存用户，
        User savedUser = userRepository.save(user);
        // ✅ 创建钱包，使用保存后的ID
        Wallet wallet = new Wallet();
        wallet.setUserId(savedUser.getId());
        wallet.setBalance(BigDecimal.valueOf(2000));
        wallet.setCreateTime(LocalDateTime.now());
        wallet.setUpdateTime(LocalDateTime.now());
        walletRepository.save(wallet);
     return "success";
    }
    //重置密码
    @Override
    public User resetPassword(User user) {
        // 根据 用户名 + 手机号 查找用户
        User u = userRepository.findByUsernameAndPhone(user.getUsername(), user.getPhone());
        // 把新密码 set 进原来的 password 字段
        u.setPassword(user.getPassword());
        // 保存到数据库（
        return userRepository.save(u);
    }



}