package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.GiftPack;
import com.campus.campusbao.entity.Product;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.GiftPackRepository;
import com.campus.campusbao.repository.ProductRepository;
import com.campus.campusbao.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/giftPack")
public class GiftPackController {

    @Autowired
    private GiftPackRepository giftPackRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取礼包列表
    @GetMapping("/list")
    public Result getList(@RequestParam String type) {
        List<GiftPack> packs = giftPackRepository.findByTypeAndIsActiveOrderByCreateTimeDesc(type, 1);
        List<GiftPack> validPacks = new ArrayList<>();
        for (GiftPack pack : packs) {
            User seller = userRepository.findById(pack.getSellerId()).orElse(null);
            pack.setSeller(seller);

            // ✅ 获取商品列表用于显示封面图
            if (pack.getProductIds() != null && !pack.getProductIds().isEmpty()) {
                List<Integer> productIds = Arrays.stream(pack.getProductIds().split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                List<Product> products = productRepository.findAllById(productIds);
                pack.setProducts(products);
                boolean allOnSale = products.stream().allMatch(p -> p.getStatus() == 0);
                if (allOnSale) {
                    validPacks.add(pack);
                }
            }
        }

        return Result.success(validPacks);
    }

    // 获取礼包详情
    @GetMapping("/detail/{id}")
    public Result getDetail(@PathVariable Integer id) {
        GiftPack pack = giftPackRepository.findById(id).orElse(null);
        if (pack == null) {
            return Result.error("礼包不存在");
        }

        // 获取卖家信息
        User seller = userRepository.findById(pack.getSellerId()).orElse(null);
        pack.setSeller(seller);

        // 获取商品列表
        List<Integer> productIds = Arrays.stream(pack.getProductIds().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);
        pack.setProducts(products);

        return Result.success(pack);
    }

    // 创建礼包
    @PostMapping("/create")
    public Result create(@RequestBody GiftPack pack, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        pack.setSellerId(loginUser.getId());
        pack.setCreateTime(LocalDateTime.now());
        pack.setIsActive(1);
        giftPackRepository.save(pack);

        return Result.success("创建成功");
    }

    // 删除礼包
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        GiftPack pack = giftPackRepository.findById(id).orElse(null);
        if (pack == null) {
            return Result.error("礼包不存在");
        }
        if (!pack.getSellerId().equals(loginUser.getId()) && !"admin".equals(loginUser.getUsername())) {
            return Result.error("无权删除");
        }
        giftPackRepository.deleteById(id);
        return Result.success("删除成功");
    }
}