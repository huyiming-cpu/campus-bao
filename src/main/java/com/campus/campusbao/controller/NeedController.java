package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Need;
import com.campus.campusbao.entity.Product;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.NeedRepository;
import com.campus.campusbao.repository.ProductRepository;
import com.campus.campusbao.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/need")
public class NeedController {

    @Autowired
    private NeedRepository needRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
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
        // category 已经包含在 need 对象中，不需要额外处理
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
            item.put("category", need.getCategory());
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
    // 智能匹配：根据用户发布的需求推荐匹配的闲置/交换
    @GetMapping("/match")
    public Result getMatchRecommendations(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.success(new ArrayList<>());
        }

        // 1. 获取用户最近发布的需求（want类型，取最近3条）
        List<Need> userNeeds = needRepository.findRecentByUserIdAndType(loginUser.getId(), "want");
        if (userNeeds.size() > 3) {
            userNeeds = userNeeds.subList(0, 3);
        }
        if (userNeeds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 2. 收集用户的分类和关键词
        List<String> userCategories = new ArrayList<>();
        List<String> keywords = new ArrayList<>();

        for (Need need : userNeeds) {
            if (need.getCategory() != null && !userCategories.contains(need.getCategory())) {
                userCategories.add(need.getCategory());
            }
            // 从标题提取关键词
            String title = need.getTitle();
            String[] words = title.split("[\\s,，、。！？]+");
            for (String word : words) {
                if (word.length() >= 2) {
                    keywords.add(word);
                }
            }
        }

        // 3. 匹配（优先按分类匹配，其次按关键词）
        List<Need> matches = new ArrayList<>();

        // 3.1 按分类匹配
        for (String category : userCategories) {
            List<Need> found = needRepository.findByTypeInAndCategoryAndUserIdNot(
                    Arrays.asList("have", "exchange"), category, loginUser.getId());
            for (Need need : found) {
                if (!matches.contains(need)) {
                    matches.add(need);
                }
            }
        }

        // 3.2 按关键词匹配（如果分类匹配不够6条）
        if (matches.size() < 6) {
            for (String keyword : keywords) {
                List<Need> found = needRepository.findByTypeInAndTitleContainingAndUserIdNot(
                        Arrays.asList("have", "exchange"), keyword, loginUser.getId());
                for (Need need : found) {
                    if (!matches.contains(need) && matches.size() < 12) {
                        matches.add(need);
                    }
                }
            }
        }

        // 4. 限制最多6条
        matches = matches.stream().limit(6).collect(Collectors.toList());

        // 5. 填充用户信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (Need need : matches) {
            User user = userRepository.findById(need.getUserId()).orElse(null);
            if (user != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", need.getId());
                item.put("userId", user.getId());
                item.put("username", user.getUsername());
                item.put("avatar", user.getAvatar());
                item.put("university", user.getUniversity());
                item.put("type", need.getType());
                item.put("category", need.getCategory());
                item.put("title", need.getTitle());
                item.put("content", need.getContent());
                item.put("createTime", need.getCreateTime());
                item.put("image", need.getImage());
                result.add(item);
            }
        }

        return Result.success(result);
    }
    // 根据用户需求推荐商品 - 平均分配版本
    @GetMapping("/recommend/byNeed")
    public Result recommendByNeed(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.success(new ArrayList<>());
        }

        // 1. 获取用户所有"想要"类型的需求
        List<Need> userNeeds = needRepository.findByUserIdAndType(loginUser.getId(), "want");
        if (userNeeds == null || userNeeds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 2. 按需求分组，每个需求推荐一定数量的商品
        List<Product> allProducts = productRepository.findByStatus(0);
        List<Product> recommendations = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();

        int totalLimit = 12;  // 总共推荐12件
        int needCount = userNeeds.size();
        int perNeed = Math.max(2, totalLimit / needCount);  // 每个需求至少推荐2件

        System.out.println("用户有 " + needCount + " 条需求，每条推荐 " + perNeed + " 件");

        for (Need need : userNeeds) {
            String category = need.getCategory();
            String title = need.getTitle();

            // 提取关键词
            List<String> keywords = new ArrayList<>();
            if (title != null) {
                String[] words = title.split("[\\s,，、。！？]+");
                for (String word : words) {
                    if (word.length() >= 2) {
                        keywords.add(word);
                    }
                }
            }

            int count = 0;

            // 先按分类匹配
            if (category != null && !category.isEmpty()) {
                for (Product p : allProducts) {
                    if (!addedIds.contains(p.getId()) && category.equals(p.getType())) {
                        recommendations.add(p);
                        addedIds.add(p.getId());
                        count++;
                        System.out.println("分类匹配: " + category + " -> " + p.getName());
                        if (count >= perNeed) break;
                    }
                }
            }

            // 再按关键词匹配（如果分类匹配不够）
            if (count < perNeed) {
                for (String keyword : keywords) {
                    for (Product p : allProducts) {
                        if (!addedIds.contains(p.getId()) && p.getName() != null && p.getName().contains(keyword)) {
                            recommendations.add(p);
                            addedIds.add(p.getId());
                            count++;
                            System.out.println("关键词匹配: " + keyword + " -> " + p.getName());
                            if (count >= perNeed) break;
                        }
                    }
                    if (count >= perNeed) break;
                }
            }

            if (recommendations.size() >= totalLimit) break;
        }

        // 如果还不够12条，随便补
        if (recommendations.size() < totalLimit) {
            for (Product p : allProducts) {
                if (!addedIds.contains(p.getId())) {
                    recommendations.add(p);
                    addedIds.add(p.getId());
                    if (recommendations.size() >= totalLimit) break;
                }
            }
        }

        return Result.success(recommendations);
    }
}