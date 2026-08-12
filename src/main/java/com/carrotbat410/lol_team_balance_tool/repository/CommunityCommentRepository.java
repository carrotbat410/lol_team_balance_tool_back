package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityCommentResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityCommentEntity, Long> {

    @Query("""
            select new com.carrotbat410.lol_team_balance_tool.dto.response.CommunityCommentResponseDTO(
                comment.no, comment.postNo, comment.content, comment.writerId,
                comment.created_at, comment.updated_at
            )
            from CommunityCommentEntity comment
            where comment.postNo = :postNo
            order by comment.no asc
            """)
    @Transactional(readOnly = true)
    List<CommunityCommentResponseDTO> findByPostNoOrderByNoAsc(Long postNo);

    long countByPostNo(Long postNo);

    long deleteByPostNoIn(Collection<Long> postNos);

    long deleteByWriterId(String writerId);
}
