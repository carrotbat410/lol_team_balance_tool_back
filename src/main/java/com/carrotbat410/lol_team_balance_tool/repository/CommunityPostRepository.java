package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {

    List<CommunityPostEntity> findByCategoryOrderByNoDesc(CommunityPostCategory category);

    Page<CommunityPostEntity> findByCategoryOrderByNoDesc(CommunityPostCategory category, Pageable pageable);

    List<CommunityPostEntity> findByWriterId(String writerId);

    long deleteByWriterId(String writerId);
}
