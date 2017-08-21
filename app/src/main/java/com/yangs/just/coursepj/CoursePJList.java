package com.yangs.just.coursepj;

/**
 * Created by yangs on 2017/7/30.
 */

public class CoursePJList {
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }


    private String teacher;
    private String score;


    public Boolean getHasPj() {
        return hasPj;
    }

    public void setHasPj(Boolean hasPj) {
        this.hasPj = hasPj;
    }

    private Boolean hasPj;
}
