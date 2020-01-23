package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.PhaseEntity;

import java.util.List;

@Repository("phaseRepository")
public interface PhaseRepository extends JpaRepository<PhaseEntity, Long> {
    List<PhaseEntity> findPhaseEntitiesByGameId(Long gameId);
}
