package com.capsulestudio.schoolmanagement.Model;

/**
 * Created by Shuvo on 2/28/2018.
 */

public class Subjects {

    private int id;
    private String sub_name;

    public Subjects() {
    }

    public Subjects(int id, String sub_name) {
        this.id = id;
        this.sub_name = sub_name;
    }

    public Subjects(String sub_name) {
        this.sub_name = sub_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }
}
