package ru.spbstu.hsisct.stockmarket.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsisct.stockmarket.model.Order;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderOperationType;
import ru.spbstu.hsisct.stockmarket.model.enums.OrderStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Order save(final Order order) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        if (Objects.nonNull(order.getId())) {
            closeOrder(order.getId());
        }
        jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(""" 
                    INSERT INTO "order" (broker_id, company_id, individual_id, size, min_price, max_price, operation_type, order_status, public, timestamp, parent_id) 
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); 
                """, Statement.RETURN_GENERATED_KEYS);
                injectOrderFieldsIntoStatement(order, statement);

                return statement;
            }, keyHolder);
        final long id = (long) Objects.requireNonNull(keyHolder.getKeys()).get("id");

        return getInsertedOrderById(id);
    }

    @Override
    public Optional<Order> findById(final long id) {

        return Optional.ofNullable(jdbcTemplate.query(con -> {
                    PreparedStatement statement = con.prepareStatement("""
                        SELECT * FROM "order" WHERE ID = ?;
                    """);
                    statement.setLong(1, id);

                    return statement;
                }, OrderRepositoryImpl::constructOrderFromResultSet)
        );
    }

    @Override
    public List<Order> findClientsOrders(long individualId) {
        return Objects.requireNonNull(jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement("""
                        SELECT * FROM "order" WHERE individual_id = ? AND order_status = 'ACTIVE';
                    """);
            statement.setLong(1, individualId);

            return statement;
        }, OrderRepositoryImpl::constructOrderListFromResultSet));
    }

    @Override
    public List<Order> findClientsOrdersForBroker(final long brokerId) {
        return Objects.requireNonNull(jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement("""
                        SELECT * FROM "order" WHERE broker_id = ? AND individual_id IS NOT NULL AND order_status = 'ACTIVE' AND public = false;
                    """);
            statement.setLong(1, brokerId);

            return statement;
        }, OrderRepositoryImpl::constructOrderListFromResultSet));
    }

    @Override
    public List<Order> findOrdersForBroker(final long brokerId) {
        return Objects.requireNonNull(jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement("""
                        SELECT * FROM "order" WHERE broker_id = ? AND individual_id IS NULL AND order_status = 'ACTIVE';
                    """);
            statement.setLong(1, brokerId);

            return statement;
        }, OrderRepositoryImpl::constructOrderListFromResultSet));
    }

    @Override
    public List<Order> findPurchasableCompanyOrders() {
        return Objects.requireNonNull(jdbcTemplate.query("""
                    SELECT * FROM "order" WHERE individual_id IS NULL AND broker_id IS NULL AND order_status = 'ACTIVE';
                """, OrderRepositoryImpl::constructOrderListFromResultSet));
    }

    @Override
    public void deleteById(final Long orderId) {
        final int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("""
                UPDATE "order" SET order_status = 'CLOSED' WHERE id = ?
            """);
            statement.setLong(1, orderId);

            return statement;
        });

        assert rowsAffected <= 1;
    }

    private Order getInsertedOrderById(final long id) {
        return Objects.requireNonNull(jdbcTemplate.query(
                con -> {
                    PreparedStatement statement = con.prepareStatement("""
                        SELECT * FROM "order" WHERE id = ?;
                    """);
                    statement.setLong(1, id);

                    return statement;
                }, OrderRepositoryImpl::constructOrderFromResultSet)
        );
    }

    private void closeOrder(final long id) {
        jdbcTemplate.update(
                con -> {
                    PreparedStatement statement = con.prepareStatement("""
                        UPDATE "order" SET order_status = ? WHERE id = ?
                    """);
                    statement.setString(1, OrderStatus.CLOSED.name());
                    statement.setLong(2, id);

                    return statement;
                });
    }

    private static void injectOrderFieldsIntoStatement(final Order order, final PreparedStatement statement) throws SQLException {
        statement.setObject(1, order.getBrokerId());
        statement.setObject(2, order.getCompanyId());
        statement.setObject(3, order.getIndividualId());
        statement.setLong(4, order.getSize());
        statement.setObject(5, order.isLimitedOrder() ? order.getMinPrice() : null);
        statement.setObject(6, order.isLimitedOrder() ? order.getMaxPrice() : null);
        statement.setString(7, order.getOperationType().name());
        statement.setString(8, order.getOrderStatus().name());
        statement.setBoolean(9, order.isPublic());
        statement.setObject(10, order.getTimestamp());
        statement.setObject(11, order.getParentId());
    }

    private static Order constructOrderFromResultSet(final ResultSet rs) throws SQLException {
        if (!rs.next()) {
            throw new RuntimeException("TODO"); //TODO implement
        }

        return parseResultListIntoOrder(rs);
    }

    private static Order parseResultListIntoOrder(final ResultSet rs) throws SQLException {
        return Order.builder()
                .id(rs.getLong("id"))
                .size(rs.getLong("size"))
                .minPrice(rs.getBigDecimal("min_price"))
                .maxPrice(rs.getBigDecimal("max_price"))
                .operationType(OrderOperationType.valueOf(rs.getString("operation_type")))
                .orderStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .isPublic(rs.getBoolean("public"))
                .timestamp(rs.getObject("timestamp", LocalDateTime.class))
                .parentId(rs.getLong("parent_id") != 0 ? rs.getLong("parent_id") : null)
                .brokerId(rs.getLong("broker_id") != 0 ? rs.getLong("broker_id") : null)
                .individualId(rs.getLong("individual_id") != 0 ? rs.getLong("individual_id") : null)
                .companyId(rs.getLong("company_id"))
                .build();
    }

    private static List<Order> constructOrderListFromResultSet(final ResultSet rs) throws SQLException {
        var result = new ArrayList<Order>();
        while (rs.next()) {
            result.add(parseResultListIntoOrder(rs));
        }

        return result;
    }

}
