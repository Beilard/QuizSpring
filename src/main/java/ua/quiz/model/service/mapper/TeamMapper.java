package ua.quiz.model.service.mapper;

import ua.quiz.model.dto.Team;
import ua.quiz.model.entity.TeamEntity;

import java.util.Objects;

public class TeamMapper {
    public Team mapTeamEntityToTeam(TeamEntity teamEntity) {
        if (Objects.isNull(teamEntity)) {
            return null;
        }
        return new Team(teamEntity.getId(), teamEntity.getTeamName());
    }

    public TeamEntity mapTeamToTeamEntity(Team team) {
        if (Objects.isNull(team)) {
            return null;
        }

        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setId(team.getId());
        teamEntity.setTeamName(team.getTeamName());
        return teamEntity;
    }
}
