package ostryzhniuk.andriy.catering.order.view.dto;

import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;

public class DtoOrderingForReport extends DtoOrdering {

    private String dishesType;

    public DtoOrderingForReport() {
    }

    public DtoOrderingForReport(String dishesName, Integer numberOfServings, String dishesType) {
        super(dishesName, numberOfServings);
        this.dishesType = dishesType;
    }

    public String getDishesType() {
        return dishesType;
    }

    public void setDishesType(String dishesType) {
        this.dishesType = dishesType;
    }
}
