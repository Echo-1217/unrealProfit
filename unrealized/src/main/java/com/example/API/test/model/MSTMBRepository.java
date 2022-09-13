package com.example.API.test.model;

import com.example.API.test.model.entity.MSTMB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MSTMBRepository extends JpaRepository<MSTMB, String> {
    @Query(value = "SELECT * from mstmb WHERE Stock=?1", nativeQuery = true)
    MSTMB findByStock(String stock);
}
