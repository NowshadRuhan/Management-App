package com.capsulestudio.schoolmanagement.Model;

/**
 * Created by Shuvo on 12/13/2017.
 */

public class Classes {

    private int id;
    private String class_name;
    protected String section;

    public Classes() {

    }

    public Classes(String class_name, String section) {
        this.class_name = class_name;
        this.section = section;
    }
    public Classes(int id, String class_name, String section) {
        this.id = id;
        this.class_name = class_name;
        this.section = section;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
