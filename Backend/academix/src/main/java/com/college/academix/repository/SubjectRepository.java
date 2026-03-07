package com.college.academix.repository;

import com.college.academix.model.Department;
import com.college.academix.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByDepartment(Department department);

    List<Subject> findByDepartmentAndYear(Department department, int year);
}
