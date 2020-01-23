package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.TeamEntity;

import java.util.Optional;

@Repository("teamRepository")
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
   Optional<TeamEntity> findByTeamName(String teamName);
}
