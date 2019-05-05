package com.example.homestaytesting.Modal;

public class Upload {
    private String hmName;
    private String imgUrl;
    private String hmDetails;
    private String hmLocation;
    private String hmPrice;
    private String hmContact;
    private String Uid;
    private Double lat;
    private Double lang;
    private String hmPropertyType;
    private String hmBedrooms;
    private String hmBathroom;
    private String hmFurnish;
    private String hmId;

    public Upload(){
        //empty constructor needed
    }

    public Upload(String name, String imageUrl, String details, String location, String price, String contact, String uid,
                  Double latitude, Double longitude, String propertyType, String bedrooms, String bathroom, String furnished, String hmid){
        if (name.trim().equals("")){
            name = "No Name";
        }
        hmName = name;
        imgUrl = imageUrl;
        hmDetails = details;
        hmLocation = location;
        hmPrice = price;
        hmContact = contact;
        Uid = uid;
        lat = latitude;
        lang = longitude;
        hmPropertyType = propertyType;
        hmBedrooms = bedrooms;
        hmBathroom = bathroom;
        hmFurnish = furnished;
        hmId = hmid;

    }

    public String getHmName() {
        return hmName;
    }

    public void setHmName(String hmName) {
        this.hmName = hmName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHmDetails() {
        return hmDetails;
    }

    public void setHmDetails(String hmDetails) {
        this.hmDetails = hmDetails;
    }

    public String getHmLocation() {
        return hmLocation;
    }

    public void setHmLocation(String hmLocation) {
        this.hmLocation = hmLocation;
    }

    public String getHmPrice() {
        return hmPrice;
    }

    public void setHmPrice(String hmPrice) {
        this.hmPrice = hmPrice;
    }

    public String getHmContact() {
        return hmContact;
    }

    public void setHmContact(String hmContact) {
        this.hmContact = hmContact;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLang() {
        return lang;
    }

    public void setLang(Double lang) {
        this.lang = lang;
    }

    public String getHmPropertyType() {
        return hmPropertyType;
    }

    public void setHmPropertyType(String hmPropertyType) {
        this.hmPropertyType = hmPropertyType;
    }

    public String getHmBedrooms() {
        return hmBedrooms;
    }

    public void setHmBedrooms(String hmBedrooms) {
        this.hmBedrooms = hmBedrooms;
    }

    public String getHmBathroom() {
        return hmBathroom;
    }

    public void setHmBathroom(String hmBathroom) {
        this.hmBathroom = hmBathroom;
    }

    public String getHmFurnish() {
        return hmFurnish;
    }

    public void setHmFurnish(String hmFurnish) {
        this.hmFurnish = hmFurnish;
    }

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

}
