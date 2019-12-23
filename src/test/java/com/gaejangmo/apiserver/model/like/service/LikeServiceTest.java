package com.gaejangmo.apiserver.model.like.service;

import com.gaejangmo.apiserver.config.oauth.SecurityUser;
import com.gaejangmo.apiserver.model.like.domain.LikeRepository;
import com.gaejangmo.apiserver.model.like.domain.Likes;
import com.gaejangmo.apiserver.model.user.domain.User;
import com.gaejangmo.apiserver.model.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final Long SOURCE_ID = 1L;
    private static final Long TARGET_ID = 2L;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    private SecurityUser loginUser = null;

    @Test
    void 좋아요_저장() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        given(likeRepository.save(any())).willReturn(mock(Likes.class));

        // when & then
        assertDoesNotThrow(() -> likeService.save(SOURCE_ID, TARGET_ID));
    }

    @Test
    void 내가_좋아요를_누른_사람들_조회() {
        // given
        Likes like = Likes.builder()
                .source(mock(User.class))
                .target(mock(User.class))
                .build();

        given(userRepository.findById(SOURCE_ID)).willReturn(Optional.of(mock(User.class)));
        given(likeRepository.findAllBySource(any())).willReturn(List.of(like));

        // when
        List<Likes> likes = likeService.findAllBySource(SOURCE_ID);

        // then
        assertThat(likes).isEqualTo(List.of(like));
        verify(userRepository, times(1)).findById(SOURCE_ID);
        verify(likeRepository, times(1)).findAllBySource(any());
    }

    @Test
    void 좋아요_취소() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        doNothing().when(likeRepository).deleteBySourceAndTarget(any(), any());

        // when & then
        assertDoesNotThrow(() -> likeService.deleteBySourceAndTarget(SOURCE_ID, TARGET_ID));
        verify(userRepository, times(2)).findById(anyLong());
        verify(likeRepository, times(1)).deleteBySourceAndTarget(any(), any());
    }

    @Test
    void 좋아요_눌렀을_때_true_반환_확인() {
        // given
        loginUser = SecurityUser.builder().id(SOURCE_ID).build();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        given(likeRepository.findBySourceAndTarget(any(), any())).willReturn(Optional.of(mock(Likes.class)));

        // when & then
        assertThat(likeService.isLiked(loginUser, TARGET_ID)).isTrue();
    }

    @Test
    void 좋아요_눌르지_않았을_때_false_반환_확인() {
        // given
        loginUser = SecurityUser.builder().id(SOURCE_ID).build();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        given(likeRepository.findBySourceAndTarget(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThat(likeService.isLiked(loginUser, TARGET_ID)).isFalse();
    }

    @Test
    void 로그인되지_않은_상태에서_좋아요가_false_반환하는지_확인() {
        // when & then
        assertThat(likeService.isLiked(loginUser, TARGET_ID)).isFalse();
    }
}