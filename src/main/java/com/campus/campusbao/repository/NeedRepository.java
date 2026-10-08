package com.campus.campusbao.repository;

import com.campus.campusbao.entity.Need;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NeedRepository extends JpaRepository<Need, Integer> {

    // 查询所有需求按时间倒序
    List<Need> findAllByOrderByCreateTimeDesc();

    // 查询用户最近发布的指定类型需求
    @Query("SELECT n FROM Need n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createTime DESC")
    List<Need> findRecentByUserIdAndType(@Param("userId") Integer userId, @Param("type") String type);

    // 按分类匹配（排除自己）
    @Query("SELECT n FROM Need n WHERE n.type IN :types AND n.category = :category AND n.userId != :userId")
    List<Need> findByTypeInAndCategoryAndUserIdNot(@Param("types") List<String> types,
                                                   @Param("category") String category,
                                                   @Param("userId") Integer userId);

    // 按标题关键词匹配（排除自己）
    @Query("SELECT n FROM Need n WHERE n.type IN :types AND n.title LIKE %:keyword% AND n.userId != :userId")
    List<Need> findByTypeInAndTitleContainingAndUserIdNot(@Param("types") List<String> types,
                                                          @Param("keyword") String keyword,
                                                          @Param("userId") Integer userId);
    // 查询用户所有指定类型的需求
    List<Need> findByUserIdAndType(Integer userId, String type);


}