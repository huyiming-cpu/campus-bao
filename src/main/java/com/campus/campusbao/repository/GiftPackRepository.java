package com.campus.campusbao.repository;

import com.campus.campusbao.entity.GiftPack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftPackRepository extends JpaRepository<GiftPack, Integer> {
    List<GiftPack> findByTypeAndIsActiveOrderByCreateTimeDesc(String type, Integer isActive);
    List<GiftPack> findBySellerId(Integer sellerId);
}