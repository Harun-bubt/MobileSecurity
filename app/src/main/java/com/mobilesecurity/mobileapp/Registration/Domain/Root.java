package com.mobilesecurity.mobileapp.Registration.Domain;

public class Root{
    public String mobile;
    public String masking;
    public String status;

    public Root(String mobile, String masking, String status) {
        this.mobile = mobile;
        this.masking = masking;
        this.status = status;
    }

    public Root() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMasking() {
        return masking;
    }

    public void setMasking(String masking) {
        this.masking = masking;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
