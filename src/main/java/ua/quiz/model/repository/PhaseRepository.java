package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.quiz.model.entity.PhaseEntity;

import java.util.List;


public interface PhaseRepository extends JpaRepository<PhaseEntity, Long> {
    List<PhaseEntity> findPhaseEntitiesByGameId(Long gameId);
}
