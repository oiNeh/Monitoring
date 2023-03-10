package github.oineh.monitoring.team.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.oineh.monitoring.common.IntegrationTest;
import github.oineh.monitoring.department.domain.Department;
import github.oineh.monitoring.department.domain.DepartmentRepository;
import github.oineh.monitoring.groups.domain.Groups;
import github.oineh.monitoring.groups.domain.GroupsRepository;
import github.oineh.monitoring.team.domain.Team;
import github.oineh.monitoring.team.domain.TeamRepository;
import github.oineh.monitoring.team.web.rest.req.AddressCreateIpRequest;
import github.oineh.monitoring.team.web.rest.req.AddressCreatePortRequest;
import github.oineh.monitoring.team.web.rest.req.AddressCreateUrlRequest;
import github.oineh.monitoring.user.domain.User;
import github.oineh.monitoring.user.domain.User.Information;
import github.oineh.monitoring.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("api 팀 주소")
class TeamAddressControllerTest extends IntegrationTest {

    private final String url = "/api/team/address";
    User user;
    Groups groups;
    Department dept;
    Team team;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    GroupsRepository groupsRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    @BeforeEach
    void setup() {
        Information userInfo = new Information("test@test.com", "test_name", "test_Nickname");
        user = userRepository.save(new User("test_user_id", "password", userInfo));
        groups = groupsRepository.save(new Groups(user, "group_name"));
        dept = departmentRepository.save(new Department(user, "dept_name"));
        groups.updateDept(dept);
        team = teamRepository.save(new Team(user, "team_name"));
        dept.updateTeam(team);
    }

    @Test
    @DisplayName("url 등록")
    void addAddressUrl() throws Exception {
        //given
        AddressCreateUrlRequest req = new AddressCreateUrlRequest(team.getId(), groups.getId(), "naver", "https://naver.com");

        //when
        ResultActions action = mvc.perform(post(url + "/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }

    @Test
    @DisplayName("ip:port 등록")
    void addAddressIpPort() throws Exception {
        //given
        AddressCreatePortRequest req = new AddressCreatePortRequest(team.getId(), "my pc", 127, 0, 0, 1, 80);

        //when
        ResultActions action = mvc.perform(post(url + "/port")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }

    @Test
    @DisplayName("ip 등록")
    void addAddressIp() throws Exception {
        //given
        AddressCreateIpRequest req = new AddressCreateIpRequest(team.getId(), "my pc", 127, 0, 0, 1);

        //when
        ResultActions action = mvc.perform(post(url + "/ip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }
}
