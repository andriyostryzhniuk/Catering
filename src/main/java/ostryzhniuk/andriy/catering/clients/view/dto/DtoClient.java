package ostryzhniuk.andriy.catering.clients.view.dto;

import java.math.BigDecimal;

/**
 * Created by Andriy on 04/18/2016.
 */
public class DtoClient {
    private int id;
    private String name;
    private String address;
    private String telephoneNumber;
    private String contactPerson;
    private BigDecimal discount;
    private String email;
    private Integer icq;
    private String skype;

    public DtoClient() {
    }

    public DtoClient(String address, String contactPerson, BigDecimal discount, String email, Integer icq, int id, String name, String skype, String telephoneNumber) {
        this.address = address;
        this.contactPerson = contactPerson;
        this.discount = discount;
        this.email = email;
        this.icq = icq;
        this.id = id;
        this.name = name;
        this.skype = skype;
        this.telephoneNumber = telephoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIcq() {
        return icq;
    }

    public void setIcq(Integer icq) {
        this.icq = icq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
