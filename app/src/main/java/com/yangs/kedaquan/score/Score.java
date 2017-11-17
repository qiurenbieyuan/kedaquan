package com.yangs.kedaquan.score;

/**
 * Created by yangs on 2017/8/1.
 */

public class Score {
    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getXf() {
        return xf;
    }

    public void setXf(String xf) {
        this.xf = xf;
    }

    public String getKs() {
        return ks;
    }

    public void setKs(String ks) {
        this.ks = ks;
    }

    public String getKhfx() {
        return khfx;
    }

    public void setKhfx(String khfx) {
        this.khfx = khfx;
    }

    public String getKcsx() {
        return kcsx;
    }

    public void setKcsx(String kcsx) {
        this.kcsx = kcsx;
    }

    public String getKcxz() {
        return kcxz;
    }

    public void setKcxz(String kcxz) {
        this.kcxz = kcxz;
    }

    private String cno;     //课程号
    private String name;    //课程名
    private String score;   //成绩
    private String xf;      //学分
    private String ks;      //课时
    private String khfx;      //考核方式
    private String kcsx;    //课程属性
    private String kcxz;    //课程性质

}
