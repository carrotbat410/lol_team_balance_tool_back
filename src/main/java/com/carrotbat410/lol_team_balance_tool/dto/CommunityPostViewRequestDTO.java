package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityPostViewRequestDTO {

    @NotBlank
    @Size(max = 80)
    @Pattern(regexp = "^[A-Za-z0-9_-]+$")
    private String visitorId;
}
