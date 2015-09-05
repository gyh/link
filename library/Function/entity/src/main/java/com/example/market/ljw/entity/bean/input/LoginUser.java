package com.example.market.ljw.entity.bean.input;


/**
 * Created by GYH on 2014/10/22.
 */
public class LoginUser {

    private String AdvertisementTypeCode;
    private String LoginName;
    private String Password;
    private String ClientType;
    private String tooken;
    private long Duration;
    private String MemberID;
    private String Version;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getClientType() {
        return ClientType;
    }

    public void setClientType(String clientType) {
        ClientType = clientType;
    }

    public String getTooken() {
        return tooken;
    }

    public void setTooken(String tooken) {
        this.tooken = tooken;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public String getMemberID() {
        return MemberID;
    }

    public void setMemberID(String memberID) {
        MemberID = memberID;
    }

    public String getAdvertisementTypeCode() {
        return AdvertisementTypeCode;
    }

    public void setAdvertisementTypeCode(String advertisementTypeCode) {
        AdvertisementTypeCode = advertisementTypeCode;
    }
}
