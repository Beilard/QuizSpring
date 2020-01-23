package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.quiz.model.entity.QuestionEntity;
import ua.quiz.model.entity.TeamEntity;

import java.util.Optional;

@Repository("questionRepository")
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

}
