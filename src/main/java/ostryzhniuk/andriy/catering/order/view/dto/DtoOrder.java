package ostryzhniuk.andriy.catering.order.view.dto;

import lombok.ToString;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ToString
public class DtoOrder implements Serializable {

    private int id;
    private Date date;
    private String client;
    private BigDecimal cost;
    private BigDecimal discount;
    private BigDecimal bill;
    private BigDecimal paid;
    private String formatDate;

    public DtoOrder() {
    }

    public DtoOrder(int id, Date date, String client, BigDecimal cost, BigDecimal discount, BigDecimal paid) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.cost = cost;
        this.discount = discount;
        this.paid = paid;
    }

    public void calculationBill(){
        this.bill = cost.subtract(cost.multiply(discount).divide(new BigDecimal(100))).setScale(2, RoundingMode.CEILING);
    }

    public void formattingDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.formatDate = dateFormat.format(this.date);
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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getBill() {
        return bill;
    }

    public void setBill(BigDecimal bill) {
        this.bill = bill;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }
}
