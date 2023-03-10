package fixture;

import github.oineh.monitoring.user.domain.User;
import github.oineh.monitoring.user.domain.User.Information;

public class UserFixture {

    public static User getUser() {
        String id = "test_user_id";
        String pw = "test_pw";
        String email = "test_email@naver.com";
        String name = "강정훈";
        String nickName = "학생_1";

        Information information = new Information(email, name, nickName);

        return new User(id, pw, information);

    }
}
