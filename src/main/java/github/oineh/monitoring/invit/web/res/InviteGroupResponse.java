package github.oineh.monitoring.invit.web.res;

import github.oineh.monitoring.invit.domain.InvitedGroups;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteGroupResponse {

    private Long id;
    private String groupsName;
    private Long groupsId;
    private String sendName;

    public InviteGroupResponse(InvitedGroups groups) {
        this.id = groups.getId();
        this.groupsName = groups.getGroupsName();
        this.groupsId = groups.getGroupsId();
        this.sendName = groups.getSendUserName();
    }
}
