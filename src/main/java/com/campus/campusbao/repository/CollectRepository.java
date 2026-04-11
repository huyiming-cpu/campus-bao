package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Collect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectRepository extends JpaRepository<Collect, Integer> {
    Collect findByUserIdAndProductId(Integer userId, Integer productId);

    List<Collect> findByUserId(Integer userId);
}