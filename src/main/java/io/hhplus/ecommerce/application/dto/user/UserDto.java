package io.hhplus.ecommerce.application.dto.user;

import io.hhplus.ecommerce.domain.entity.user.User;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long userId;
    private String name;
    private BigDecimal point;

    public User toEntity(){
        return User.builder()
                .userId(this.userId)
                .name(this.name)
                .point(this.point)
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint())
                .build();
    }


}
