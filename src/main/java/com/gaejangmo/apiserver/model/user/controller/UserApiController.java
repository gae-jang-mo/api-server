package com.gaejangmo.apiserver.model.user.controller;

import com.gaejangmo.apiserver.commons.logging.EnableLog;
import com.gaejangmo.apiserver.config.oauth.SecurityUser;
import com.gaejangmo.apiserver.model.common.exception.ApiErrorResponse;
import com.gaejangmo.apiserver.model.common.resolver.LoginUser;
import com.gaejangmo.apiserver.model.image.dto.FileResponseDto;
import com.gaejangmo.apiserver.model.user.domain.vo.Motto;
import com.gaejangmo.apiserver.model.user.dto.UserIntroduceDto;
import com.gaejangmo.apiserver.model.user.dto.UserResponseDto;
import com.gaejangmo.apiserver.model.user.dto.UserSearchDto;
import com.gaejangmo.apiserver.model.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@EnableLog
@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;

    public UserApiController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/image")
    public ResponseEntity<FileResponseDto> updateUserImage(@RequestParam final MultipartFile file,
                                                           @LoginUser final SecurityUser securityUser) {
        FileResponseDto fileResponseDto = userService.updateUserImage(file, securityUser.getId());
        return ResponseEntity.ok(fileResponseDto);
    }

    @GetMapping("/logined")
    public ResponseEntity<UserResponseDto> showUser(@LoginUser SecurityUser user) {
        UserResponseDto response = userService.findUserResponseDtoByOauthId(user.getOauthId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserResponseDto> showUserByName(@PathVariable final String name, @LoginUser SecurityUser securityUser) {
        UserResponseDto response = userService.findUserResponseDtoByName(name, securityUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes")
    public ResponseEntity<List<UserResponseDto>> showLikeUser(@LoginUser SecurityUser securityUser) {
        List<UserResponseDto> response = userService.findUserResponseDtoBySourceId(securityUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/random")
    public ResponseEntity<List<UserSearchDto>> showRandomUsers(@LoginUser final SecurityUser securityUser) {
        return ResponseEntity.ok(userService.findRandomUserResponse(securityUser.getId()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDto>> search(@RequestParam final String username) {
        List<UserSearchDto> userSearchDtos = userService.findUserSearchDtosByUserName(username);
        return ResponseEntity.ok(userSearchDtos);
    }

    @PutMapping("/motto")
    public ResponseEntity<Motto> updateMotto(@RequestBody final Motto motto,
                                             @LoginUser SecurityUser user) {
        UserResponseDto response = userService.updateMotto(user.getId(), motto);
        return ResponseEntity.ok(Motto.of(response.getMotto()));
    }

    @PutMapping("/introduce")
    public ResponseEntity<UserIntroduceDto> updateIntroduce(@RequestBody final UserIntroduceDto introduce,
                                                            @LoginUser SecurityUser securityUser) {
        UserResponseDto response = userService.updateIntroduce(securityUser.getId(), introduce.getIntroduce());
        return ResponseEntity.ok(new UserIntroduceDto(response.getIntroduce()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ApiErrorResponse> handleException(final Exception exception) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }
}
