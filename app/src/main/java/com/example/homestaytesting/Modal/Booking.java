package com.example.homestaytesting.Modal;

public class Booking {
    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getHmName() {
        return hmName;
    }

    public void setHmName(String hmName) {
        this.hmName = hmName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    private String hmId;
    private String hmName;
    private String ownerId;
    private String uid;
    private String userName;
    private String userEmail;
    private String userContact;
    private String bookDate;
    private String paymentDate;
    private String totalPrice;

    public Booking(){}

    public Booking(String homeId, String homeName, String ownerid, String userid, String userName, String userEmail, String userContact,
                   String bookingDate, String payDate, String payTotal){
        this.hmId = homeId;
        this.hmName = homeName;
        this.ownerId = ownerid;
        this.uid = userid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userContact = userContact;
        this.bookDate = bookingDate;
        this.paymentDate = payDate;
        this.totalPrice = payTotal;
    }
}
