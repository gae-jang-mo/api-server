package com.gaejangmo.apiserver.model.user.service;

import com.gaejangmo.apiserver.model.common.domain.vo.Link;
import com.gaejangmo.apiserver.model.like.domain.Likes;
import com.gaejangmo.apiserver.model.like.service.LikeService;
import com.gaejangmo.apiserver.model.user.domain.User;
import com.gaejangmo.apiserver.model.user.domain.UserRepository;
import com.gaejangmo.apiserver.model.user.domain.vo.Email;
import com.gaejangmo.apiserver.model.user.domain.vo.Grade;
import com.gaejangmo.apiserver.model.user.domain.vo.Motto;
import com.gaejangmo.apiserver.model.user.dto.UserResponseDto;
import com.gaejangmo.apiserver.model.user.dto.UserSearchDto;
import com.gaejangmo.apiserver.model.user.testdata.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    private static final long USER_ID = 100L;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LikeService likeService;

    @Test
    void OauthId_유저_조회() {
        given(userRepository.findByOauthId(anyLong())).willReturn(Optional.of(UserTestData.ENTITY_NOT_INCLUDE_ISLIKED));

        UserResponseDto result = userService.findUserResponseDtoByOauthId(1234L);

        assertThat(result).isEqualTo(UserTestData.RESPONSE_DTO_NOT_INCLUDE_ISLIKED);
    }

    @Test
    void username_유저_조회() {
        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(UserTestData.ENTITY_GENERAL));
        given(likeService.isLiked(anyLong(), anyLong())).willReturn(false);
        User user = UserTestData.ENTITY_GENERAL;

        // when
        UserResponseDto result = userService.findUserResponseDtoByName(user.getUsername(), 2L);

        // then
        assertThat(result).isEqualTo(UserTestData.RESPONSE_DTO);
    }

    @Test
    void username_유저_엔티티_조회() {
        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(UserTestData.ENTITY_GENERAL));

        // when
        User result = userService.findByUsername(UserTestData.ENTITY_GENERAL.getUsername());

        // then
        assertThat(result).isEqualTo(UserTestData.ENTITY_GENERAL);
    }

    @Test
    void username_유저_엔티티_조회_시_예외처리() {
        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername(UserTestData.ENTITY_GENERAL.getUsername()));
    }

    @Test
    void 모토_업데이트() {
        //given
        Motto updatedMotto = Motto.of("updated");
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(UserTestData.ENTITY_GENERAL));

        //when
        UserResponseDto actual = userService.updateMotto(USER_ID, updatedMotto);

        //then
        assertThat(actual.getMotto()).isEqualTo(updatedMotto.value());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void 내가_좋아요를_누른_사람들을_조회() {
        // given
        Likes like = Likes.builder()
                .source(mock(User.class))
                .target(UserTestData.ENTITY_GENERAL)
                .build();

        given(likeService.findAllBySource(anyLong())).willReturn(List.of(like));

        // when
        List<UserResponseDto> actual = userService.findUserResponseDtoBySourceId(1L);

        // then
        assertThat(actual).isEqualTo(
                List.of(UserResponseDto.builder()
                        .id(1L)
                        .oauthId(20608121L)
                        .username("JunHoPark93")
                        .email("abc@gmail.com")
                        .motto("장비충개발자")
                        .imageUrl("https://previews.123rf.com/images/aquir/aquir1311/aquir131100316/23569861-%EC%83%98%ED%94%8C-%EC%A7%80-%EB%B9%A8%EA%B0%84%EC%83%89-%EB%9D%BC%EC%9A%B4%EB%93%9C-%EC%8A%A4%ED%83%AC%ED%94%84.jpg")
                        .introduce("안녕 난 제이")
                        .isLiked(true)
                        .isCelebrity(false)
                        .build()));
        verify(likeService, times(1)).findAllBySource(1L);
    }

    @Test
    void username으로_list_검색() {
        // given
        String username = "username";
        List<User> users = List.of(UserTestData.ENTITY_GENERAL);
        List<UserSearchDto> expected = List.of(
                UserSearchDto.builder()
                        .id(UserTestData.ENTITY_GENERAL.getId())
                        .imageUrl(UserTestData.ENTITY_GENERAL.getImageUrl())
                        .username(UserTestData.ENTITY_GENERAL.getUsername())
                        .motto(UserTestData.ENTITY_GENERAL.getMotto())
                        .isCelebrity(UserTestData.ENTITY_GENERAL.isCelebrity())
                        .isLiked(false)
                        .build());
        when(userRepository.findAllByUsernameContainingIgnoreCase(username)).thenReturn(users);

        // when
        List<UserSearchDto> actual = userService.findUserSearchDtosByUserName(username);

        // then
        assertThat(actual).isEqualTo(expected);
        verify(userRepository).findAllByUsernameContainingIgnoreCase(username);
    }

    @Test
    void username으로_list_검색_empty면_emptyList() {
        // given
        String username = "";
        List<UserSearchDto> expected = Collections.emptyList();
        when(userRepository.findAllByUsernameContainingIgnoreCase(username)).thenReturn(Collections.emptyList());

        // when
        List<UserSearchDto> actual = userService.findUserSearchDtosByUserName(username);

        // then
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, never()).findAllByUsernameContainingIgnoreCase(username);
    }

    @Test
    void 자기소개_수정() {
        // given
        String updatedIntroduce = "안녕 난 수정된 제이";
        User entity = User.builder()
                .oauthId(20608121L)
                .username("JunHoPark93")
                .email(Email.of("abc@gmail.com"))
                .motto(Motto.of("장비충개발자"))
                .imageUrl(Link.of("https://previews.123rf.com/images/aquir/aquir1311/aquir131100316/23569861-%EC%83%98%ED%94%8C-%EC%A7%80-%EB%B9%A8%EA%B0%84%EC%83%89-%EB%9D%BC%EC%9A%B4%EB%93%9C-%EC%8A%A4%ED%83%AC%ED%94%84.jpg"))
                .introduce("안녕 난 제이")
                .grade(Grade.GENERAL)
                .build();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(entity));

        // when
        UserResponseDto actual = userService.updateIntroduce(USER_ID, updatedIntroduce);

        // then
        assertThat(actual.getIntroduce()).isEqualTo(updatedIntroduce);
        verify(userRepository, times(1)).findById(anyLong());
    }
}