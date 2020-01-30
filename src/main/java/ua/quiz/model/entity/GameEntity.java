package ua.quiz.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @Column(name = "number_of_questions", nullable = false)
    private Integer numberOfQuestions;

    @Column(name = "time_per_question", nullable = false)
    private Integer timePerQuestion;

    @Column(name = "current_phase")
    private Integer currentPhase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEntity statusEntity;

    @OneToMany(mappedBy = "game")
    private List<PhaseEntity> phases;
}
