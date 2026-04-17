package ch.example.musicapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUser {

    private Integer id;
    private String username;
    private String password;
    private Boolean enabled;
    private Integer roleId;
    private String roleName;
}