package github.oineh.monitoring.config.auth;

import github.oineh.monitoring.authority.domain.Auth;
import github.oineh.monitoring.authority.domain.Grade;
import github.oineh.monitoring.user.domain.User;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogin implements UserDetails {

    private String username;
    private String password;
    private Set<Grade> authorities;
    private boolean enabled;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserLogin(String username, String password, Set<Grade> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
    }


    public static UserLogin ofPricipal(Object principal) {
        return (UserLogin) principal;
    }

    public enum Type {
        login,
        refresh
    }

    public static UserDetails of(User user, Auth auth) {
        return new UserLogin(user.getLoginId(), user.getPw(), auth.getGrades());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonLocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
