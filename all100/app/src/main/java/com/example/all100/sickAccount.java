package com.example.all100;

import java.util.ArrayList;

public class sickAccount {

    private String idToken; //파이어베이스 고유 토큰 정보
    private String name;
    private String address;
    private String birth;
    private String phoneNumber;
    private ArrayList<String> guardList;            //보호대상자 UID 배열
    private ArrayList<String> regionList;
    private ArrayList<String> chatList;
    private boolean onoff; //on=true, off=false

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getGuardList() {
        return guardList;
    }

    public void setGuardList(ArrayList<String> guardList) {
        this.guardList = guardList;
    }

    public ArrayList<String> getRegionList() {
        return regionList;
    }

    public void setRegionList(ArrayList<String> regionList) {
        this.regionList = regionList;
    }

    public ArrayList<String> getChatList() {
        return chatList;
    }

    public void setChatList(ArrayList<String> chatList) {
        this.chatList = chatList;
    }

    public boolean isOnoff() {
        return onoff;
    }

    public void setOnoff(boolean onoff) {
        this.onoff = onoff;
    }
}