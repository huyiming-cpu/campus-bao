package com.campus.campusbao.repository;

import com.campus.campusbao.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Integer> {
    List<TransactionLog> findByUserIdOrderByCreateTimeDesc(Integer userId);
}