package com.carrotbat410.lol_team_balance_tool.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "community_posts")
public class CommunityPostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CommunityPostCategory category;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String writerId;

    @Column(nullable = false)
    private long viewCount;
}
