package ostryzhniuk.andriy.catering.sql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ostryzhniuk.andriy.catering.dto.DtoOrdering;

import java.util.List;

import static ostryzhniuk.andriy.catering.sql.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DtoOrdering> select() {
        List<DtoOrdering> dtoOrderingList = getJdbcTemplate().query("select stock.id, " +
                "ifnull(stock.name, stockCategory.name) as name, stockCategory.name as stockCategory " +
                "from stockCategory, stock left join " +
                "   (select distinct stocktracking.stock_id " +
                "   from stocktracking " +
                "   where stocktracking.returnDate is null or " +
                "   stocktracking.returnDate > curdate()) usingStock " +
                "   on stock.id = usingStock.stock_id " +
                "where usingStock.stock_id is null and " +
                "stock.status = 'доступно' and " +
                "stock.stockCategory_id = stockCategory.id " +
                "order by stock.name asc", BeanPropertyRowMapper.newInstance(DtoOrdering.class));
        return dtoOrderingList;
    }
}