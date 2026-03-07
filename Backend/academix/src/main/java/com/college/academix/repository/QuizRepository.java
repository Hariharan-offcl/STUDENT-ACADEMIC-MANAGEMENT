package com.college.academix.repository;

import com.college.academix.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByDepartmentAndSectionAndYear(Department department, Section section, int year);

    List<Quiz> findByFaculty(Faculty faculty);
}
