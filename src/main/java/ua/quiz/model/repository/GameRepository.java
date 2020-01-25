package ua.quiz.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.GameEntity;

import java.awt.print.Pageable;
import java.util.List;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<GameEntity, Long> {
    Page<GameEntity> findByTeamId(Long teamId, PageRequest page);

    Long countAllByTeamId(Long teamId);
}
