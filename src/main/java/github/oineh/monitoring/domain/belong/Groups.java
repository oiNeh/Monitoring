package github.oineh.monitoring.domain.belong;


import static lombok.AccessLevel.PROTECTED;

import github.oineh.monitoring.domain.belong.group.Department;
import github.oineh.monitoring.domain.belong.group.Division;
import github.oineh.monitoring.domain.belong.group.Team;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Division division;

    @OneToOne(fetch = FetchType.LAZY)
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    private Team team;


    public Groups(Division division, Department department, Team team) {
        this.division = division;
        this.department = department;
        this.team = team;
    }
}
