package com.project.six.treasurehunt;

/**
 * Created by gunhe on 2017-11-24.
 */

public class userInfo {
    public String uid;
    public String userName;
    public String email;
    public long FindTreasure;
    public long HideTreasure;
    public userInfo(String userName,String email){
        this.userName=userName;
        this.email=email;
    }
}
