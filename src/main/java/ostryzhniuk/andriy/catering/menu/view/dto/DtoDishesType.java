package ostryzhniuk.andriy.catering.menu.view.dto;

import java.io.Serializable;

/**
 * Created by Andriy on 04/25/2016.
 */
public class DtoDishesType implements Serializable {
    private int id;
    private String type;

    public DtoDishesType() {
    }

    public DtoDishesType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
