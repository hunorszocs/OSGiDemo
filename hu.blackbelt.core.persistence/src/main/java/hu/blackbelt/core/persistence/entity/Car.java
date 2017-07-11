package hu.blackbelt.core.persistence.entity;

import lombok.Data;

import javax.annotation.Generated;

/**
 * Car is a Querydsl bean type
 */
@Data
@Generated("com.querydsl.codegen.BeanSerializer")
public class Car {

    private java.math.BigInteger id;

    private String licenseplate;

    private String rim;

    private java.math.BigInteger speed;

    public java.math.BigInteger getId() {
        return id;
    }

    public void setId(java.math.BigInteger id) {
        this.id = id;
    }

    public String getLicenseplate() {
        return licenseplate;
    }

    public void setLicenseplate(String licenseplate) {
        this.licenseplate = licenseplate;
    }

    public String getRim() {
        return rim;
    }

    public void setRim(String rim) {
        this.rim = rim;
    }

    public java.math.BigInteger getSpeed() {
        return speed;
    }

    public void setSpeed(java.math.BigInteger speed) {
        this.speed = speed;
    }

}

