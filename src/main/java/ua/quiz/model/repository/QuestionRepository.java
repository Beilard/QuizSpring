package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.quiz.model.entity.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

}
