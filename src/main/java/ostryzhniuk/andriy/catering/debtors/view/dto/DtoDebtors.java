package ostryzhniuk.andriy.catering.debtors.view.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DtoDebtors implements Serializable {
    private Integer clientId;
    private String clientName;
    private String telephoneNumber;
    private String contactPerson;
    private Integer orderId;
    private Date date;
    private String formatDate;
    private BigDecimal cost;
    private BigDecimal discount;
    private BigDecimal bill;
    private BigDecimal paid;
    private BigDecimal debt;

    public DtoDebtors() {
    }

    public DtoDebtors(Integer clientId, String clientName, String telephoneNumber, String contactPerson,
                      Integer orderId, Date date, BigDecimal cost, BigDecimal discount, BigDecimal paid) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.telephoneNumber = telephoneNumber;
        this.contactPerson = contactPerson;
        this.orderId = orderId;
        this.date = date;
        this.cost = cost;
        this.discount = discount;
        this.paid = paid;
    }

    public void formattingDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.formatDate = dateFormat.format(this.date);
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
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

    public BigDecimal getDebt() {
        return debt;
    }

    public void setDebt(BigDecimal debt) {
        this.debt = debt;
    }

}

