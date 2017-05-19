package com.vikas.trillo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by OFFICE on 5/18/2017.
 */

public class ProductInfoModel {
    @SerializedName("ProductName")
    @Expose
    private String productName;
    @SerializedName("ProductImage")
    @Expose
    private String productImage;
    @SerializedName("DiscountInfo")
    @Expose
    private String discountInfo;
    @SerializedName("UsedToday")
    @Expose
    private String usedToday;
    @SerializedName("Added")
    @Expose
    private String added;
    @SerializedName("Coupon")
    @Expose
    private String coupon;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(String discountInfo) {
        this.discountInfo = discountInfo;
    }

    public String getUsedToday() {
        return usedToday;
    }

    public void setUsedToday(String usedToday) {
        this.usedToday = usedToday;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
}
