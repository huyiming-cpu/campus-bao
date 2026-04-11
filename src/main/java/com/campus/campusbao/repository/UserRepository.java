/*package com.campus.campusbao.repository;

import com.campus.campusbao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsernameAndPassword(String username, String password);
    User findByUsernameAndPhone(String username, String phone);
    boolean existsByStudentId(String studentId);
    // 判断一卡通是否重复

}*/
package com.campus.campusbao.repository;

import com.campus.campusbao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 原有登录方法
    User findByUsernameAndPassword(String username, String password);
    User findByUsernameAndPhone(String username, String phone);
    boolean existsByStudentId(String studentId);
    boolean existsByCardId(String studentId);
    // 根据用户名查询（判断重复）

    // 修复：严格匹配 Integer 类型的更新方法
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userTags = :tags WHERE u.id = :userId")
    void updateUserTags(Integer userId, String tags);
}