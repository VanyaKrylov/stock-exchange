package ru.spbstu.hsisct.stockmarket.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Broker;
import ru.spbstu.hsisct.stockmarket.model.Company;
import ru.spbstu.hsisct.stockmarket.model.Individual;
import ru.spbstu.hsisct.stockmarket.model.Order;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;

import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
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

        if (Objects.nonNull(order.getId())) {
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("""
                    UPDATE "order" SET order_status = ? WHERE id = ?
                """);
                statement.setString(1, OrderStatus.CLOSED.name());
                statement.setLong(2, order.getId());

                return statement;
            });
        }

        final int id = jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(""" 
                INSERT INTO "order" (broker_id, company_id, individual_id, size, min_price, max_price, operation_type, order_status, timestamp, parent_id) 
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?); 
            """);
            statement.setLong(1, broker_id);
            statement.setLong(2, company_id);
            statement.setLong(3, individual_id);
            statement.setLong(4, order.getSize());
            statement.setObject(5, order.isLimitedOrder() ? order.getMinPrice() : null);
            statement.setObject(6, order.isLimitedOrder() ? order.getMaxPrice() : null);
            statement.setString(7, order.getOperationType().name());
            statement.setString(8, order.getOrderStatus().name());
            statement.setObject(9, order.getTimestamp());
            statement.setObject(10, order.getParentId()); //TODO check that Long unboxes and null is also supported

            return statement;
        });

        return Objects.requireNonNull(jdbcTemplate.query(
                con -> {
                    PreparedStatement statement = con.prepareStatement("""
                                SELECT * FROM "order" WHERE id = ?;
                            """);
                    statement.setLong(1, id);

                    return statement;
                },
                rs -> {
                    if (!rs.next()) {
                        throw new RuntimeException("TODO"); //TODO implement
                    }

                    return Order.builder()
                            .id(rs.getLong("id"))
                            .size(rs.getLong("size"))
                            .minPrice(rs.getBigDecimal("min_price"))
                            .maxPrice(rs.getBigDecimal("max_price"))
                            .operationType(OrderOperationType.valueOf(rs.getString("operation_type")))
                            .orderStatus(OrderStatus.valueOf(rs.getString("order_status")))
                            .isPublic(rs.getBoolean("public"))
                            .timestamp(rs.getObject("timestamp", LocalDateTime.class))
                            .parentId(rs.getLong("parent_id"))
                            .build();
                })
        );
    }

    @Override
    public Order findById(long id) {
        return null;
    }
}
