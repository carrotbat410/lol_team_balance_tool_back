package com.carrotbat410.lol_team_balance_tool.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@ToString
@Table(name = "summoners")
public class SummonerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    private String userId;
    private String summonerName;
    private String tagLine;
    @Enumerated(EnumType.STRING)
    private Tier tier;
    private Integer rank1;
    private int mmr;
    private int level;
    private int wins;
    private int losses;
    private int iconId;
}
