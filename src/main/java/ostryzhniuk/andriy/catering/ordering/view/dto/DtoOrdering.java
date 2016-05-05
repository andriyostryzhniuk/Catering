package ostryzhniuk.andriy.catering.ordering.view.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DtoOrdering implements Serializable {
    private Integer id;
    private Integer orderId;
    private Integer menuId;
    private String dishesName;
    private Integer numberOfServings;
    private BigDecimal price;
    private BigDecimal sumPrice = new BigDecimal(0);

    public DtoOrdering() {
    }

    public DtoOrdering(Integer id, Integer orderId, Integer menuId, String dishesName,
                       Integer numberOfServings, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.menuId = menuId;
        this.dishesName = dishesName;
        this.numberOfServings = numberOfServings;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }

    public void calculateSumPrice(){
        this.sumPrice = price.multiply(new BigDecimal(numberOfServings)).setScale(2, RoundingMode.CEILING);
    }

}
