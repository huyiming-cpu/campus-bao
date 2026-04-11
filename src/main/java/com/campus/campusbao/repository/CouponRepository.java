package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findByIsActive(Integer isActive);
}
