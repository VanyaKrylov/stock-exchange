package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Stock;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {

    @Query(value = """
        SELECT * FROM stock WHERE id IN (SELECT MIN(id) FROM stock GROUP BY type, company_id)
    """, nativeQuery = true)
    List<Stock> findAllUniqueStocks();


    @Query(value = """
        SELECT * FROM stock WHERE id IN (SELECT stock_id FROM individuals_stocks WHERE individual_id = :indivId AND active = true)
    """, nativeQuery = true)
    List<Stock> findAllIndividualsStocks(@Param("indivId") final long indivId);

    @Query(value = """
        SELECT * FROM stock 
            WHERE id IN 
                (SELECT stock_id FROM individuals_stocks 
                    WHERE individual_id = :indivId AND active = true)
            AND company_id = :companyId
    """, nativeQuery = true)
    List<Stock> findAllIndividualsStocksForCompany(@Param("indivId") final long indivId, @Param("companyId") final long companyId);

    @Query(value = """
        SELECT count(*) as amount, s.company_id as \"companyId\", s.type as type FROM stock AS s
            WHERE s.id IN (
                SELECT stock_id FROM individuals_stocks WHERE individual_id = :indivId AND active = true)
            GROUP BY s.type, s.company_id
    """, nativeQuery = true)
    List<StockWithCount> findAllIndividualsStocksGrouped(@Param("indivId") long indivId);

    @Query(value = """
        SELECT count(*) as amount, s.company_id as \"companyId\", s.type as type FROM stock AS s
            WHERE s.id IN (
                SELECT stock_id FROM stocks_owners WHERE broker_id = :brokerId AND active = true)
            AND s.id NOT IN (
                SELECT stock_id FROM individuals_stocks WHERE active = true)
            GROUP BY s.type, s.company_id
    """, nativeQuery = true)
    List<StockWithCount> findAllBrokersStocksGrouped(@Param("brokerId") long brokerId);

    @Query(value = """
        SELECT * FROM stock AS s
            WHERE s.id IN (
                SELECT stock_id FROM stocks_owners WHERE broker_id = :brokerId AND active = true)
            AND s.id NOT IN (
                SELECT stock_id FROM individuals_stocks WHERE active = true)
            AND company_id = :companyId
    """, nativeQuery = true)
    List<Stock> findAllBrokerStocksForCompany(@Param("brokerId") final long brokerId, @Param("companyId") final long companyId);

    @Query(value = """
        SELECT count(*) FROM stock AS s
            WHERE s.id IN (
                SELECT stock_id FROM individuals_stocks
                    WHERE individual_id = :indivId AND active = true AND company_id = :companyId)
            GROUP BY s.company_id
    """, nativeQuery = true)
    Long countIndividualsStocksOfCompany(@Param("indivId") long indivId, @Param("companyId") long companyId);

    Long countStockByCompanyId(final Long companyId);

    @Query(value = """
        SELECT * FROM stock WHERE id NOT IN (SELECT stock_id FROM stocks_owners WHERE active = true) AND company_id = :companyId
    """, nativeQuery = true)
    List<Stock> findNotOwnedStocks(@Param("companyId") Long companyId);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = """
        SELECT COUNT(*) AS amount, s.type AS type FROM stock AS s WHERE company_id = :companyId GROUP BY s.type
    """, nativeQuery = true)
    List<StockInfoWithoutCompany> findAllStocksForCompanyGroupedByType(@Param("companyId") final Long companyId);

    interface StockWithCount {
        Long getAmount();
        Long getCompanyId();
        String getType();
    }

    interface StockInfoWithoutCompany {
        Long getAmount();
        String getType();
    }
}
