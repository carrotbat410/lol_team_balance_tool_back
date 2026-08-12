package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {

    @Query("""
            select new com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostResponseDTO(
                post.no, post.category, post.title, post.content, post.writerId, post.viewCount,
                post.created_at, post.updated_at
            )
            from CommunityPostEntity post
            where post.category = :category
            order by post.no desc
            """)
    @Transactional(readOnly = true)
    List<CommunityPostResponseDTO> findByCategoryOrderByNoDesc(CommunityPostCategory category);

    @Query(
            value = """
                    select new com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostResponseDTO(
                        post.no, post.category, post.title, post.content, post.writerId, post.viewCount,
                        post.created_at, post.updated_at
                    )
                    from CommunityPostEntity post
                    where post.category = :category
                    order by post.no desc
                    """,
            countQuery = """
                    select count(post)
                    from CommunityPostEntity post
                    where post.category = :category
                    """
    )
    @Transactional(readOnly = true)
    Page<CommunityPostResponseDTO> findByCategoryOrderByNoDesc(CommunityPostCategory category, Pageable pageable);

    List<CommunityPostEntity> findByWriterId(String writerId);

    long deleteByWriterId(String writerId);
}
