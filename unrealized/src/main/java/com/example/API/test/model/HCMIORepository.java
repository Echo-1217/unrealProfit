package com.example.API.test.model;

import com.example.API.test.model.entity.HCMIO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HCMIORepository extends JpaRepository<HCMIO, HCMIO.HCIMO_ID> {

    @Query(value = "SELECT DocSeq FROM hcmio WHERE TradeDate = ?1 ORDER BY DocSeq DESC LIMIT 1", nativeQuery = true)
    String getLastDocSeq(String tradeDate);

}
