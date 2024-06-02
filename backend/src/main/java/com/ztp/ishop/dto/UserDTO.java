package com.ztp.ishop.dto;

import com.ztp.ishop.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String login;
    private String email;
    private String token;
    private Role type;

    public UserDTO toEntity() {
        UserDTO user = new UserDTO();
        user.setId(this.getId());
        user.setName(this.getName());

        return user;
    }
}