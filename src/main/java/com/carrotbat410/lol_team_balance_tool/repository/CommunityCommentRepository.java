package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.CommunityCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityCommentEntity, Long> {

    List<CommunityCommentEntity> findByPostNoOrderByNoAsc(Long postNo);

    long countByPostNo(Long postNo);

    long deleteByPostNoIn(Collection<Long> postNos);

    long deleteByWriterId(String writerId);
}
