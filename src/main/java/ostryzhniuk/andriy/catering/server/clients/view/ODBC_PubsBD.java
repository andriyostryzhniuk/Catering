package ostryzhniuk.andriy.catering.server.clients.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ostryzhniuk.andriy.catering.clients.view.dto.DtoClient;
import ostryzhniuk.andriy.catering.debtors.view.dto.DtoDebtors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.creteSimpleJdbcInsert;
import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    private static SimpleJdbcInsert simpleJdbcInsertForClient;

    private static SimpleJdbcInsert getSimpleJdbcInsertForClient() {
        if (simpleJdbcInsertForClient == null) {
            simpleJdbcInsertForClient = creteSimpleJdbcInsert();
            simpleJdbcInsertForClient
                    .withTableName("client")
                    .usingGeneratedKeyColumns("id");
        }
        return simpleJdbcInsertForClient;
    }

    public static List<DtoClient> selectClients() {
        List<DtoClient> dtoClientsList = getJdbcTemplate().query("select id, name, address, telephoneNumber, " +
                "contactPerson, discount, email, icq, skype " +
                "from client " +
                "order by name", BeanPropertyRowMapper.newInstance(DtoClient.class));
        return dtoClientsList;
    }

    public static void insertClient(String name, String address, String telephoneNumber, String contactPerson,
                                              BigDecimal discount, String email, Integer icq, String skype){
        Map<String, Object> parameters = new HashMap<>(8);
        parameters.put("name", name);
        parameters.put("address", address);
        parameters.put("telephoneNumber", telephoneNumber);
        parameters.put("contactPerson", contactPerson);
        parameters.put("discount", discount);
        parameters.put("email", email);
        parameters.put("icq", icq);
        parameters.put("skype", skype);
        getSimpleJdbcInsertForClient().execute(parameters);
    }

//    public static void insertClient(DtoClient dtoClient){
//        String sqlInsert = "INSERT INTO client (id, name, address, telephoneNumber, contactPerson, " +
//                "discount, email, icq, skype) " +
//                "VALUES (:id, :name, :address, :telephoneNumber, :contactPerson, :discount, :email, :icq, :skype)";
//        getJdbcTemplate().update(sqlInsert, new BeanPropertySqlParameterSource(dtoClient));
//    }

    public static void updateClient(String name, String address, String telephoneNumber, String contactPerson,
                                   BigDecimal discount, String email, Integer icq, String skype, int id){
        getJdbcTemplate().update("UPDATE client " +
                "SET name = ?, " +
                "address = ?, " +
                "telephoneNumber = ?, " +
                "contactPerson = ?, " +
                "discount = ?, " +
                "email = ?, " +
                "icq = ?, " +
                "skype = ? " +
                "WHERE id = ?", name, address, telephoneNumber, contactPerson, discount, email, icq, skype, id);
    }

    public static void deleteClient(int clientId){
        getJdbcTemplate().update("DELETE FROM client " +
                "WHERE id = ?", clientId);
    }

    public static List<DtoDebtors> selectDebtors(){
        return getJdbcTemplate().query("select client.id as clientId, client.name as clientName, " +
                "client.telephoneNumber, client.contactPerson, ordering.id as orderId, " +
                "ordering.date, ordering.cost, ordering.discount, ordering.paid, bill, ordering.paid, debt " +
                "from ordering, client, ( " +
                "   select id, bill, truncate(bill - paid, 2) as debt " +
                "   from ( " +
                "       select ordering.id as id, ordering.paid as paid, " +
                "       truncate(cost - (cost * (ordering.discount / 100)), 2) as bill " +
                "       from ordering) billTable) debtTable " +
                "where debtTable.debt > 0 and " +
                "ordering.id = debtTable.id and " +
                "ordering.client_id = client.id " +
                "order by client.name", BeanPropertyRowMapper.newInstance(DtoDebtors.class));
    }

}