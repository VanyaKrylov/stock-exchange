package ru.spbstu.hsisct.stockmarket.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.model.Order;

import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Order save(final Order order,
                      final long broker_id,
                      final long company_id,
                      final long individual_id) {
        assert broker_id >= 0;
        assert company_id >= 0;
        assert individual_id >= 0;

        final int id = jdbcTemplate.update(con -> { //TODO replace with .query()
            PreparedStatement statement = con.prepareStatement(""" 
                INSERT INTO "ORDER" (broker_id, company_id, individual_id, size, min_price, max_price, operation_type, timestamp, parent_id) 
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?); 
            """);
            statement.setLong(1, broker_id);
            statement.setLong(2, company_id);
            statement.setLong(3, individual_id);
            statement.setLong(4, order.getSize());
            statement.setObject(5, order.isLimitedOrder() ? order.getMinPrice() : null);
            statement.setObject(6, order.isLimitedOrder() ? order.getMaxPrice() : null);
            statement.setString(7, order.getOperationType().name());
            statement.setObject(8, order.getTimestamp());
            statement.setObject(9, order.getParentId()); //TODO check that Long unboxes and null is also supported

            return statement;
        });

        return ;
    }

    @Override
    public Order findById(long id) {
        return null;
    }
}
