package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Product;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.UserRepository;
import com.campus.campusbao.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    //1.获取用户信息
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
    //2.管理员获取所有用户
    @GetMapping("/admin/users")
    public Result getAllUsers() {
        List<User> users = userRepository.findAll();
        // 隐藏密码
        for (User user : users) {
            user.setPassword(null);
        }
        return Result.success(users);
    }
    //3. 获取个人信息
    @GetMapping("/getMyInfo")
    public Result getMyInfo(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("未登录");
        }
        User fullUser = userService.getById(loginUser.getId());
        fullUser.setPassword(null);
        return Result.success(fullUser);
    }
    //更新头像
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(MultipartFile avatar, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("未登录");
        }

        String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
        // ✅ 改成 avatar（去掉s）
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/avatar/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = basePath + fileName;

        try {
            avatar.transferTo(new File(filePath));
            loginUser.setAvatar(fileName);
            userService.updateById(loginUser);
            return Result.success("头像修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    // 4.管理员更新用户信息
    @PostMapping("/admin/update")
    public Result adminUpdateUser(@RequestBody User user, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        User dbUser = userService.getById(user.getId());
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        if (user.getUsername() != null) dbUser.setUsername(user.getUsername());
        if (user.getUniversity() != null) dbUser.setUniversity(user.getUniversity());
        if (user.getCreditScore() != null) dbUser.setCreditScore(user.getCreditScore());
        if (user.getCreditLevel() != null) dbUser.setCreditLevel(user.getCreditLevel());
        if (user.getDescription() != null) dbUser.setDescription(user.getDescription());
        if (user.getPhone() != null) dbUser.setPhone(user.getPhone());           // 手机号
        if (user.getStudentId() != null) dbUser.setStudentId(user.getStudentId()); // 学号
        if (user.getCardId() != null) dbUser.setCardId(user.getCardId());         // 一卡通号
        if (user.getGender() != null) dbUser.setGender(user.getGender());         // 性别
        if (user.getBirth() != null) dbUser.setBirth(user.getBirth());            // 生日

        userService.updateById(dbUser);
        return Result.success("更新成功");
    }
    // 5.用户自己注销账号
    @DeleteMapping("/delete")
    public Result delete(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        userService.removeById(loginUser.getId());
        session.invalidate();
        return Result.success("注销成功");
    }

    // 短信验证码
    private Map<String, String> codeMap = new HashMap<>();
//6.验证码发送
    @GetMapping("/sendSms")
    public Result sendSms(String phone) {
        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        codeMap.put(phone, code);
        System.out.println("给 " + phone + " 发送验证码：" + code);
        return Result.success(code);
    }

    // 7.用户自己修改信息
    @PostMapping("/update")
    public Result update(
            @RequestBody User user,
            HttpSession session,
            @RequestParam(required = false) String code
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");

        User dbUser = userService.getById(loginUser.getId());

        boolean needCheckCode = false;
        if (user.getPhone() != null && !user.getPhone().equals(dbUser.getPhone())) {
            needCheckCode = true;
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            needCheckCode = true;
        }

        if (needCheckCode) {
            if (code == null || code.isEmpty()) {
                return Result.error("修改手机号/密码必须输入验证码");
            }
            String savedCode = codeMap.get(user.getPhone());
            if (savedCode == null || !savedCode.equals(code)) {
                return Result.error("验证码错误");
            }
            codeMap.remove(user.getPhone());
        }

        if (user.getUsername() != null) dbUser.setUsername(user.getUsername());
        if (user.getPhone() != null) dbUser.setPhone(user.getPhone());
        if (user.getDescription() != null) dbUser.setDescription(user.getDescription());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            dbUser.setPassword(user.getPassword());
        }

        userService.updateById(dbUser);
        return Result.success("保存成功");
    }
    /*暂停
    @GetMapping("/save-tag")
    public Result saveUserTag(
            @RequestParam Integer userId,
            @RequestParam String tag
    ) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return Result.error("用户不存在");

        String oldTags = user.getUserTags() == null ? "" : user.getUserTags();
        Set<String> tagSet = new HashSet<>(Arrays.asList(oldTags.split(",")));
        tagSet.add(tag);

        String newTags = String.join(",", tagSet);
        userRepository.updateUserTags(userId, newTags);

        return Result.success("记录成功");
    }*/
}