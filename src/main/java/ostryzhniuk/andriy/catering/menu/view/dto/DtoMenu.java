package ostryzhniuk.andriy.catering.menu.view.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Andriy on 04/19/2016.
 */
public class DtoMenu implements Serializable {
    private Integer id;
    private Integer dishesTypeId;
    private String dishesTypeName;
    private String name;
    private BigDecimal price;
    private Double mass;
    private String ingredients;

    public DtoMenu() {
    }

    public DtoMenu(Integer id, Integer dishesTypeId, String dishesTypeName, String name, BigDecimal price, Double mass,
                   String ingredients) {
        this.id = id;
        this.dishesTypeId = dishesTypeId;
        this.dishesTypeName = dishesTypeName;
        this.name = name;
        this.price = price;
        this.mass = mass;
        this.ingredients = ingredients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDishesTypeId() {
        return dishesTypeId;
    }

    public void setDishesTypeId(Integer dishesTypeId) {
        this.dishesTypeId = dishesTypeId;
    }

    public String getDishesTypeName() {
        return dishesTypeName;
    }

    public void setDishesTypeName(String dishesTypeName) {
        this.dishesTypeName = dishesTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
