package roles;

import java.sql.Timestamp;

public class Log {
    private int number;
    private int userid;
    private int areaid;
    private String cardkey;
    private Timestamp time;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getAreaid() {
        return areaid;
    }

    public void setAreaid(int areaid) {
        this.areaid = areaid;
    }

    public String getCardkey() {
        return cardkey;
    }

    public void setCardkey(String cardkey) {
        this.cardkey = cardkey;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
