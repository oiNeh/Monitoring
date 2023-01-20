package github.oineh.monitoring.groups.group.dept.team.invit.web.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamInviteAcceptReq {

    private Long inviteId;
    private Long teamId;
}
