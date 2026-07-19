package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitTrackRequestDTO {

    @NotBlank
    @Size(max = 80)
    private String visitorId;

    @Size(max = 500)
    private String path;
}
