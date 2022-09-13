package com.example.API.test.model;

import com.example.API.test.model.entity.TCNUD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TCNUDRepository extends JpaRepository<TCNUD, TCNUD.TCNUD_ID> {

//    @Query(value = "SELECT  *  from tcnud WHERE BranchNo=?1 AND CustSeq=?2",nativeQuery = true)
//    List<TCNUD> findByBC(String BranchNo, String CustSeq);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tcnud Values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13)", nativeQuery = true)
    void create(String TradeDate,
                String BranchNo,
                String CustSeq,
                String DocSeq,
                String Stock,
                Double Price,
                Long Qty,
                Long RemainQty,
                Long Fee,
                Long Cost,
                String ModDate,
                String ModTime,
                String ModUser);

    @Query(value = "SELECT  *  from tcnud WHERE BranchNo=?1 AND CustSeq=?2 AND Stock=?3", nativeQuery = true)
    List<TCNUD> findByBCS(String BranchNo, String CustSeq, String Stock);


    @Query(value = "SELECT  *  from tcnud WHERE TradeDate=?1 AND BranchNo=?2 AND CustSeq=?3 AND DocSeq=?4", nativeQuery = true)
    List<TCNUD>  findByTBCD(String TradeDate , String BranchNo, String CustSeq, String DocSeq);

    @Query(value = "select distinct Stock from tcnud where BranchNo = ?1 and CustSeq = ?2 order by Stock", nativeQuery = true)
    List<String> getStockList(String branchNo ,String custSeq);

    @Query(value = "select sum(Cost) from tcnud where BranchNo= ?1 and CustSeq= ?2 and TradeDate= ?3 ;",nativeQuery = true)
    Long getSumCost(String branchNo, String custSeq, String tradeDate);
}
