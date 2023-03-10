package github.oineh.monitoring.team.domain;

import fixture.UserFixture;
import github.oineh.monitoring.department.domain.Department;
import github.oineh.monitoring.groups.domain.Groups;
import github.oineh.monitoring.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("팀")
public class TeamTest {

    User user;
    Groups groups;
    Department dept;

    @BeforeEach
    void setup() {
        user = UserFixture.getUser();
        groups = new Groups(user, "groupsName");
        dept = new Department(user, "부서");
    }

    @Test
    @DisplayName("만들기")
    void teamCreate() {
        //given
        Team team = new Team(user, "개발팀");

        //when
        dept.updateTeam(team);

        //then
        assertThat(dept.getTeams()).contains(team);
    }
}
