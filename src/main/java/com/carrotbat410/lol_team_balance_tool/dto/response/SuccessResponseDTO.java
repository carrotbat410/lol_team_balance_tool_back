package com.carrotbat410.lol_team_balance_tool.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter //* INFO: Spring은 @RestController의 반환 객체를 JSON으로 변환할 때 getter가 없으면 데이터를 읽을 수 없음.
//@JsonInclude(JsonInclude.Include.NON_NULL) // data: null 인값 안보내려면 왼쪽 어노테이션 사용하면됨. 통일성 및 "데이터가 없는 성공"을 의미하기 위해서 그냥 보낼거.
public class SuccessResponseDTO <T>{
    private final String message;
    private final T data;

    public SuccessResponseDTO() {
        this("ok");
    }

    public SuccessResponseDTO(String message){
        this(message, null);
    }

    public SuccessResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
