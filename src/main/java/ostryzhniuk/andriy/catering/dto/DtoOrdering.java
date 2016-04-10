package ostryzhniuk.andriy.catering.dto;

import java.sql.Date;

/**
 * Created by Andriy on 04/10/2016.
 */
public class DtoOrdering {
    private Date date;
    private String client;
    private Double cost;
    private Double discount;
    private Double paid;

    public DtoOrdering() {
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
