package ua.quiz.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.quiz.model.entity.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
    Page<GameEntity> findAll(Pageable pageable);

    Page<GameEntity> findByTeamId(Long teamId, Pageable page);
}
