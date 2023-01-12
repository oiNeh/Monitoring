package github.oineh.monitoring.controller.team;


import github.oineh.monitoring.controller.team.req.TeamCreateIpReq;
import github.oineh.monitoring.controller.team.req.TeamCreatePortReq;
import github.oineh.monitoring.controller.team.req.TeamCreateUrlReq;
import github.oineh.monitoring.controller.team.req.TeamInviteReq;
import github.oineh.monitoring.controller.team.res.TeamInDominRes;
import github.oineh.monitoring.controller.team.res.TeamInMemberRes;
import github.oineh.monitoring.domain.connect.ConnectService;
import github.oineh.monitoring.domain.group.GroupService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class ApiTeamController {

    private final ConnectService connectService;
    private final GroupService groupService;


    @PostMapping("/find/domain/{teamId}")
    public ResponseEntity<List<TeamInDominRes>> findTeamDomain(@PathVariable("teamId") Long teamId,
        Principal principal) {
        List<TeamInDominRes> res = connectService.findTeamInConnectList(teamId, principal.getName());

        System.out.println(res.size());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/find/member/{teamId}")
    public ResponseEntity<List<TeamInMemberRes>> findTeam(@PathVariable("teamId") Long teamId,
        Principal principal) {
        List<TeamInMemberRes> res = connectService.findTeamInConnectMemberList(teamId, principal.getName());

        System.out.println(res.size());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> invite(@RequestBody TeamInviteReq req, Principal principal) {
        groupService.targetUserInvite(req, principal.getName());

        return ResponseEntity.ok().build();
    }


    @PostMapping("/add/url")
    public ResponseEntity<Void> createUrl(@RequestBody TeamCreateUrlReq req, Principal principal) {
        System.out.println(",,, " + req.getUrl());
        connectService.createUrl(req, principal.getName());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/add/ip/port")
    public ResponseEntity<Void> createPort(@RequestBody TeamCreatePortReq req, Principal principal) {
        System.out.println("controller ip :" + req.getIp() + ",port:" + req.getPort());
        connectService.createIpPort(req, principal.getName());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/add/ip")
    public ResponseEntity<Void> createIp(@RequestBody TeamCreateIpReq req, Principal principal) {
        System.out.println(req.getIp() + " ==== ");
        connectService.createIp(req, principal.getName());

        return ResponseEntity.ok().build();

    }
}