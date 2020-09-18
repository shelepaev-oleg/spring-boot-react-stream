package com.example.springbootreactstream.repository;

import com.example.springbootreactstream.model.TestFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestFileRepository extends JpaRepository<TestFile, Long> {
}
