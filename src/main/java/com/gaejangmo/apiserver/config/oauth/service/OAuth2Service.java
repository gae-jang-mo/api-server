package com.gaejangmo.apiserver.config.oauth.service;

import com.gaejangmo.apiserver.config.oauth.dto.OAuthAttributesDto;
import com.gaejangmo.apiserver.model.common.domain.vo.Link;
import com.gaejangmo.apiserver.model.common.resolver.SessionUser;
import com.gaejangmo.apiserver.model.user.domain.User;
import com.gaejangmo.apiserver.model.user.domain.UserRepository;
import com.gaejangmo.apiserver.model.user.domain.vo.Email;
import com.gaejangmo.apiserver.model.user.domain.vo.Grade;
import com.gaejangmo.apiserver.model.user.domain.vo.Motto;
import com.gaejangmo.apiserver.model.user.domain.vo.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

import static com.gaejangmo.apiserver.model.common.resolver.SessionUser.USER_SESSION_KEY;

@Component
public class OAuth2Service {
    private final UserRepository userRepository;
    private final HttpSession session;

    public OAuth2Service(final UserRepository userRepository, final HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    public DefaultOAuth2User getUser(final String registrationId, final String userNameAttributeName, final Map<String, Object> attributes) {
        OAuthAttributesDto attributesDto = OAuthAttributesDto.of(registrationId, userNameAttributeName, attributes);

        User user = saveOrUpdate(attributesDto);
        session.setAttribute(USER_SESSION_KEY, toSessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleType())),
                attributesDto.getAttributes(),
                attributesDto.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(final OAuthAttributesDto attributes) {
        User user = userRepository.findByOauthId(attributes.getOauthId())
                .map(userEntity -> userEntity.update(attributes.getUsername(), attributes.getImageUrl()))
                .orElseGet(() -> toEntity(attributes));

        return userRepository.save(user);
    }

    private SessionUser toSessionUser(final User user) {
        return SessionUser.builder()
                .email(user.getEmail())
                .userName(user.getUsername())
                .id(user.getId())
                .build();
    }

    private User toEntity(final OAuthAttributesDto attributes) {
        return User.builder()
                .oauthId(attributes.getOauthId())
                .username(attributes.getUsername())
                .imageUrl(Link.of(attributes.getImageUrl()))
                .role(Role.USER)
                .grade(Grade.GENERAL)
                .email(Email.of(attributes.getEmail()))
                .motto(Motto.of(""))
                .build();
    }
}