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

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsernameAndPassword(String username, String password);
    User findByUsernameAndPhone(String username, String phone);
    boolean existsByStudentId(String studentId);
    boolean existsByCardId(String studentId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userTags = :tags WHERE u.id = :userId")
    void updateUserTags(Integer userId, String tags);
    @Query("SELECT u FROM User u WHERE u.username != 'admin' ORDER BY u.creditScore DESC")
    List<User> findAllByOrderByCreditScoreDesc();
}