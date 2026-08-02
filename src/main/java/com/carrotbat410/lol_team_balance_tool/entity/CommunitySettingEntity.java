package com.carrotbat410.lol_team_balance_tool.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "community_settings")
public class CommunitySettingEntity {

    @Id
    private Integer id;

    @Column(name = "visible_to_users", nullable = false)
    private boolean visibleToUsers;

    @Column(name = "notice_display_count", nullable = false)
    private int noticeDisplayCount;
}
