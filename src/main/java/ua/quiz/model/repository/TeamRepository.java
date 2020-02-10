package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.quiz.model.entity.TeamEntity;

import java.util.Optional;


public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    Optional<TeamEntity> findByTeamName(String teamName);
}
