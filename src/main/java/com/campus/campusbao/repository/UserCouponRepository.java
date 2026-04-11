package com.campus.campusbao.repository;

import com.campus.campusbao.entity.UserCoupon;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    List<UserCoupon> findByUserIdAndStatus(Integer userId, String status);
    boolean existsByUserIdAndCouponId(Integer userId, Integer couponId);
    int countByUserIdAndGetTimeAfter(Integer userId, LocalDateTime time);

    @Modifying
    @Transactional
    @Query("UPDATE UserCoupon uc SET uc.status = :status, uc.useTime = :useTime WHERE uc.id = :id")
    int updateStatus(@Param("id") Integer id, @Param("status") String status, @Param("useTime") LocalDateTime useTime);
}