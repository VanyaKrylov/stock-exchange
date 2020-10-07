package ru.spbstu.hsisct.stockmarket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.dto.StockDto;
import ru.spbstu.hsisct.stockmarket.model.Stock;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {

    @Query(value = """
        SELECT * FROM stock WHERE id IN (SELECT MIN(id) FROM stock GROUP BY type, company_id)
    """, nativeQuery = true)
    List<Stock> getAllUniqueStocks();


    @Query(value = """
        SELECT * FROM stock WHERE id IN (SELECT stock_id FROM individuals_stocks WHERE individual_id = :indivId AND active = true)
    """, nativeQuery = true)
    List<Stock> getAllIndividualsStocks(@Param("indivId") final long indivId);

    @Query(value = """
        SELECT count(*) as amount, s.company_id as \"companyId\", s.type as type FROM stock AS s
            WHERE s.id IN (
                SELECT stock_id FROM individuals_stocks WHERE individual_id = :indivId AND active = true)
            GROUP BY s.type, s.company_id
    """, nativeQuery = true)
    List<StockWithCount> getAllIndividualsStocksGrouped(@Param("indivId") long indivId);

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
        SELECT * FROM stock WHERE id NOT IN (SELECT stock_id FROM stocks_owners WHERE active = true)
    """, nativeQuery = true)
    List<Stock> findNotOwnedStocks();

    interface StockWithCount {
        Long getAmount();
        Long getCompanyId();
        String getType();
    }
}
