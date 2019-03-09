package com.example.homestaytesting;

public class Upload {
    private String imgName;
    private String imgUrl;
    private String imgDetails;
    private String imgLocation;
    private String hmPrice;
    private String hmContact;
    private String Uid;

    public Upload(){
        //empty constructor needed
    }

    public Upload(String name, String imageUrl, String imageDetails,String imageLocation, String price, String contact, String uid){
        if (name.trim().equals("")){
            name = "No Name";
        }
        imgName = name;
        imgUrl = imageUrl;
        imgDetails = imageDetails;
        imgLocation = imageLocation;
        hmPrice = price;
        hmContact = contact;
        Uid = uid;

    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgDetails() {
        return imgDetails;
    }

    public void setImgDetails(String imgDetails) {
        this.imgDetails = imgDetails;
    }

    public String getImgLocation() {
        return imgLocation;
    }

    public void setImgLocation(String imgLocation) {
        this.imgLocation = imgLocation;
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
}
