package ostryzhniuk.andriy.catering.ordering.view.dto;

import java.io.Serializable;

public class DtoOrdering implements Serializable {
    private Integer id;
    private Integer menuId;
    private String dishesName;
    private Integer numberOfServings;

    public DtoOrdering() {
    }

    public DtoOrdering(Integer id, Integer menuId, String dishesName, Integer numberOfServings) {
        this.id = id;
        this.menuId = menuId;
        this.dishesName = dishesName;
        this.numberOfServings = numberOfServings;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public Integer getNumberOfServings() {
        return numberOfServings;
    }

    public void setNumberOfServings(Integer numberOfServings) {
        this.numberOfServings = numberOfServings;
    }

}
