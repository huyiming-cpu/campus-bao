package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Optional<Wallet> findByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount, w.updateTime = CURRENT_TIMESTAMP WHERE w.userId = :userId")
    int deductBalance(Integer userId, BigDecimal amount);

    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount, w.updateTime = CURRENT_TIMESTAMP WHERE w.userId = :userId")
    int addBalance(Integer userId, BigDecimal amount);

}