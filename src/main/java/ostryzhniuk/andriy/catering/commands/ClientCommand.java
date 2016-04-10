package ostryzhniuk.andriy.catering.commands;

import ostryzhniuk.andriy.catering.dto.DtoOrdering;

import java.io.Serializable;

/**
 * Created by Andriy on 04/10/2016.
 */
public class ClientCommand implements Serializable {

    private ClientCommandTypes clientCommandType;
    private Object object;

    public ClientCommand(ClientCommandTypes clientCommandType, Object object) {
        this.clientCommandType = clientCommandType;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ClientCommandTypes getClientCommandType() {
        return clientCommandType;
    }

    public void setClientCommandType(ClientCommandTypes clientCommandType) {
        this.clientCommandType = clientCommandType;
    }

    public Object processCommand() {
        if (clientCommandType == ClientCommandTypes.WRITE_ORDER) {
            DtoOrdering order = (DtoOrdering) object;
            order.setCost(order.getCost() * 2);
            return order;
            // orderDao.createOrder(order);
        } else if (clientCommandType == ClientCommandTypes.READ_LAST_ORDERS){
            ///
            return null;
         } else {
            throw new IllegalArgumentException("NO SUCH COMMAND");
        }
    }

}
