package github.oineh.monitoring.domain.connect;

import github.oineh.monitoring.common.service.MonitoringService;
import github.oineh.monitoring.config.exception.ApiException;
import github.oineh.monitoring.config.exception.ErrorCode;
import github.oineh.monitoring.controller.pc.req.ByPcReq;
import github.oineh.monitoring.controller.pc.res.ConnectStatusRes;
import github.oineh.monitoring.controller.team.req.TeamCreateIpReq;
import github.oineh.monitoring.controller.team.req.TeamCreatePortReq;
import github.oineh.monitoring.controller.team.req.TeamCreateUrlReq;
import github.oineh.monitoring.controller.team.res.TeamInDominRes;
import github.oineh.monitoring.controller.team.res.TeamInMemberRes;
import github.oineh.monitoring.domain.group.category.Team;
import github.oineh.monitoring.domain.group.category.TeamRepository;
import io.github.sno.network.Host;
import io.github.sno.network.NetProtocolDto;
import io.github.sno.network.NetProtocolType;
import io.github.sno.network.NetStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectService {

    private final TeamRepository teamRepository;
    private final MonitoringService monitoringService;

    public ConnectStatusRes findConnectStatus(ByPcReq byPcReq, Host host) {
        NetProtocolDto netProtocolDto = new NetProtocolDto(host, byPcReq.getUrl(), byPcReq.getPort());

        NetProtocolType netProtocolType = monitoringService.connectNetProtocol(netProtocolDto);

        return new ConnectStatusRes(netProtocolType);
    }

    @Transactional
    public List<TeamInDominRes> findTeamInConnectList(Long teamId, String userId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEAM));
        String ip = "127.0.0.1";
        int[] arr = Arrays.stream(ip.split("\\.")).mapToInt(Integer::parseInt).toArray();

        return team.getConnects().stream()
            .filter(connect -> connect.getConnectType() != null)
            .map(connect -> new TeamInDominRes(connect.getName(), connectStatus(connect)))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<TeamInMemberRes> findTeamInConnectMemberList(Long teamId, String userId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEAM));

        return team.getMember().stream()
            .filter(member -> member.getPc() != null)
            .map(member -> new TeamInMemberRes(member.getInformation().getNickName(),
                connectStatus(member.getPc().getConnect())))
            .collect(Collectors.toList());
    }

    private NetStatus connectStatus(Connect connect) {
        if (connect.getConnectType() == ConnectType.ICMP) {
            return monitoringService.IcmpStatus(Host.from(connect.getIp()));
        }
        if (connect.getConnectType() == ConnectType.TCP_PORT) {
            System.out.println("ip : " + connect.getIp());
            return monitoringService.TcpStatus(Host.from(connect.getIp()), connect.getPort());
        }
        if (connect.getConnectType() == ConnectType.TCP_URL) {
            return monitoringService.TcpStatus(connect.getUrl());
        }
        return null;
    }

    @Transactional
    public void createUrl(TeamCreateUrlReq req, String userId) {
        Team team = teamRepository.findById(req.getTeamId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEAM));

        if (!req.getUrl().contains("://")) {
            req.updateUrlHttp();
        }
        Connect connect = Connect.tcp(req.getName(), req.getUrl());

        team.updateConnect(connect);
    }

    @Transactional
    public void createIpPort(TeamCreatePortReq req, String userId) {
        Team team = teamRepository.findById(req.getTeamId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEAM));

        Connect connect = Connect.tcp(req.getName(), req.getIp(), req.getPort());

        team.updateConnect(connect);
    }

    @Transactional
    public void createIp(TeamCreateIpReq req, String userId) {
        Team team = teamRepository.findById(req.getTeamId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEAM));

        Connect connect = Connect.icmp(req.getName(), req.getIp());

        team.updateConnect(connect);
    }


}
