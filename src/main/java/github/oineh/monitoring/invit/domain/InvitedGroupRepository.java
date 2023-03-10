package github.oineh.monitoring.invit.domain;

import github.oineh.monitoring.team.domain.Team;
import github.oineh.monitoring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitedGroupRepository extends JpaRepository<InvitedTeam, Long> {

    Optional<List<InvitedTeam>> findByTargetUser(User targetUser);

    Optional<InvitedTeam> findByTargetUserAndTeam(User targetUser, Team team);
}
