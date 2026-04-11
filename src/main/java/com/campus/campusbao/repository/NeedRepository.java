package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Need;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NeedRepository extends JpaRepository<Need, Integer> {
    List<Need> findAllByOrderByCreateTimeDesc();
}