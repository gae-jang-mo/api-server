package com.gaejangmo.apiserver.model.user.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String motto;
    private String imageUrl;
    private String introduce;

    @Builder
    public UserResponseDto(final Long id, final String username, final String email, final String motto, final String imageUrl, final String introduce) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.motto = motto;
        this.imageUrl = imageUrl;
        this.introduce = introduce;
    }
}

