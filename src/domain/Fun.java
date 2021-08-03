package domain;

public class Fun {
    private Integer fid;
    private String fname;
    private Integer ftype;//1菜单 2按钮
    private String fhref;//菜单对应的请求url
    private Integer pid;//父级功能编号，根菜单的父级编号为-1
    private String auth;//权限范围
    private String yl1;
    private String yl2;

    public Fun() {}

    public Fun(Integer fid, String fname, Integer ftype, String fhref, Integer pid, String auth, String yl1, String yl2) {
        this.fid = fid;
        this.fname = fname;
        this.ftype = ftype;
        this.fhref = fhref;
        this.pid = pid;
        this.auth = auth;
        this.yl1 = yl1;
        this.yl2 = yl2;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public Integer getFtype() {
        return ftype;
    }

    public void setFtype(Integer ftype) {
        this.ftype = ftype;
    }

    public String getFhref() {
        return fhref;
    }

    public void setFhref(String fhref) {
        this.fhref = fhref;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getYl1() {
        return yl1;
    }

    public void setYl1(String yl1) {
        this.yl1 = yl1;
    }

    public String getYl2() {
        return yl2;
    }

    public void setYl2(String yl2) {
        this.yl2 = yl2;
    }
}
