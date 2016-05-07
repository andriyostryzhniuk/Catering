package ostryzhniuk.andriy.catering.order.view.dto;

import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ToString
public class DtoOrderReport implements Serializable {

    private Date date;
    private String client;
    private String address;
    private Integer orderId;
    private BigDecimal bill;
    private BigDecimal debt;

    public DtoOrderReport() {
    }

    public DtoOrderReport(Date date, String client, String address, Integer orderId, BigDecimal bill, BigDecimal debt) {
        this.date = date;
        this.client = client;
        this.address = address;
        this.orderId = orderId;
        this.bill = bill;
        this.debt = debt;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getBill() {
        return bill;
    }

    public void setBill(BigDecimal bill) {
        this.bill = bill;
    }

    public BigDecimal getDebt() {
        return debt;
    }

    public void setDebt(BigDecimal debt) {
        this.debt = debt;
    }
}
