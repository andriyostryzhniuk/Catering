package ostryzhniuk.andriy.catering.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import ostryzhniuk.andriy.catering.dto.DtoOrdering;
import java.util.List;
import static ostryzhniuk.andriy.catering.mysql.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DtoOrdering> selectOrdering() {
        List<DtoOrdering> dtoOrderings = getJdbcTemplate().query("select ordering.id, ordering.date, " +
                "client.name, ordering.cost, ordering.discount, ordering.paid " +
                "from ordering, client " +
                "where ordering.client_id = client.id " +
                "order by ordering.date asc", BeanPropertyRowMapper.newInstance(DtoOrdering.class));
        return dtoOrderings;
    }

//    public static Integer selectObjectEmployeesId(String date, int employeesId) {
//        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select object_employees.id " +
//                "from object_employees " +
//                "where object_employees.employees_id = '" + employeesId + "' and " +
//                "((object_employees.startDate <= convert('" + date + "', DATE) and " +
//                "object_employees.finishDate is null) or " +
//                "convert('" + date + "', DATE) between object_employees.startDate and object_employees.finishDate)");
//        Integer objectEmployeesId = null;
//        while (rs.next()) {
//            objectEmployeesId = new Integer(rs.getInt(1));
//        }
//        return objectEmployeesId;
//    }
//
//    public static List<DtoObject> selectObjectList(String firstDayOfMonth, String lastDayOfMonth) {
//        List<DtoObject> dtoObjectList = getJdbcTemplate().query("select address, id, startDate, finishDate " +
//                "from object " +
//                "where startDate between convert('" + firstDayOfMonth + "', DATE) and convert('" + lastDayOfMonth + "', DATE) or " +
//                "finishDate between convert('" + firstDayOfMonth + "', DATE) and convert('" + lastDayOfMonth + "', DATE) or " +
//                "startDate < convert('" + firstDayOfMonth + "', DATE) and finishDate > convert('" + lastDayOfMonth + "', DATE) or " +
//                "startDate < convert('" + firstDayOfMonth + "', DATE) and finishDate is null " +
//                "order by address asc", BeanPropertyRowMapper.newInstance(DtoObject.class));
//        return dtoObjectList;
//    }
}