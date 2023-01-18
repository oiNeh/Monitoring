package github.oineh.monitoring.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.oineh.monitoring.controller.common.IntegrationTest;
import github.oineh.monitoring.controller.user.req.SingUpReq;
import github.oineh.monitoring.controller.user.req.UserGroupsInviteReq;
import github.oineh.monitoring.controller.user.req.UserGroupsTeamInviteReq;
import github.oineh.monitoring.domain.groups.Groups;
import github.oineh.monitoring.domain.groups.GroupsRepository;
import github.oineh.monitoring.domain.groups.group.category.Team;
import github.oineh.monitoring.domain.groups.group.category.TeamRepository;
import github.oineh.monitoring.domain.groups.group.invit.InvitedGroup;
import github.oineh.monitoring.domain.groups.group.invit.InvitedGroupRepository;
import github.oineh.monitoring.domain.groups.invit.InvitedGroups;
import github.oineh.monitoring.domain.groups.invit.InvitedGroupsRepository;
import github.oineh.monitoring.domain.user.User;
import github.oineh.monitoring.domain.user.User.Information;
import github.oineh.monitoring.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("api 유저 컨트롤 테스트")
class ApiUserControllerTest extends IntegrationTest {

    static final String TARGET_RESOURCE = "/api/user";

    User adminUser;
    Groups groups;
    User user;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private InvitedGroupsRepository invitedGroupsRepository;
    @Autowired
    private InvitedGroupRepository invitedGroupRepository;
    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setup() {
        Information information = new Information("test_email_@test.com", "test_name", "test_Nickname");
        adminUser = userRepository.save(new User("test_admin_id", "password", information));

        Information userInfo = new Information("test@test.com", "test_name", "test_Nickname");
        user = userRepository.save(new User("test_user_id", "password", userInfo));

        groups = groupsRepository.save(new Groups(adminUser, "group_name"));
    }

    @AfterEach
    void after() {
        invitedGroupRepository.deleteAll();
        invitedGroupsRepository.deleteAll();
        userRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("그룹 초대장 리스트 테스트")
    void findGroupsInvite() throws Exception {
        //given
        InvitedGroups invited = new InvitedGroups(user, adminUser, groups);
        invitedGroupsRepository.save(invited);

        //when
        ResultActions action = mvc.perform(get(TARGET_RESOURCE + "/groups/invite"));

        //then
        action.andExpect(status().isOk()).andExpect(jsonPath("$[0].groupsName").value(groups.getName()))
            .andExpect(jsonPath("$[0].sendName").value(adminUser.getInformation().getName()));
    }

    @Test
    @DisplayName("그룹 초대 수락 테스트")
    void acceptGroupsInvite() throws Exception {
        //given
        InvitedGroups invited = invitedGroupsRepository.save(new InvitedGroups(user, adminUser, groups));
        UserGroupsInviteReq req = new UserGroupsInviteReq(invited.getId(), groups.getId());

        //when
        ResultActions action = mvc.perform(
            post(TARGET_RESOURCE + "/groups/invite").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }

    @Test
    @DisplayName("그룹 초대 거부 테스트")
    void cancelGroupsInvite() throws Exception {
        //given
        InvitedGroups invited = invitedGroupsRepository.save(new InvitedGroups(user, adminUser, groups));
        UserGroupsInviteReq req = new UserGroupsInviteReq(invited.getId(), groups.getId());

        //when
        ResultActions action = mvc.perform(
            delete(TARGET_RESOURCE + "/groups/invite").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }

    @Test
    @DisplayName("팀 초대 리스트 테스트")
    void findTeamInviteList() throws Exception {
        //given
        Team team = teamRepository.save(new Team(adminUser, "team_name"));
        InvitedGroup invited = invitedGroupRepository.save(new InvitedGroup(user, adminUser, team));

        //when
        ResultActions action = mvc.perform(get(TARGET_RESOURCE + "/team/invite"));

        //then
        action.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(invited.getId()))
            .andExpect(jsonPath("$[0].teamName").value(invited.getTeam().getName()))
            .andExpect(jsonPath("$[0].teamId").value(invited.getTeam().getId()))
            .andExpect(jsonPath("$[0].sendName").value(invited.getSendUser().getInformation().getName()));
    }

    @Test
    @DisplayName("팀 초대 수락 테스트")
    void acceptTeamInvite() throws Exception {
        //given
        Team team = teamRepository.save(new Team(adminUser, "team_name"));
        InvitedGroup invited = invitedGroupRepository.save(new InvitedGroup(user, adminUser, team));

        UserGroupsTeamInviteReq req = new UserGroupsTeamInviteReq(invited.getId(), team.getId());

        //when
        ResultActions action = mvc.perform(
            post(TARGET_RESOURCE + "/team/invite").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }

    @Test
    @DisplayName("팀 초대 거부 테스트")
    void cancelTeamInvite() throws Exception {
        //given
        Team team = teamRepository.save(new Team(adminUser, "team_name"));
        InvitedGroup invited = invitedGroupRepository.save(new InvitedGroup(user, adminUser, team));
        UserGroupsTeamInviteReq req = new UserGroupsTeamInviteReq(invited.getId(), team.getId());

        //when
        ResultActions action = mvc.perform(
            delete(TARGET_RESOURCE + "/team/invite").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 테스트")
    void singUp() throws Exception {
        //given
        Information info = user.getInformation();
        SingUpReq req = new SingUpReq(info.getEmail() + "Co", info.getName() + "Na", info.getNickName() + "NI",
            user.getLoginId() + "ID", user.getPw() + "!@");

        //when
        ResultActions action = mvc.perform(post(TARGET_RESOURCE + "/singup").contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(req)));

        //then
        action.andExpect(status().isOk());
    }
}