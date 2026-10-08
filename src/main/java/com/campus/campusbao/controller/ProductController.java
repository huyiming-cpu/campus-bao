/*package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Product;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 获取我的商品
    @GetMapping("/my")
    public Result my(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        List<Product> list = productRepository.findByUser_Id(loginUser.getId());
        return Result.success(list);
    }

    // 下架商品
    @PostMapping("/off/{id}")
    public Result off(@PathVariable Integer id) {
        Product p = productRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus(2);
            productRepository.save(p);
        }
        return Result.success("已下架");
    }

    // 删除商品
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        productRepository.deleteById(id);
        return Result.success("删除成功");
    }

    // 更新商品
    @PostMapping("/update")
    public Result update(@RequestBody Product product) {
        Product p = productRepository.findById(product.getId()).orElse(null);
        if (p == null) return Result.error("商品不存在");
        p.setName(product.getName());
        p.setPrice(product.getPrice());
        p.setType(product.getType());
        p.setInfo(product.getInfo());
        productRepository.save(p);
        return Result.success("更新成功");
    }

    // 发布商品
    @PostMapping("/add")
    public Result add(@RequestBody Product product, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");

        User user = new User();
        user.setId(loginUser.getId());
        product.setUser(user);

        product.setStatus(0);
        product.setHot(0);
        productRepository.save(product);
        return Result.success("发布成功");
    }

    // 图片上传
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/products/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = file.getOriginalFilename();
            File targetFile = new File(uploadDir + fileName);
            if (targetFile.exists()) targetFile.delete();

            file.transferTo(targetFile.toPath());
            return Result.success(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    // 首页商品列表（正常显示所有商品！）
    @GetMapping("/list")
    public Result list() {
        List<Product> list = productRepository.findByStatus(0);
        list.sort((a, b) -> b.getHot() - a.getHot());

        // 👇👇👇 强制把用户信息（含头像）查询出来！
        for (Product p : list) {
            User user = p.getUser();
            if (user != null) {
                // 强制加载头像
                user.getAvatar();
            }
        }

        return Result.success(list);
    }

    // 我的宝贝（出售中 / 已买到）
    @GetMapping("/my/list")
    public Result myProductList(@RequestParam String type, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<Product> list;
        if ("sell".equals(type)) {
            list = productRepository.findByUser_IdAndBuyerIdIsNull(loginUser.getId());
        } else if ("buy".equals(type)) {
            list = productRepository.findByBuyerId(loginUser.getId());
        } else {
            return Result.error("类型错误");
        }
        return Result.success(list);
    }

}*/
package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.*;
import com.campus.campusbao.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import com.campus.campusbao.repository.NeedRepository;
@RestController

@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductClickRepository productClickRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CollectRepository collectRepository;
    @Autowired
    private NeedRepository needRepository;


    public ProductController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    // 同义词映射（可以放到配置文件或数据库）
    private static final Map<String, List<String>> SYNONYMS = new HashMap<>();

    static {
        SYNONYMS.put("电脑", Arrays.asList("笔记本", "PC", "计算机", "macbook"));
        SYNONYMS.put("手机", Arrays.asList("电话", "iphone", "华为", "小米"));
        SYNONYMS.put("充电宝", Arrays.asList("移动电源", "充电器"));
        SYNONYMS.put("鞋", Arrays.asList("运动鞋", "板鞋", "跑鞋"));
        SYNONYMS.put("书", Arrays.asList("教材", "课本", "书籍"));
        SYNONYMS.put("自行车", Arrays.asList("单车", "山地车", "通勤车"));
    }
    // 1.获取我的商品
    @GetMapping("/my")
    public Result my(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        List<Product> list = productRepository.findByUser_Id(loginUser.getId());
        return Result.success(list);
    }
//2.下架商品
    @PostMapping("/off/{id}")
    public Result off(@PathVariable Integer id) {
        Product p = productRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus(2);
            productRepository.save(p);
        }
        return Result.success("已下架");
    }
//3.删除商品
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        productRepository.deleteById(id);
        return Result.success("删除成功");
    }
//4.更新商品
    @PostMapping("/update")
    public Result update(@RequestBody Product product) {
        Product p = productRepository.findById(product.getId()).orElse(null);
        if (p == null) return Result.error("商品不存在");
        p.setName(product.getName());
        p.setPrice(product.getPrice());
        p.setType(product.getType());
        p.setInfo(product.getInfo());
        p.setHot(product.getHot());
        productRepository.save(p);
        return Result.success("更新成功");
    }
//5.添加商品
    @PostMapping("/add")
    public Result add(@RequestBody Product product, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");

        User user = new User();
        user.setId(loginUser.getId());
        product.setUser(user);

        product.setStatus(3);
        product.setHot(0);
        productRepository.save(product);
        return Result.success("发布成功");
    }
//6.上传商品图片
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/products/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = file.getOriginalFilename();
            File targetFile = new File(uploadDir + fileName);
            if (targetFile.exists()) targetFile.delete();

            file.transferTo(targetFile.toPath());
            return Result.success(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
//商品列表
 /*  @GetMapping("/list")
    public Result list(HttpSession session) {
        // 查所有在售商品（热门+非热门 全部！）
        List<Product> list = productRepository.findByStatus(0);
        // 过滤自己发布的商品
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            Integer uid = loginUser.getId();
            list = list.stream()
                    .filter(p -> p.getUser() == null || !p.getUser().getId().equals(uid))
                    .toList();
        }
        for (Product p : list) {
            if (p.getUser() != null) {
                p.getUser().getAvatar();
            }
        }
        return Result.success(list);
    }
*/
//7.首页商品列表展示
@GetMapping("/list")
public Result list(HttpSession session) {
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        return Result.error("请先登录");
    }

    List<Product> allProducts = productRepository.findByStatus(0);
    List<Product> list = new ArrayList<>();
    // 管理员：看到所有商品
    if ("admin".equals(loginUser.getUsername())) {
        list = allProducts.stream()
                .filter(p -> p.getUser() != null)
                .toList();
    } else {
        // 普通用户：只看同校，过滤自己
        String myUniversity = loginUser.getUniversity();
        list = allProducts.stream()
                .filter(p -> p.getUser() != null)
                .filter(p -> !p.getUser().getId().equals(loginUser.getId()))
                .toList();
    }
    // 加载用户信息
    for (Product p : list) {
        if (p.getUser() != null) {
            p.getUser().getAvatar();
        }
    }
    return Result.success(list);
}
//8.我的宝贝
    @GetMapping("/my/list")
    public Result myProductList(@RequestParam String type, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<Product> list;
        if ("sell".equals(type)) {
            list = productRepository.findByUserId(loginUser.getId());
        } else if ("buy".equals(type)) {
            list = productRepository.findByBuyerId(loginUser.getId());
        } else {
            return Result.error("类型错误");
        }
        return Result.success(list);
    }

   /* // 9.推荐商品算法
    @GetMapping("/recommend")
    public Result recommend(@RequestParam Integer userId, HttpSession session) {
        List<Product> all = productRepository.findByStatus(0);
        User user = userRepository.findById(userId).orElse(null);
        List<Product> result = new ArrayList<>();
        // 热门
        List<Product> hotList = all.stream().filter(p -> p.getHot() == 1).toList();
        result.addAll(hotList);
        // 搜索过的类型，叠加推荐，
        if (user != null && user.getUserTags() != null && !user.getUserTags().isEmpty()) {
            String[] tags = user.getUserTags().split(",");
            for (String tag : tags) {
                int count = 0;
                for (Product p : all) {
                    if (tag.equals(p.getType())) {
                        result.add(p);
                        count++;
                        if (count >= 2) break;
                    }
                }
            }
        }
        // 去重
        Map<Integer, Product> map = new LinkedHashMap<>();
        for (Product p : result) map.put(p.getId(), p);
        List<Product> finalList = new ArrayList<>(map.values());

        // 过滤自己
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            Integer uid = loginUser.getId();
            finalList = finalList.stream()
                    .filter(p -> p.getUser() == null || !p.getUser().getId().equals(uid))
                    .toList();
        }
        return Result.success(finalList);
    }*/
   @GetMapping("/recommend")
   public Result recommend(@RequestParam Integer userId, HttpSession session) {
       List<Product> all = productRepository.findByStatus(0);
       User user = userRepository.findById(userId).orElse(null);
       Map<Integer, Product> resultMap = new LinkedHashMap<>();

       // 1. 热门商品
       List<Product> hotList = all.stream()
               .filter(p -> p.getHot() == 1)
               .collect(Collectors.toList());
       for (Product p : hotList) {
           resultMap.put(p.getId(), p);
       }

       // 2. 搜索过的类型推荐
       if (user != null && user.getUserTags() != null && !user.getUserTags().isEmpty()) {
           String[] tags = user.getUserTags().split(",");
           for (String tag : tags) {
               int count = 0;
               for (Product p : all) {
                   if (count >= 2) break;
                   if (tag.equals(p.getType()) && !resultMap.containsKey(p.getId())) {
                       resultMap.put(p.getId(), p);
                       count++;
                   }
               }
           }
       }

       // 3. 新增：点击≥3次的类型推荐
       List<String> frequentTypes = productClickRepository.findFrequentClickTypes(userId);
       for (String type : frequentTypes) {
           for (Product p : all) {
               if (type.equals(p.getType()) && !resultMap.containsKey(p.getId())) {
                   resultMap.put(p.getId(), p);
                   break;  // 每个类型只加1个
               }
           }
       }

       // 过滤自己
       User loginUser = (User) session.getAttribute("loginUser");
       if (loginUser != null) {
           Integer uid = loginUser.getId();
           List<Product> finalList = resultMap.values().stream()
                   .filter(p -> p.getUser() == null || !p.getUser().getId().equals(uid))
                   .collect(Collectors.toList());
           return Result.success(finalList);
       }

       return Result.success(new ArrayList<>(resultMap.values()));
   }
    // 10.商品详情页
    @GetMapping("/detail/{id}")
    public Result getProductDetail(@PathVariable Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 加载卖家信息
        if (product.getUser() != null) {
            product.getUser().getAvatar();
        }
        return Result.success(product);
    }
    //11.加入购物车
    @PostMapping("/cart/add")
    public Map<String, Object> addCart(Integer userId, Integer productId) {
        Map<String, Object> map = new HashMap<>();
        Cart old = cartRepository.findByUserIdAndProductId(userId, productId);
        if (old != null) {
            map.put("msg", "✅已在购物车");
            return map;
        }
        Cart c = new Cart();
        c.setUserId(userId);
        c.setProductId(productId);
        c.setCreateTime(LocalDateTime.now());
        cartRepository.save(c);
        map.put("msg", "✅加入购物车成功");
        return map;
    }
//12.收藏
    @PostMapping("/collect/toggle")
    public Map<String, Object> collect(Integer userId, Integer productId) {
        Map<String, Object> map = new HashMap<>();
        Collect old = collectRepository.findByUserIdAndProductId(userId, productId);
        if (old != null) {
            collectRepository.delete(old);
            map.put("msg", "取消收藏");
            return map;
        }
        Collect c = new Collect();
        c.setUserId(userId);
        c.setProductId(productId);
        c.setCreateTime(LocalDateTime.now());
        collectRepository.save(c);
        map.put("msg", "✅收藏成功");
        return map;
    }

    //13.我的购物车
    @GetMapping("/cart/my")
    public Result myCart(Integer userId) {
        // 1. 先查购物车
        List<Cart> carts = cartRepository.findByUserId(userId);
        // 2. 遍历购物车，关联商品信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cart.getId());
                map.put("product", product);
                result.add(map);
            }
        }
        return Result.success(result);
    }

    // 14.购物车删除
    @GetMapping("/cart/delete")
    public Result deleteCart(Integer id) {
        cartRepository.deleteById(id);
        return Result.success("✅删除成功");
    }

   //15. 我的收藏
@GetMapping("/collect/my")
public Result myCollect(Integer userId) {
    List<Collect> collects = collectRepository.findByUserId(userId);
    List<Map<String, Object>> result = new ArrayList<>();

    for (Collect collect : collects) {
        Product product = productRepository.findById(collect.getProductId()).orElse(null);
        if (product != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", collect.getId());
            map.put("product", product);
            result.add(map);
        }
    }
    return Result.success(result);
}

    // 16.取消收藏
    @GetMapping("/collect/deleteCollect")
    public Result deleteCollect(Integer id) {
        collectRepository.deleteById(id);
        return Result.success("✅取消收藏成功");
    }
    // 17. 管理员查看指定用户的商品
    @GetMapping("/admin/user/products")
    public Result getUserProducts(@RequestParam Integer userId) {
        // ✅ 用 findByUser_Id 替代
        List<Product> list = productRepository.findByUser_Id(userId);
        for (Product p : list) {
            if (p.getUser() != null) {
                p.getUser().getAvatar();
            }
        }
        return Result.success(list);
    }
    // 18.获取自己的所有在售商
    @GetMapping("/my/all")
    public Result getMyAllProducts(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // ✅ 只返回在售商品（status = 0）
        List<Product> list = productRepository.findByUserIdAndStatus(loginUser.getId(), 0);
        for (Product p : list) {
            if (p.getUser() != null) {
                p.getUser().getAvatar();
            }
        }
        return Result.success(list);
    }
    // 19.记录商品点击
    @PostMapping("/click")
    public Result recordClick(@RequestParam Integer productId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        ProductClick existing = productClickRepository.findByUserIdAndProductId(loginUser.getId(), productId);
        if (existing != null) {
            productClickRepository.incrementClickCount(loginUser.getId(), productId);
        } else {
            ProductClick click = new ProductClick();
            click.setUserId(loginUser.getId());
            click.setProductId(productId);
            click.setClickCount(1);
            click.setUpdateTime(java.time.LocalDateTime.now());
            productClickRepository.save(click);
        }

        return Result.success("记录成功");
    }

    // 20.获取点击≥3次的商品类型
    @GetMapping("/click/types")
    public Result getFrequentClickTypes(@RequestParam Integer userId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        List<String> types = productClickRepository.findFrequentClickTypes(userId);
        return Result.success(types);
    }
    //21. 获取点击≥3次的商品ID列表
    @GetMapping("/click/productIds")
    public Result getFrequentClickProductIds(@RequestParam Integer userId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        List<Integer> ids = productClickRepository.findFrequentClickProductIds(userId);
        return Result.success(ids);
    }
    // 22.清除用户的点击记录
    @PostMapping("/click/clear")
    public Result clearClickRecords(@RequestParam Integer userId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getId().equals(userId)) {
            return Result.error("无权限");
        }

        List<ProductClick> records = productClickRepository.findByUserId(userId);
        productClickRepository.deleteAll(records);
        return Result.success("清除成功");
    }
    //23. 随机获取N个在售商品
    private List<Product> getRandomProducts(Integer currentProductId, int limit) {
        List<Product> all = productRepository.findByStatus(0);
        List<Product> candidates = new ArrayList<>();
        for (Product p : all) {
            if (!p.getId().equals(currentProductId)) {
                candidates.add(p);
            }
        }
        Collections.shuffle(candidates);
        return candidates.stream().limit(limit).collect(Collectors.toList());
    }
    //24. 根据用户购买历史推荐同类商品
    @GetMapping("/recommend/byPurchase")
    public Result recommendByPurchase(@RequestParam Integer currentProductId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.success(getRandomProducts(currentProductId, 10));
        }

        // 1. 查询用户已完成的订单中的商品类型
        List<Order> completedOrders = orderRepository.findByBuyerIdAndOrderStatus(loginUser.getId(), "completed");
        Set<String> purchasedTypes = new HashSet<>();
        for (Order order : completedOrders) {
            Product orderedProduct = productRepository.findById(order.getProductId()).orElse(null);
            if (orderedProduct != null && orderedProduct.getType() != null) {
                purchasedTypes.add(orderedProduct.getType());
            }
        }

        // 2. 如果没有购买记录 → 随机推荐
        if (purchasedTypes.isEmpty()) {
            return Result.success(getRandomProducts(currentProductId, 10));
        }

        // 3. 根据这些类型推荐同类商品
        Set<Integer> excludeIds = new HashSet<>();
        excludeIds.add(currentProductId);
        List<Product> recommendList = new ArrayList<>();

        for (String type : purchasedTypes) {
            // 获取该类型下所有符合条件的商品
            List<Product> sameTypeProducts = productRepository.findByTypeAndStatusAndUserIdNot(type, 0, loginUser.getId());

            // 过滤掉已添加的和当前商品
            List<Product> available = new ArrayList<>();
            for (Product p : sameTypeProducts) {
                if (!excludeIds.contains(p.getId())) {
                    available.add(p);
                }
            }

            //  随机打乱后取前2个
            Collections.shuffle(available);
            for (int i = 0; i < Math.min(2, available.size()) && recommendList.size() < 10; i++) {
                recommendList.add(available.get(i));
                excludeIds.add(available.get(i).getId());
            }

            if (recommendList.size() >= 10) break;
        }

        // 4. 如果还不够10个，用随机商品补全
        if (recommendList.size() < 10) {
            List<Product> randomProducts = getRandomProducts(currentProductId, 10 - recommendList.size());
            for (Product p : randomProducts) {
                if (!excludeIds.contains(p.getId())) {
                    recommendList.add(p);
                }
            }
        }

        // 打乱一次最终顺序
        Collections.shuffle(recommendList);
        return Result.success(recommendList);
    }
    // 搜索联想 - 输入前缀返回相关商品名称
    @GetMapping("/search/suggest")
    public Result searchSuggest(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 查询商品名称包含关键词的商品（取前10个）
        List<Product> products = productRepository.findByNameContaining(keyword);

        // 返回商品名称列表
        List<String> suggestions = products.stream()
                .map(Product::getName)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        return Result.success(suggestions);
    }
    // 智能搜索
    @GetMapping("/search/intelligent")
    public Result intelligentSearch(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        Set<String> searchWords = new HashSet<>();
        searchWords.add(keyword);

        // 添加同义词
        for (Map.Entry<String, List<String>> entry : SYNONYMS.entrySet()) {
            if (entry.getKey().contains(keyword) || keyword.contains(entry.getKey())) {
                searchWords.add(entry.getKey());
                searchWords.addAll(entry.getValue());
            }
            for (String syn : entry.getValue()) {
                if (syn.contains(keyword) || keyword.contains(syn)) {
                    searchWords.add(entry.getKey());
                    searchWords.addAll(entry.getValue());
                }
            }
        }

        // 搜索匹配的商品
        List<Product> results = new ArrayList<>();
        for (String word : searchWords) {
            List<Product> products = productRepository.findByNameContaining(word);
            for (Product p : products) {
                if (!results.contains(p)) {
                    results.add(p);
                }
            }
        }

        return Result.success(results);
    }
    // 管理员审核商品
    @PostMapping("/admin/audit")
    public Result auditProduct(@RequestParam Integer productId,
                               @RequestParam Integer auditStatus,  // 1通过，2驳回
                               @RequestParam(required = false) String rejectReason,
                               HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return Result.error("商品不存在");
        }

        if (auditStatus == 1) {
            product.setStatus(0);  // 审核通过 → 出售中
        } else {
            product.setStatus(2);  // 审核驳回 → 下架
        }
        productRepository.save(product);

        return Result.success(auditStatus == 1 ? "审核通过" : "已驳回");
    }
    @GetMapping("/admin/pending")
    public Result getPendingProducts(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        // 查询status=3的商品（审核中）
        List<Product> list = productRepository.findByStatus(3);
        for (Product p : list) {
            if (p.getUser() != null) {
                p.getUser().getAvatar();
            }
        }
        return Result.success(list);
    }
    // 管理员获取所有商品（包括待审核、已下架等）
    @GetMapping("/admin/list")
    public Result getAdminProductList(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权限");
        }

        // 管理员看到所有商品
        List<Product> allProducts = productRepository.findAll();
        for (Product p : allProducts) {
            if (p.getUser() != null) {
                p.getUser().getAvatar();
            }
        }
        return Result.success(allProducts);
    }
}