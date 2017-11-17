package com.yangs.kedaquan.bbs;

/**
 * Created by yangs on 2017/8/3.
 */

public class BBSDetail {
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String user;        //用户
    private String time;        //时间
    private String index;       //楼层
    private String content;     //内容

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    private String avatar;      //头像地址
    private String user_url;    //用户地址

    public String getReplay_me_url() {
        return replay_me_url;
    }

    public void setReplay_me_url(String replay_me_url) {
        this.replay_me_url = replay_me_url;
    }

    private String replay_me_url; //被回复地址
}
