package domain;


//自动登录的令牌验证信息
public class Token {
    private String token;//uuid
    private User user;
    private String ip;
    private long start;//生效时间戳，单位为毫秒
    private long end;//失效时间戳

    public Token() {}

    public Token(String token, User user, String ip, long start, long end) {
        this.token = token;
        this.user = user;
        this.ip = ip;
        this.start = start;
        this.end = end;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public String getIp() {
        return ip;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
