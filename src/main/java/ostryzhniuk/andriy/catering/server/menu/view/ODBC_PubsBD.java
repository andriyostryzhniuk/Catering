package ostryzhniuk.andriy.catering.server.menu.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoDishesType;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.server.mysql.DB_Connector.getJdbcTemplate;

public class ODBC_PubsBD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODBC_PubsBD.class);

    public static List<DtoMenu> selectAllOfMenu() {
        List<DtoMenu> dtoMenuList = getJdbcTemplate().query("select menu.id as id,  dishestype.id as dishesTypeId, " +
                "dishestype.type as dishesTypeName, menu.name, menu.price, menu.mass, menu.ingredients " +
                "from menu, dishestype " +
                "where menu.dishesType_id = dishestype.id " +
                "order by dishestype.type", BeanPropertyRowMapper.newInstance(DtoMenu.class));
        return dtoMenuList;
    }

    public static List<DtoMenu> selectSomeTypeOfMenu(Integer dishesTypeId) {
        List<DtoMenu> dtoMenuList = getJdbcTemplate().query("select menu.id as id,  dishestype.id as dishesTypeId, " +
                "dishestype.type as dishesTypeName, menu.name, menu.price, menu.mass, menu.ingredients " +
                "from menu, dishestype " +
                "where menu.dishesType_id = '" +  dishesTypeId + "' and " +
                "menu.dishesType_id = dishestype.id " +
                "order by dishestype.type", BeanPropertyRowMapper.newInstance(DtoMenu.class));
        return dtoMenuList;
    }

    public static List<DtoMenu> selectOfLikeNamesMenu(String likeNames, String dishesTypeName) {
        List<DtoMenu> dtoMenuList;
        if (dishesTypeName.equals("Всі категорії")) {
            dtoMenuList = getJdbcTemplate().query("select menu.id as id,  dishestype.id as dishesTypeId, " +
                    "dishestype.type as dishesTypeName, menu.name, menu.price, menu.mass, menu.ingredients " +
                    "from menu, dishestype " +
                    "where LOWER(menu.name) LIKE LOWER('%" + likeNames + "%') and " +
                    "menu.dishesType_id = dishestype.id " +
                    "order by dishestype.type", BeanPropertyRowMapper.newInstance(DtoMenu.class));
        } else {
            dtoMenuList = getJdbcTemplate().query("select menu.id as id,  dishestype.id as dishesTypeId, " +
                    "dishestype.type as dishesTypeName, menu.name, menu.price, menu.mass, menu.ingredients " +
                    "from menu, dishestype " +
                    "where LOWER(menu.name) LIKE LOWER('%" + likeNames + "%') and " +
                    "menu.dishesType_id = '" +  selectDishesTypeId(dishesTypeName).get(0) + "' and " +
                    "menu.dishesType_id = dishestype.id " +
                    "order by dishestype.type", BeanPropertyRowMapper.newInstance(DtoMenu.class));
        }
        return dtoMenuList;
    }

    public static List<String> selectDishesTypeNames() {
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select dishesType.type " +
                "from dishesType " +
                "order by dishesType.type asc");
        List<String> list = new LinkedList<>();
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        return list;
    }

    public static List<Integer> selectDishesTypeId(String dishesTypeName){
        SqlRowSet rs = getJdbcTemplate().queryForRowSet("select dishesType.id " +
                "from dishesType " +
                "where dishesType.type = '" + dishesTypeName + "'");
        List<Integer> clientIdList = new LinkedList<>();
        while (rs.next()) {
            clientIdList.add(rs.getInt(1));
        }
        return clientIdList;
    }

    public static void insertMenu(Integer dishesTypeId, String name, BigDecimal price, Double mass, String ingredients){
        getJdbcTemplate().update("INSERT INTO menu (id, dishesType_id, name, price, mass, ingredients) " +
                "VALUES (null, '" + dishesTypeId + "', '" + name + "', '" + price + "', '" + mass + "', " +
                "'" + ingredients + "')");
    }

    public static void updateMenu(Integer dishesTypeId, String name, BigDecimal price, Double mass,
                                  String ingredients, int id){
        getJdbcTemplate().update("UPDATE menu " +
                "SET dishesType_id = '" + dishesTypeId + "', " +
                "name = '" + name + "', " +
                "price = '" + price + "', " +
                "mass = '" + mass + "', " +
                "ingredients = '" + ingredients + "' " +
                "WHERE id = '" + id + "'");
    }

    public static void deleteMenu(int menuId){
        getJdbcTemplate().update("DELETE FROM menu " +
                "WHERE id = " + menuId + "");
    }

    public static List<DtoDishesType> selectDishesType(){
        return getJdbcTemplate().query("select id, type " +
                "from dishesType " +
                "order by type asc", BeanPropertyRowMapper.newInstance(DtoDishesType.class));
    }

    public static void insertDishesType(String type){
        getJdbcTemplate().update("INSERT INTO dishesType (id, type) " +
                "VALUES (null, '" + type + "')");
    }

    public static void updateDishesType(String type, Integer id){
        getJdbcTemplate().update("UPDATE dishesType " +
                "SET type = '" + type + "' " +
                "WHERE id = '" + id + "'");
    }

}