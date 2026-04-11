/*package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            return Result.success(loginUser);
        } else {
            return Result.error("账号或密码错误");
        }
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String msg = userService.register(user);
        if ("success".equals(msg)) {
            return Result.success("注册成功");
        } else {
            return Result.error(msg);
        }
    }
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody User user) {
        return Result.success(userService.resetPassword(user));
    }
}*/
package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    // 登录滨并保存信息
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpSession session) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            session.setAttribute("loginUser", loginUser);
           return Result.success(loginUser);
        } else {
            return Result.error("账号或密码错误");
        }
    }

    // 注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        User newUser = new User();
        //用户的信息
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setPhone(user.getPhone());
        newUser.setUniversity(user.getUniversity());
        newUser.setStudentId(user.getStudentId());
        newUser.setCardId(user.getCardId());
        newUser.setGender(user.getGender());
        newUser.setBirth(user.getBirth());
        //设置默认值
        newUser.setCreditScore(60);
        newUser.setCreditLevel("良好");
        newUser.setAvatar("default.jpg");
        newUser.setDescription("这个人很懒，什么都没留下~");
        if (user.getGender() == null) user.setGender("未设置");
        if (user.getBirth() == null) user.setBirth(null);
        String msg = userService.register(newUser);
        if ("success".equals(msg)) {
            return Result.success("注册成功");
        } else {
            return Result.error(msg);
        }
    }
    //重置密码
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody User user) {
        return Result.success(userService.resetPassword(user));
    }
}