package com.college.academix.repository;

import com.college.academix.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByDepartmentAndSectionAndYear(Department department, Section section, int year);

    List<Assignment> findByDepartmentAndYear(Department department, int year);

    List<Assignment> findByFaculty(Faculty faculty);

    List<Assignment> findBySubject(Subject subject);
}
