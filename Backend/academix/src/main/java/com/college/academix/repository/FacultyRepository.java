package com.college.academix.repository;

import com.college.academix.model.Faculty;
import com.college.academix.model.Section;
import com.college.academix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByUser(User user);

    List<Faculty> findByAdvisorSection(Section section);

    Optional<Faculty> findByAdvisorSectionAndIsClassAdvisor(Section section, boolean isClassAdvisor);
}
