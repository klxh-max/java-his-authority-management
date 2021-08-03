package dao;

import domain.User;
import orm.SqlSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {
    String className = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/qxglo7";
    String password = "cy414ljh212,,,";

    public User selectOne(String uname) {
        User user = null;
        try {
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url, "root", password);
            String sql = "SELECT UID,UNAME,UPASS,YL1,YL2 FROM T_USER WHERE UNAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uname);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setUid(resultSet.getInt("uid"));
                user.setUname(resultSet.getString("uname"));
                user.setUpass(resultSet.getString("upass"));
                user.setYl1(resultSet.getString("yl1"));
                user.setYl2(resultSet.getString("yl2"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


    public int total() {
        String sql = "SELECT COUNT(*) FROM T_USER";
        SqlSession session = new SqlSession();
        return session.selectOne(sql, int.class);//查询一条语句，返回值为int
    }

    public List<User> findUserByPage(int start, int length) {
        String sql = "SELECT * FROM T_USER LIMIT #{start} , #{length}";
        Map<String, Integer> param = new HashMap<>();
        param.put("start", start);
        param.put("length", length);
        SqlSession session = new SqlSession();
        return session.selectList(sql, param, User.class);
    }

    public void saveUsers(User user) {
        String sql = "insert into t_user values (null,#{uname},#{upass},#{yl1},#{yl2})";
        SqlSession session = new SqlSession();
        session.insert(sql, user);
    }

    public Long assertUser(int uid){
        String sql = "select count(*) from t_user where uid = #{uid}" ;
        SqlSession session = new SqlSession();
        return session.selectOne(sql,uid,Long.class);
    }
}
