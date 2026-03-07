package com.college.academix.repository;

import com.college.academix.model.Department;
import com.college.academix.model.Section;
import com.college.academix.model.Student;
import com.college.academix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByUser(User user);

    List<Student> findByDepartmentAndSectionAndCurrentYear(Department department, Section section, int currentYear);

    List<Student> findByDepartmentAndCurrentYear(Department department, int currentYear);

    boolean existsByRollNumber(String rollNumber);
}
