package com.sociallogin.sociallogin.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPrincipal {  //토큰에 넣을 때 사용하는 용도 ?
    private int iuser;

    @Builder.Default    //builder 패턴 쓸 때 기본값 설정
    private List<String> roles = new ArrayList<>();
}
