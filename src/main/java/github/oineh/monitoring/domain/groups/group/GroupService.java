package github.oineh.monitoring.domain.groups.group;

import github.oineh.monitoring.config.exception.ApiException;
import github.oineh.monitoring.config.exception.ErrorCode;
import github.oineh.monitoring.controller.groups.group.req.GroupCreateTeamDeptReq;
import github.oineh.monitoring.controller.groups.group.req.GroupCreateTeamReq;
import github.oineh.monitoring.controller.groups.group.res.GroupListRes;
import github.oineh.monitoring.controller.team.req.TeamInviteReq;
import github.oineh.monitoring.controller.user.req.UserGroupsTeamInviteReq;
import github.oineh.monitoring.controller.user.res.InviteTeamUserRes;
import github.oineh.monitoring.domain.groups.Groups;
import github.oineh.monitoring.domain.groups.GroupsRepository;
import github.oineh.monitoring.domain.groups.group.category.Dept;
import github.oineh.monitoring.domain.groups.group.category.DeptRepository;
import github.oineh.monitoring.domain.groups.group.category.Team;
import github.oineh.monitoring.domain.groups.group.category.TeamRepository;
import github.oineh.monitoring.domain.groups.group.invit.InvitedGroup;
import github.oineh.monitoring.domain.groups.group.invit.InvitedGroupRepository;
import github.oineh.monitoring.domain.user.User;
import github.oineh.monitoring.domain.user.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final DeptRepository deptRepository;
    private final TeamRepository teamRepository;

    private final UserRepository userRepository;
    private final GroupsRepository groupsRepository;
    private final InvitedGroupRepository invitedGroupRepository;

    @Transactional
    public GroupListRes findGroupIn(Long groupId, String userId) {
        return groupsRepository.findById(groupId)
            .filter(groups -> checkGroupsInMember(groups, findUser(userId)))
            .map(group -> GroupListRes.of(group.getId(), group.getDept()))
            .orElse(new GroupListRes());
    }

    @Transactional
    public void createGroup(GroupCreateTeamDeptReq req, String userId) {
        groupsRepository.findById(req.getGroupsId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_GROUPS))
            .updateDept(deptSave(findUser(userId), req.getName()));
    }

    @Transactional
    public void createGroup(GroupCreateTeamReq req, String userId) {
        findGroups(req.getGroupsId()).getDept().stream()
            .filter(dep -> dep.getId().equals(req.getDeptId()))
            .findFirst()
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_GROUPS_DEPT))
            .updateTeam(teamRepository.save(new Team(findUser(userId), req.getName())));
    }

    @Transactional
    public List<InviteTeamUserRes> findInvite(String userId) {
        return mapInvitationsToResponse(getInvitationsByTargetUser(findUser(userId)));
    }

    @Transactional
    public void acceptInvite(UserGroupsTeamInviteReq req, String userId) {
        User user = findUser(userId, ErrorCode.NOT_FOUND_SEND_USER);
        Team team = findTeam(req.getTeamId());

        checkInvitedTargetUserAndTeamOK(user, team);

        team.updateMember(user);
        invitedGroupRepository.delete(findInvitedGroup(user, team));
    }


    @Transactional
    public void cancelInvite(UserGroupsTeamInviteReq req, String userId) {
        User user = findUser(userId);
        Team team = findTeam(req.getTeamId());
        InvitedGroup invited = findInvitedGroup(user, team);

        checkInvitedTargetUserAndTeamOK(user, team);
        invitedGroupRepository.delete(invited);
    }


    @Transactional
    public void targetUserInvite(TeamInviteReq req, String userId) {
        Team team = findTeam(req.getTeamId());
        User targetUser = getUserByEmail(req.getEmail(), ErrorCode.NOT_FOUND_TARGET_USER);
        User sendUser = findUser(userId, ErrorCode.NOT_FOUND_SEND_USER);

        checkGroupsInMember(findGroups(req.getGroupsId()), targetUser);
        checkSendUserIsTeamMember(sendUser, team);
        checkInvitedTargetUserAndTeam(targetUser, team);

        invitedGroupRepository.save(new InvitedGroup(targetUser, sendUser, team));
    }

    private void checkInvitedTargetUserAndTeamOK(User targetUser, Team team) {
        if (invitedGroupRepository.findByTargetUserAndTeam(targetUser, team).isEmpty()) {
            throw new ApiException(ErrorCode.NO_TEAM_INVITES);
        }
    }

    private void checkInvitedTargetUserAndTeam(User targetUser, Team team) {
        if (invitedGroupRepository.findByTargetUserAndTeam(targetUser, team).isPresent()) {
            throw new ApiException(ErrorCode.OVERLAP_INVITED_TEAM);
        }
    }

    private List<InvitedGroup> getInvitationsByTargetUser(User user) {
        return invitedGroupRepository.findByTargetUser(user).orElse(Collections.emptyList());
    }

    private List<InviteTeamUserRes> mapInvitationsToResponse(List<InvitedGroup> invited) {
        return invited.stream().map(InviteTeamUserRes::new).collect(Collectors.toList());
    }

    private boolean checkGroupsInMember(Groups group, User user) {
        if (!group.checkMember(user)) {
            throw new ApiException(ErrorCode.YOUR_NOT_GROUP);
        }
        return true;
    }

    private Dept deptSave(User user, String name) {
        return deptRepository.save(new Dept(user, name));
    }

    private User findUser(String loginId) {
        return userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));
    }

    private User findUser(String loginId, ErrorCode code) {
        return userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new ApiException(code));
    }

    private Groups findGroups(Long groupId) {
        return groupsRepository.findById(groupId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_GROUPS));
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEAM));
    }

    private InvitedGroup findInvitedGroup(User user, Team team) {
        return invitedGroupRepository.findByTargetUserAndTeam(user, team)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_TEAM_INVITES));
    }

    private User getUserByEmail(String email, ErrorCode errorCode) {
        return userRepository.findByInformationEmail(email).orElseThrow(() -> new ApiException(errorCode));
    }

    private void checkSendUserIsTeamMember(User sendUser, Team team) {
        if (!team.getMembers().contains(sendUser)) {
            throw new ApiException(ErrorCode.YOUR_NOT_TEAM);
        }
    }

}
