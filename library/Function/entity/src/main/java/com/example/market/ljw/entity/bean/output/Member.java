package com.example.market.ljw.entity.bean.output;


import java.io.Serializable;

/**
 * Created by GYH on 2014/10/22.
 */
public class Member implements Serializable{
    private long MemberID;
    private long ID; //会员ID
    private String LoginName;//登录账号
    private String TrueName; //姓名
    private long Score; //积分
    private long TodayScore; //当天积分
    private long HistoryScore; //历史积分
    private String MemberShopUrl; //商城地址
    private long Duration; //当天在线时长
    private long HourScore; //每小时积分
    private String ClientSubmitInterval; //提交信息间隔时间
    private String AdvertisementUrl; // 广告地址
    private String MemberTypeCode; //会员类型
    private String ServerTime;//

    public String getServerTime() {
        return ServerTime;
    }

    public void setServerTime(String serverTime) {
        ServerTime = serverTime;
    }

    public long getMemberID() {
        return MemberID;
    }

    public void setMemberID(long memberID) {
        MemberID = memberID;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getTrueName() {
        return TrueName;
    }

    public void setTrueName(String trueName) {
        TrueName = trueName;
    }

    public long getScore() {
        return Score;
    }

    public void setScore(long score) {
        Score = score;
    }

    public long getTodayScore() {
        return TodayScore;
    }

    public void setTodayScore(long todayScore) {
        TodayScore = todayScore;
    }

    public long getHistoryScore() {
        return HistoryScore;
    }

    public void setHistoryScore(long historyScore) {
        HistoryScore = historyScore;
    }

    public String getMemberShopUrl() {
        return MemberShopUrl;
    }

    public void setMemberShopUrl(String memberShopUrl) {
        MemberShopUrl = memberShopUrl;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public long getHourScore() {
        return HourScore;
    }

    public void setHourScore(long hourScore) {
        HourScore = hourScore;
    }

    public String getClientSubmitInterval() {
        return ClientSubmitInterval;
    }

    public void setClientSubmitInterval(String clientSubmitInterval) {
        ClientSubmitInterval = clientSubmitInterval;
    }

    public String getAdvertisementUrl() {
        return AdvertisementUrl;
    }

    public void setAdvertisementUrl(String advertisementUrl) {
        AdvertisementUrl = advertisementUrl;
    }

    public String getMemberTypeCode() {
        return MemberTypeCode;
    }

    public void setMemberTypeCode(String memberTypeCode) {
        MemberTypeCode = memberTypeCode;
    }
}
