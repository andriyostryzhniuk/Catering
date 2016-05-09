package ostryzhniuk.andriy.catering.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.debtors.view.dto.DtoDebtors;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;
import ostryzhniuk.andriy.catering.server.order.view.ODBC_PubsBD;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import static ostryzhniuk.andriy.catering.server.clients.view.ODBC_PubsBD.*;
import static ostryzhniuk.andriy.catering.server.menu.view.ODBC_PubsBD.*;
import static ostryzhniuk.andriy.catering.server.order.view.ODBC_PubsBD.selectMealsADay;
import static ostryzhniuk.andriy.catering.server.order.view.ODBC_PubsBD.selectOrderReport;

public class ClientCommand implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCommand.class);

    private ClientCommandTypes clientCommandType;
    private List<Object> objectList;

    public ClientCommand(ClientCommandTypes clientCommandType, List<Object> objectList) {
        this.clientCommandType = clientCommandType;
        this.objectList = objectList;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public ClientCommandTypes getClientCommandType() {
        return clientCommandType;
    }

    public Object processCommand() {
//        for order view
        if (clientCommandType == ClientCommandTypes.SELECT_ORDER) {
            return selectOrders();

        } else if (clientCommandType == ClientCommandTypes.SELECT_ORDER_BY_DATE) {
            return selectOrdersByDate();

        } else if (clientCommandType == ClientCommandTypes.SELECT_ORDER_BY_ID) {
            return selectOrdersById();

        } else if (clientCommandType == ClientCommandTypes.SELECT_CLIENT_NAMES) {
            return ODBC_PubsBD.selectClientNames();

        } else if (clientCommandType == ClientCommandTypes.INSERT_ORDER) {
            return insertOrder();

        } else if (clientCommandType == ClientCommandTypes.SELECT_CLIENT_ID) {
            return ODBC_PubsBD.selectClientId((String)objectList.get(0));

        } else if (clientCommandType == ClientCommandTypes.UPDATE_ORDER) {
            updateOrder();
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.DELETE_ORDER) {
            List<Boolean> booleanList = new LinkedList<>();
            booleanList.add(ODBC_PubsBD.deleteOrder((Integer)objectList.get(0)));
            return booleanList;
//        for ordering view
        } else if (clientCommandType == ClientCommandTypes.SELECT_ORDERING) {
            return ODBC_PubsBD.selectOrdering((Integer) objectList.get(0));

        } else if (clientCommandType == ClientCommandTypes.INSERT_ORDERING) {
            List<DtoOrdering> dtoOrderingList = new LinkedList<>();
            objectList.forEach(item -> dtoOrderingList.add((DtoOrdering) item));
            ODBC_PubsBD.insertOrdering(dtoOrderingList);
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.UPDATE_ORDERING) {
            List<DtoOrdering> dtoOrderingList = new LinkedList<>();
            objectList.forEach(item -> dtoOrderingList.add((DtoOrdering) item));
            ODBC_PubsBD.updateOrdering(dtoOrderingList);
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.DELETE_ORDERING) {
            List<Integer> dtoOrderingList = new LinkedList<>();
            objectList.forEach(item -> dtoOrderingList.add((Integer) item));
            ODBC_PubsBD.deleteOrdering(dtoOrderingList);
            return new LinkedList<>();
//      for client view
        } else if (clientCommandType == ClientCommandTypes.SELECT_CLIENT) {
            return selectClients();

        } else if (clientCommandType == ClientCommandTypes.INSERT_CLIENT) {
            insertClients();
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.UPDATE_CLIENT) {
            updateClients();
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.DELETE_CLIENT) {
            List<Boolean> booleanList = new LinkedList<>();
            booleanList.add(deleteClient((Integer) objectList.get(0)));
            return booleanList;
//      for menu view
        } else if (clientCommandType == ClientCommandTypes.SELECT_ALL_OF_MENU) {
            return selectAllOfMenu();

        } else if (clientCommandType == ClientCommandTypes.SELECT_SOME_TYPE_OF_MENU) {
            return selectSomeTypeOfMenu((Integer)objectList.get(0));

        } else if (clientCommandType == ClientCommandTypes.SELECT_DISHES_TYPE_NAME) {
            return selectDishesTypeNames();

        } else if (clientCommandType == ClientCommandTypes.SELECT_DISHES_TYPE_ID) {
            return selectDishesTypeId((String)objectList.get(0));

        } else if (clientCommandType == ClientCommandTypes.INSERT_MENU) {
            insertMenu((Integer) objectList.get(0), (String) objectList.get(1), (BigDecimal) objectList.get(2),
                    (Double)objectList.get(3), (String) objectList.get(4));
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.UPDATE_MENU) {
            updateMenu((Integer) objectList.get(0), (String) objectList.get(1), (BigDecimal) objectList.get(2),
                    (Double)objectList.get(3), (String) objectList.get(4), (Integer)objectList.get(5));
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.DELETE_MENU) {
            List<Boolean> booleanList = new LinkedList<>();
            booleanList.add(deleteMenu((Integer) objectList.get(0)));
            return booleanList;

        } else if (clientCommandType == ClientCommandTypes.SELECT_OF_LIKE_NAMES_MENU) {
            return selectOfLikeNamesMenu((String) objectList.get(0), (String) objectList.get(1));
//      for dishes type view
        } else if (clientCommandType == ClientCommandTypes.SELECT_DISHES_TYPE) {
            return selectDishesType();

        } else if (clientCommandType == ClientCommandTypes.INSERT_DISHES_TYPE) {
            insertDishesType((String) objectList.get(0));
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.UPDATE_DISHES_TYPE) {
            updateDishesType((String) objectList.get(0), (Integer) objectList.get(1));
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.DELETE_DISHES_TYPE) {
            List<Boolean> booleanList = new LinkedList<>();
            booleanList.add(deleteDishesType((Integer)objectList.get(0)));
            return booleanList;
//      for debtors view
        } else if (clientCommandType == ClientCommandTypes.SELECT_DEBTORS) {
            return dtoDebtorses();
//      for order report
        } else if (clientCommandType == ClientCommandTypes.SELECT_ORDER_REPORT) {
            return selectOrderReport((Date) objectList.get(0));

        } else if (clientCommandType == ClientCommandTypes.SELECT_MEALS_A_DAY) {
            return selectMealsADay((Date) objectList.get(0));

        } else {
            throw new IllegalArgumentException("NO SUCH COMMAND");
        }
    }


    private List<DtoOrder> selectOrders(){
        List<DtoOrder> dtoOrdersList = ODBC_PubsBD.selectOrders();
        dtoOrdersList.forEach(item -> {
            item.formattingDate();
            item.calculationBill();
        });
        return dtoOrdersList;
    }

    private List<DtoOrder> selectOrdersByDate(){
        List<DtoOrder> dtoOrdersList = ODBC_PubsBD.selectOrdersByDate((Date) objectList.get(0));
        dtoOrdersList.forEach(item -> {
            item.formattingDate();
            item.calculationBill();
        });
        return dtoOrdersList;
    }

    private List<DtoOrder> selectOrdersById(){
        List<DtoOrder> dtoOrdersList = ODBC_PubsBD.selectOrdersById((Integer) objectList.get(0));
        dtoOrdersList.forEach(item -> {
            item.formattingDate();
            item.calculationBill();
        });
        return dtoOrdersList;
    }

    private List<Integer> insertOrder(){
        return ODBC_PubsBD.insertOrder((String)objectList.get(0), (Integer)objectList.get(1),
                (BigDecimal)objectList.get(2), (BigDecimal)objectList.get(3), (BigDecimal)objectList.get(4));
    }

    private void updateOrder(){
        ODBC_PubsBD.updateOrder((Integer)objectList.get(5), (String)objectList.get(0), (Integer)objectList.get(1),
                (BigDecimal)objectList.get(2), (BigDecimal)objectList.get(3), (BigDecimal)objectList.get(4));
    }

    private void insertClients(){
        insertClient((String) objectList.get(0), (String) objectList.get(1), (String) objectList.get(2),
                (String) objectList.get(3), (BigDecimal) objectList.get(4), (String) objectList.get(5),
                (Integer) objectList.get(6), (String) objectList.get(7));

//        insertClient(new DtoClient(null, (String) objectList.get(0), (String) objectList.get(1), (String) objectList.get(2),
//                (String) objectList.get(3), (BigDecimal) objectList.get(4), (String) objectList.get(5),
//                (Integer) objectList.get(6), (String) objectList.get(7)));
    }

    private void updateClients(){
        updateClient((String) objectList.get(0), (String) objectList.get(1), (String) objectList.get(2),
                (String) objectList.get(3), (BigDecimal) objectList.get(4), (String) objectList.get(5),
                (Integer) objectList.get(6), (String) objectList.get(7), (Integer) objectList.get(8));
    }

    private List<DtoDebtors> dtoDebtorses(){
        List<DtoDebtors> dtoDebtorsList = selectDebtors();
        dtoDebtorsList.forEach(item -> {
            item.formattingDate();
        });
        return dtoDebtorsList;
    }

}
