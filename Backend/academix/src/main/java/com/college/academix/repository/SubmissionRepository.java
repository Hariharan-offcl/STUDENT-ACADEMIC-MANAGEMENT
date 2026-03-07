package com.college.academix.repository;

import com.college.academix.model.Assignment;
import com.college.academix.model.Student;
import com.college.academix.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, Student student);

    List<Submission> findByAssignment(Assignment assignment);

    List<Submission> findByStudent(Student student);

    boolean existsByAssignmentAndStudent(Assignment assignment, Student student);
}
