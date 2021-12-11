package com.mobilesecurity.mobileapp.Registration.Domain;

public class OtpResponse {
    String mobile;
    String masking;
    String status;

    public OtpResponse() {
    }

    public OtpResponse(String mobile, String status) {
        this.mobile = mobile;
        this.status = status;
    }

    public OtpResponse(String mobile, String masking, String status) {
        this.mobile = mobile;
        this.masking = masking;
        this.status = status;
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
