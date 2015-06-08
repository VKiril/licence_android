package com.test.asus.bluetoothtestapp.Entities;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by asus on 08.06.2015.
 */
public class User extends SugarRecord<User> {

    private String name = "";
    private String surname = "";
    private String email = "";
    private String birthDay = "";
    private String nickName = "";
    private String body_mass = "";
    private String height = "" ;
    private String gender = "";

    public User(Context context) {
        super(context);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBody_mass() {
        return body_mass;
    }

    public void setBody_mass(String body_mass) {
        this.body_mass = body_mass;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String password = "";


}
