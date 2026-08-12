package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityCommentResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityCommentEntity;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class CommunityProjectionRepositoryTest {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private CommunityCommentRepository communityCommentRepository;

    @Test
    void projectsPostPageWithDescendingOrderAndPageMetadata() {
        CommunityPostEntity first = savePost("첫 게시글");
        CommunityPostEntity second = savePost("둘째 게시글");

        Page<CommunityPostResponseDTO> result = communityPostRepository
                .findByCategoryOrderByNoDesc(CommunityPostCategory.RECRUIT, PageRequest.of(0, 1));

        assertThat(result.getContent()).singleElement().satisfies(post -> {
            assertThat(post.getNo()).isEqualTo(second.getNo());
            assertThat(post.getTitle()).isEqualTo("둘째 게시글");
            assertThat(post.getCategory()).isEqualTo(CommunityPostCategory.RECRUIT);
            assertThat(post.getWriterId()).isEqualTo("projection-user");
            assertThat(post.getCreatedAt()).isNotNull();
            assertThat(post.getUpdatedAt()).isNotNull();
        });
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isZero();
        assertThat(first.getNo()).isLessThan(second.getNo());
    }

    @Test
    void projectsCommentsWithAscendingOrder() {
        CommunityPostEntity post = savePost("댓글 게시글");
        CommunityCommentEntity first = saveComment(post.getNo(), "첫 댓글");
        CommunityCommentEntity second = saveComment(post.getNo(), "둘째 댓글");

        List<CommunityCommentResponseDTO> result = communityCommentRepository
                .findByPostNoOrderByNoAsc(post.getNo());

        assertThat(result).extracting(CommunityCommentResponseDTO::getNo)
                .containsExactly(first.getNo(), second.getNo());
        assertThat(result).extracting(CommunityCommentResponseDTO::getContent)
                .containsExactly("첫 댓글", "둘째 댓글");
        assertThat(result).allSatisfy(comment -> {
            assertThat(comment.getPostNo()).isEqualTo(post.getNo());
            assertThat(comment.getWriterId()).isEqualTo("projection-user");
            assertThat(comment.getCreatedAt()).isNotNull();
            assertThat(comment.getUpdatedAt()).isNotNull();
        });
    }

    private CommunityPostEntity savePost(String title) {
        CommunityPostEntity post = new CommunityPostEntity();
        post.setCategory(CommunityPostCategory.RECRUIT);
        post.setTitle(title);
        post.setContent("내용");
        post.setWriterId("projection-user");
        post.setViewCount(0);
        return communityPostRepository.saveAndFlush(post);
    }

    private CommunityCommentEntity saveComment(Long postNo, String content) {
        CommunityCommentEntity comment = new CommunityCommentEntity();
        comment.setPostNo(postNo);
        comment.setContent(content);
        comment.setWriterId("projection-user");
        return communityCommentRepository.saveAndFlush(comment);
    }
}
