package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Coupon;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.entity.UserCoupon;
import com.campus.campusbao.repository.CouponRepository;
import com.campus.campusbao.repository.UserCouponRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    // 获取可用优惠券列表
    @GetMapping("/list")
    public Result getList() {
        List<Coupon> coupons = couponRepository.findByIsActive(1);
        return Result.success(coupons);
    }

    // 获取今日剩余抽奖次数
    @GetMapping("/remain")
    public Result getRemainDraw(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 今日已抽次数
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        int todayCount = userCouponRepository.countByUserIdAndGetTimeAfter(loginUser.getId(), start);
        int remain = Math.max(0, 2 - todayCount);  // 每天2次

        return Result.success(remain);
    }

    // 抽奖
    @PostMapping("/draw")
    public Result draw(@RequestParam(required = false) Integer couponId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 检查今日剩余次数
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        int todayCount = userCouponRepository.countByUserIdAndGetTimeAfter(loginUser.getId(), start);
        if (todayCount >= 2) {
            return Result.error("今日抽奖次数已用完");
        }

        Coupon coupon;
        if (couponId != null) {
            // 前端指定了券ID
            coupon = couponRepository.findById(couponId).orElse(null);
            if (coupon == null) {
                return Result.error("优惠券不存在");
            }
        } else {
            // 随机抽取
            List<Coupon> coupons = couponRepository.findByIsActive(1);
            if (coupons.isEmpty()) {
                return Result.error("暂无优惠券");
            }
            Random random = new Random();
            coupon = coupons.get(random.nextInt(coupons.size()));
        }

        // 减库存
        coupon.setStock(coupon.getStock() - 1);
        couponRepository.save(coupon);

        // 记录领取
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(loginUser.getId());
        userCoupon.setCouponId(coupon.getId());
        userCoupon.setStatus("unused");
        userCoupon.setGetTime(LocalDateTime.now());
        userCoupon.setExpireTime(LocalDateTime.now().plusDays(30));
        userCouponRepository.save(userCoupon);

        return Result.success(coupon);
    }
    // 获取我的优惠券
    @GetMapping("/my")
    public Result getMyCoupons(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndStatus(loginUser.getId(), "unused");
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponRepository.findById(uc.getCouponId()).orElse(null);
            if (coupon != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", uc.getId());
                item.put("coupon", coupon);
                item.put("expireTime", uc.getExpireTime());
                result.add(item);
            }
        }
        return Result.success(result);
    }
    // 使用优惠券（创建订单时调用）
    @PostMapping("/use")
    public Result useCoupon(@RequestParam Integer userCouponId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        UserCoupon userCoupon = userCouponRepository.findById(userCouponId).orElse(null);
        if (userCoupon == null || !userCoupon.getUserId().equals(loginUser.getId())) {
            return Result.error("优惠券不存在");
        }

        userCoupon.setStatus("used");
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponRepository.save(userCoupon);

        return Result.success("使用成功");
    }
}