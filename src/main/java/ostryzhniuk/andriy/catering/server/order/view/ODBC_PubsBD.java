package ostryzhniuk.andriy.catering.server.order.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrderReport;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrderingForReport;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.creteSimpleJdbcInsert;
import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.getJdbcTemplate;
import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.getNamedParameterJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    private static SimpleJdbcInsert simpleJdbcInsertForOrder;

    private static SimpleJdbcInsert getSimpleJdbcInsertForOrder() {
        if (simpleJdbcInsertForOrder == null) {
            simpleJdbcInsertForOrder = creteSimpleJdbcInsert();
            simpleJdbcInsertForOrder
                    .withTableName("ordering")
                    .usingGeneratedKeyColumns("id");
        }
        return simpleJdbcInsertForOrder;
    }

    public static List<DtoOrder> selectOrders() {
        List<DtoOrder> dtoOrdersList = getJdbcTemplate().query("select ordering.id, ordering.date, " +
                "client.name as client, ordering.cost, ordering.discount, ordering.paid " +
                "from ordering, client " +
                "where ordering.client_id = client.id " +
                "order by ordering.date desc", BeanPropertyRowMapper.newInstance(DtoOrder.class));
        return dtoOrdersList;
    }

    public static List<DtoOrder> selectOrdersByDate(Date date) {
        List<DtoOrder> dtoOrdersList = getJdbcTemplate().query("select ordering.id, ordering.date, " +
                "client.name as client, ordering.cost, ordering.discount, ordering.paid " +
                "from ordering, client " +
                "where ordering.date = ? and " +
                "ordering.client_id = client.id " +
                "order by ordering.date desc", BeanPropertyRowMapper.newInstance(DtoOrder.class), date);
        return dtoOrdersList;
    }

    public static List<String> selectClientNames() {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select client.name " +
                "from client " +
                "order by client.name asc");
        List<String> list = new LinkedList<>();
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        return list;
    }

    public static List<Integer> selectClientId(String clientName){
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select client.id " +
                "from client " +
                "where client.name = ?", clientName);
        List<Integer> clientIdList = new LinkedList<>();
        while (rs.next()) {
            clientIdList.add(rs.getInt(1));
        }
        return clientIdList;
    }

    public static List<Integer> insertOrder(String date, Integer clientId, BigDecimal cost, BigDecimal discount, BigDecimal paid){
        Map<String, Object> parameters = new HashMap<>(5);
        parameters.put("date", date);
        parameters.put("client_id", clientId);
        parameters.put("cost", cost);
        parameters.put("discount", discount);
        parameters.put("paid", paid);
        List<Integer> orderIdList = new LinkedList<>();
        orderIdList.add(getSimpleJdbcInsertForOrder().executeAndReturnKey(parameters).intValue());
        return orderIdList;
    }

    public static void updateOrder(int id, String date, int clientId, BigDecimal cost, BigDecimal discount, BigDecimal paid){
        getJdbcTemplate().update("UPDATE ordering " +
                "SET date = convert(?, DATE), " +
                "client_id = ?, " +
                "cost = ?, " +
                "discount = ?, " +
                "paid = ? " +
                "WHERE id = ?", date, clientId, cost, discount, paid, id);
    }

    public static Boolean deleteOrder(int orderId){
        try {
            getJdbcTemplate().update("DELETE FROM ordering " +
                    "WHERE id = ?", orderId);
        } catch (DataIntegrityViolationException e) {
            return false;
        }
        return true;
    }

    public static List<DtoOrdering> selectOrdering(Integer orderId) {
        return getJdbcTemplate().query("select ordering_menu.id, ordering_menu.ordering_id as orderId, " +
                "ordering_menu.menu_id as menuId, menu.name as dishesName, ordering_menu.servings as numberOfServings, " +
                "menu.price " +
                "from ordering_menu, menu " +
                "where ordering_menu.ordering_id = ? and " +
                "menu.id = ordering_menu.menu_id " +
                "order by menu.name asc", BeanPropertyRowMapper.newInstance(DtoOrdering.class), orderId);
    }

    public static void insertOrdering(List<DtoOrdering> dtoOrderingList){
        getNamedParameterJdbcTemplate().batchUpdate("INSERT INTO ordering_menu (id, ordering_id, menu_id, servings) " +
                "VALUES (:id, :orderId, :menuId, :numberOfServings)",
                SqlParameterSourceUtils.createBatch(dtoOrderingList.toArray()));
    }

    public static void updateOrdering(List<DtoOrdering> dtoOrderingList){
        getNamedParameterJdbcTemplate().batchUpdate("UPDATE ordering_menu " +
                "SET ordering_id = :orderId, " +
                "menu_id = :menuId, " +
                "servings = :numberOfServings " +
                "WHERE id = :id",
                SqlParameterSourceUtils.createBatch(dtoOrderingList.toArray()));
    }

    public static void deleteOrdering(List<Integer> idList) {
        getJdbcTemplate().batchUpdate("DELETE FROM ordering_menu " +
                "WHERE id = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, idList.get(i));
            }

            @Override
            public int getBatchSize() {
                return idList.size();
            }
        });
    }

    public static List<DtoOrderReport> selectOrderReport (Date date) {
        return getJdbcTemplate().query("select ordering.date, client.name as client, client.address, " +
                "ordering.id as orderId, bill, if(debt > 0, debt, 0) as debt " +
                "from ordering, client, ( " +
                "   select id, bill, truncate(bill - paid, 2) as debt " +
                "   from ( " +
                "       select ordering.id as id, ordering.paid as paid, " +
                "       truncate(cost - (cost * (ordering.discount / 100)), 2) as bill " +
                "       from ordering) billTable) debtTable\n" +
                "where ordering.date = ? and " +
                "ordering.id = debtTable.id and " +
                "ordering.client_id = client.id " +
                "order by client.name", BeanPropertyRowMapper.newInstance(DtoOrderReport.class), date);
    }

    public static List<DtoOrderingForReport> selectMealsADay(Date date) {
        return getJdbcTemplate().query("select menu.name as dishesName, " +
                "sum(ordering_menu.servings) as numberOfServings, dishestype.type as dishesType " +
                "from ordering, ordering_menu, menu, dishestype " +
                "where ordering.date = ? and " +
                "ordering.id = ordering_menu.ordering_id and " +
                "ordering_menu.menu_id = menu.id and " +
                "menu.dishesType_id = dishestype.id " +
                "group by menu_id", BeanPropertyRowMapper.newInstance(DtoOrderingForReport.class), date);
    }

}