package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Need;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.NeedRepository;
import com.campus.campusbao.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.campus.campusbao.entity.Need;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/need")
public class NeedController {

    @Autowired
    private NeedRepository needRepository;

    @Autowired
    private UserRepository userRepository;
    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/need/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            file.transferTo(new File(uploadDir + fileName));
            return Result.success(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
    // 发布需求
    @PostMapping("/publish")
    public Result publish(@RequestBody Need need, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        need.setUserId(loginUser.getId());
        need.setCreateTime(LocalDateTime.now());
        needRepository.save(need);

        return Result.success("发布成功");
    }

    // 获取需求列表
    @GetMapping("/list")
    public Result getList(@RequestParam(required = false) String filter, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        List<Need> needs = needRepository.findAllByOrderByCreateTimeDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Need need : needs) {
            User user = userRepository.findById(need.getUserId()).orElse(null);
            if (user == null) continue;

            // 本校筛选
            if ("school".equals(filter) && loginUser != null) {
                if (!loginUser.getUniversity().equals(user.getUniversity())) {
                    continue;
                }
            }
            // 类型筛选

            Map<String, Object> item = new HashMap<>();
            item.put("id", need.getId());
            item.put("userId", user.getId());
            item.put("username", user.getUsername());
            item.put("avatar", user.getAvatar());
            item.put("university", user.getUniversity());
            item.put("type", need.getType());
            item.put("title", need.getTitle());
            item.put("content", need.getContent());
            item.put("createTime", need.getCreateTime());
            item.put("image", need.getImage());
            result.add(item);
        }

        return Result.success(result);
    }
    // 删除需求
    @DeleteMapping("/delete/{id}")
    public Result deleteNeed(@PathVariable Integer id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        Need need = needRepository.findById(id).orElse(null);
        if (need == null) {
            return Result.error("需求不存在");
        }

        // 只有发布者或管理员可以删除
        if (!need.getUserId().equals(loginUser.getId()) && !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权删除");
        }

        needRepository.deleteById(id);
        return Result.success("删除成功");
    }

}