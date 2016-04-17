package ostryzhniuk.andriy.catering.server.order.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DtoOrder> selectOrders() {
        List<DtoOrder> dtoOrdersList = getJdbcTemplate().query("select ordering.id, ordering.date, " +
                "client.name as client, ordering.cost, ordering.discount, ordering.paid " +
                "from ordering, client " +
                "where ordering.client_id = client.id " +
                "order by ordering.date desc", BeanPropertyRowMapper.newInstance(DtoOrder.class));
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

    public static void insertOrder(String date, Integer clientId, BigDecimal cost, BigDecimal discount, BigDecimal paid){
        getJdbcTemplate().update("INSERT INTO ordering (id, date, client_id, cost, discount, paid) " +
                "VALUES (null, convert('" + date + "', DATE), " + clientId + ", " + cost + ", " + discount + ", " + paid + ")");
    }

    public static List<Integer> selectClientId(String clientName){
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select client.id " +
                "from client " +
                "where client.name = '" + clientName + "'");
        List<Integer> clientIdList = new LinkedList<>();
        while (rs.next()) {
            clientIdList.add(rs.getInt(1));
        }
        return clientIdList;
    }

}