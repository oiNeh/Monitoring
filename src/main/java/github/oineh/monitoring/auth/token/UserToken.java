package github.oineh.monitoring.auth.token;

import lombok.Getter;

import java.util.Map;

@Getter
public class UserToken {

    private String username;
    private String authorities;
    private String userId;
    private String password;

    public UserToken(Object principal) {
        System.out.println(principal);
        Map<String, String> map = (Map<String, String>) principal;
        this.username = map.get("username");
        this.authorities = map.get("authorities");
        this.userId = map.get("userId");
        this.password = map.get("password");
    }
}
