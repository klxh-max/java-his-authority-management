package domain;

public class User {
    private Integer uid;
    private String uname;
    private String upass;
    private String yl1;
    private String yl2;

    public User() {}

    public User(Integer uid, String uname, String upass, String yl1, String yl2) {
        this.uid = uid;
        this.uname = uname;
        this.upass = upass;
        this.yl1 = yl1;
        this.yl2 = yl2;
    }

    public Integer getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getUpass() {
        return upass;
    }

    public String getYl1() {
        return yl1;
    }

    public String getYl2() {
        return yl2;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    public void setYl1(String yl1) {
        this.yl1 = yl1;
    }

    public void setYl2(String yl2) {
        this.yl2 = yl2;
    }
}
