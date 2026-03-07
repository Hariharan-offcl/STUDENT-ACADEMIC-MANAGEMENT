package com.college.academix.repository;

import com.college.academix.model.Quiz;
import com.college.academix.model.Student;
import com.college.academix.model.StudentQuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentQuizAttemptRepository extends JpaRepository<StudentQuizAttempt, Long> {
    Optional<StudentQuizAttempt> findByStudentAndQuiz(Student student, Quiz quiz);

    List<StudentQuizAttempt> findByQuiz(Quiz quiz);

    List<StudentQuizAttempt> findByStudent(Student student);

    boolean existsByStudentAndQuiz(Student student, Quiz quiz);
}
