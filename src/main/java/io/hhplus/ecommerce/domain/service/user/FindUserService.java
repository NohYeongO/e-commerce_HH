package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.common.exception.user.UserNotFoundException;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindUserService {

    private final UserJpaRepository userJpaRepository;

    /**
     * 회원 조회
     */
    @Transactional
    public UserDto getUser(Long userId, boolean locked) {
        User user;
        if(locked){
            // Lock이 필요할 경우
            user = userJpaRepository.findByIdWithLock(userId)
                    .orElseThrow(() ->
                            new UserNotFoundException("회원이 존재하지 않습니다.", 200)
                    );
        } else{
            // 단순 회원 조회 시
            user = userJpaRepository.findById(userId)
                    .orElseThrow(() ->
                            new UserNotFoundException("회원이 존재하지 않습니다.", 200)
                    );
        }
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint()).build();
    }

}
