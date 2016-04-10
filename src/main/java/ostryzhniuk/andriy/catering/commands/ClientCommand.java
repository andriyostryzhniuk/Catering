package ostryzhniuk.andriy.catering.commands;

import ostryzhniuk.andriy.catering.dto.DtoOrdering;

/**
 * Created by Andriy on 04/10/2016.
 */
public class ClientCommand {

    private ClientCommandTypes clientCommandType;

    private DtoOrdering dtoOrdering;

    public DtoOrdering getDtoOrdering() {
        return dtoOrdering;
    }

    public void setDtoOrdering(DtoOrdering dtoOrdering) {
        this.dtoOrdering = dtoOrdering;
    }

    public void processCommand() {
        clientCommandType = ClientCommandTypes.WRITE;
//        dtoOrdering
    }

}
