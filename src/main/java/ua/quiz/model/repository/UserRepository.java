package ua.quiz.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.quiz.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByTeamId(Long teamId);

}
