package com.PBL.lecture.dto;

import com.PBL.user.User;
import lombok.Builder;
import lombok.Data;

/**
 * 작성자 정보 DTO
 */
@Data
@Builder
public class AuthorInfo {
    private Long id;
    private String username;
    private String loginId;

    public static AuthorInfo from(User user) {
        return  AuthorInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .loginId(user.getLoginId())
                .build();
    }



}
