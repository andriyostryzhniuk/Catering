package ostryzhniuk.andriy.catering.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.server.order.view.ODBC_PubsBD;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andriy on 04/10/2016.
 */
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

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public ClientCommandTypes getClientCommandType() {
        return clientCommandType;
    }

    public void setClientCommandType(ClientCommandTypes clientCommandType) {
        this.clientCommandType = clientCommandType;
    }

    public Object processCommand() {
        if (clientCommandType == ClientCommandTypes.SELECT_ORDER) {
            return selectOrders();

        } else if (clientCommandType == ClientCommandTypes.SELECT_CLIENT_NAMES) {
            return ODBC_PubsBD.selectClientNames();

        } else if (clientCommandType == ClientCommandTypes.INSERT_ORDER) {
            insertOrder();
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.SELECT_CLIENT_ID) {
            return ODBC_PubsBD.selectClientId((String)objectList.get(0));

        } else if (clientCommandType == ClientCommandTypes.UPDATE_ORDER) {
            updateOrder();
            return new LinkedList<>();

        } else if (clientCommandType == ClientCommandTypes.DELETE_ORDER) {
            ODBC_PubsBD.deleteOrder((Integer)objectList.get(0));
            return new LinkedList<>();

        } else {
            throw new IllegalArgumentException("NO SUCH COMMAND");
        }
    }


    public List<DtoOrder> selectOrders(){
        List<DtoOrder> dtoOrdersList = ODBC_PubsBD.selectOrders();
        dtoOrdersList.forEach(item -> {
            item.formattingDate();
            item.calculationBill();
        });
        return dtoOrdersList;
    }

    public void insertOrder(){
        ODBC_PubsBD.insertOrder((String)objectList.get(0), (Integer)objectList.get(1), (BigDecimal)objectList.get(2),
                (BigDecimal)objectList.get(3), (BigDecimal)objectList.get(4));
    }

    public void updateOrder(){
        ODBC_PubsBD.updateOrder((Integer)objectList.get(5), (String)objectList.get(0), (Integer)objectList.get(1),
                (BigDecimal)objectList.get(2), (BigDecimal)objectList.get(3), (BigDecimal)objectList.get(4));
    }

}
