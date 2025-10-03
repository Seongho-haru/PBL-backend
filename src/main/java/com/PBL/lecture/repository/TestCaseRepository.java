package com.PBL.lecture.repository;

import com.PBL.lecture.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {


    List<TestCase> findByLectureId(Long id);
}
