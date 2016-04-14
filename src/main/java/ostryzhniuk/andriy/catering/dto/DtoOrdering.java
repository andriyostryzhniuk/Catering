package ostryzhniuk.andriy.catering.dto;

import lombok.ToString;
import java.io.Serializable;
import java.util.Date;

@ToString
public class DtoOrdering implements Serializable {

    private int id;
    private Date date;
    private String client;
    private Double cost;
    private Double discount;
    private Double paid;

    public DtoOrdering() {
    }

    public DtoOrdering(int id, Date date, String client, Double cost, Double discount, Double paid) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.cost = cost;
        this.discount = discount * cost;
        this.paid = paid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

}
