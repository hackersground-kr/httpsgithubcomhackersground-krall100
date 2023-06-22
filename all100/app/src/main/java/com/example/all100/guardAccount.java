package com.example.all100;

import java.util.ArrayList;

//보호자
public class guardAccount {

    private String idToken; //파이어베이스 고유 토큰 정보
    private String name;
    private String address;
    private String birth;
    private String phoneNumber;
    private ArrayList<String> sickList;            //보호대상자 UID 배열

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

    public ArrayList<String> getSickList() {
        return sickList;
    }

    public void setSickList(ArrayList<String> sickList) {
        this.sickList = sickList;
    }

}