package ua.quiz.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "question")
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "body", nullable = false, length = 300)
    private String body;

    @Column(name = "correct_answer", nullable = false, length = 100)
    private String correctAnswer;

    @Column(name = "hint", nullable = false, length = 200)
    private String hint;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhaseEntity> phases;
}
