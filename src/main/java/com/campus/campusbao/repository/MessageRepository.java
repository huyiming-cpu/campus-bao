package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {


        List<Message> findByTouserid(Integer toUserId);
        List<Message> findByFromuseridAndTouserid(Integer from, Integer to);
        List<Message> findByFromuserid(Integer userId);
    List<Message> findByFromuseridAndTouseridAndIsread(Integer from, Integer to, Integer isread);
}