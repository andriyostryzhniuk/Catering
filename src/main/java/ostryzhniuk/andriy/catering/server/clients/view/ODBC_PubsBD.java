package ostryzhniuk.andriy.catering.server.clients.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ostryzhniuk.andriy.catering.clients.view.dto.DtoClient;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DtoClient> selectClients() {
        List<DtoClient> dtoClientsList = getJdbcTemplate().query("select id, name, address, telephoneNumber, " +
                "contactPerson, discount, email, icq, skype " +
                "from client " +
                "order by name", BeanPropertyRowMapper.newInstance(DtoClient.class));
        return dtoClientsList;
    }

    public static void insertClient(String name, String address, String telephoneNumber, String contactPerson,
                                    BigDecimal discount, String email, Integer icq, String skype){
        getJdbcTemplate().update("INSERT INTO client (id, name, address, telephoneNumber, contactPerson, " +
                "discount, email, icq, skype) " +
                "VALUES (null, '" + name + "', '" + address + "', '" + telephoneNumber + "', '" + contactPerson + "', " +
                "" + discount + ", '" + email + "', " + icq + ", '" + skype + "')");
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
                "SET name = '" + name + "', " +
                "address = '" + address + "', " +
                "telephoneNumber = '" + telephoneNumber + "', " +
                "contactPerson = '" + contactPerson + "', " +
                "discount = " + discount + ", " +
                "email = '" + email + "', " +
                "icq = " + icq + ", " +
                "skype = '" + skype + "' " +
                "WHERE id = " + id + "");
    }

    public static void deleteClient(int clientId){
        getJdbcTemplate().update("DELETE FROM client " +
                "WHERE id = ?", clientId);
    }

}