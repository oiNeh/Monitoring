package github.oineh.monitoring.user.service;

import github.oineh.monitoring.auth.domain.Auth;
import github.oineh.monitoring.auth.domain.AuthRepository;
import github.oineh.monitoring.auth.token.UserLogin;
import github.oineh.monitoring.config.exception.ApiException;
import github.oineh.monitoring.config.exception.AuthenticationCustomException;
import github.oineh.monitoring.config.exception.ErrorCode;
import github.oineh.monitoring.user.domain.User;
import github.oineh.monitoring.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUser(username);
        Auth auth = findAuth(user);

        log.info("login : " + user.getEmail() + ", Auth Level -" + auth.getGrades().toString());

        return UserLogin.of(user, auth);
    }

    private Auth findAuth(User user) {
        return authRepository.findByUser(user)
                .orElseThrow(() -> new AuthenticationCustomException(ErrorCode.VALIDATE_AUTHENTICATION));
    }

    private User findUser(String userId) {
        return userRepository.findByLoginId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));
    }
}
