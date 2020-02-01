package ua.quiz.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.GameEntity;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<GameEntity, Long> {
    Page<GameEntity> findByTeamId(Long teamId, Pageable page);

    Long countAllByTeamId(Long teamId);
}
