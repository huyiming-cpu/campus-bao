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
import com.campus.campusbao.entity.Product;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.ProductRepository;
import com.campus.campusbao.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.campus.campusbao.entity.Cart;
import com.campus.campusbao.entity.Collect;
import com.campus.campusbao.repository.CartRepository;
import com.campus.campusbao.repository.CollectRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.HashMap;
@RestController

@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CollectRepository collectRepository;

    public ProductController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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

        product.setStatus(0);
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

    // 9.推荐商品算法
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
    public Result deleteCart(Integer id) {  // 方法名从 delete → deleteCart
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
    // 获取自己的所有在售商品（用于创建礼包）
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
}