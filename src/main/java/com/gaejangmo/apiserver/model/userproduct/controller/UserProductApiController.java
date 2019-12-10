package com.gaejangmo.apiserver.model.userproduct.controller;


import com.gaejangmo.apiserver.model.userproduct.service.UserProductService;
import com.gaejangmo.apiserver.model.userproduct.service.dto.UserProductCreateDto;
import com.gaejangmo.apiserver.model.userproduct.service.dto.UserProductResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/api/v1/userproducts")
public class UserProductApiController {
    private final UserProductService userProductService;

    public UserProductApiController(final UserProductService userProductService) {
        this.userProductService = userProductService;
    }

    @PostMapping
    public ResponseEntity<UserProductResponseDto> create(@RequestBody final UserProductCreateDto userProductCreateDto) {
        // TODO: 2019/12/10 유저 정보 가져와서 id 넘기기
        UserProductResponseDto responseDto = userProductService.save(userProductCreateDto, 1L);
        URI uri = linkTo(UserProductApiController.class).slash(responseDto.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserProductResponseDto>> list(@PathVariable final Long userId) {
        List<UserProductResponseDto> responseDtos = userProductService.findByUserId(userId);
        return ResponseEntity.ok(responseDtos);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable final Long id) {
        // TODO: 2019/12/10 유저 정보 가져와서 id 넘기기
        Long userId = 1L;
        userProductService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
