package github.oineh.monitoring.domain.groups.group.category;

import static lombok.AccessLevel.PROTECTED;

import github.oineh.monitoring.common.entity.BaseEntity;
import github.oineh.monitoring.domain.groups.Groups;
import github.oineh.monitoring.domain.user.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Dept extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User adminUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private Groups groups;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Team> teams = new ArrayList<>();
    private String name;

    public Dept(User adminUser, String name) {
        this.adminUser = adminUser;
        this.name = name;
    }

    public void updateTeam(Team team) {
        this.teams.add(team);
    }
}
