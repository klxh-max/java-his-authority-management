package orm;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SqlSession对象的小弟  (二弟)
 * 负责帮助SqlSession做一些琐事
 */
public class Handler {

    //insert into atm values(#{aname},#{apassword},#{abalance})
    //设计一个方法 负责解析带有#{key}形式的SQL
    //参数???     SQL
    //返回值???
    //  1.得到所有的key都按照顺序存起来   好多key  容器  集合ArrayList<String>
    //  2.原来那个样子的SQL    带问号   String
    //      对象{ArrayList,String}
    SQLAndKey parseSQL(String sql){
        //1.提供两个新的变量 用来分别存储解析后的两部分
        ArrayList<String> keyList = new ArrayList();
        StringBuilder newSQL = new StringBuilder();
        //2.解析
        //insert into atm values(#{aname},#{apassword},#{abalance} order by xxx)
        while(true) {
            //找寻#{ }的位置
            int left = sql.indexOf("#{");
            int right = sql.indexOf("}");
            //严谨的判断
            if (left != -1 && right != -1 && left < right) {
                //找到了一组#{} 截取中间的key
                newSQL.append(sql.substring(0, left));//前半部分 保留
                newSQL.append("?");
                keyList.add(sql.substring(left + 2, right));
            }else{//sql中已经没有成组的#{}啦
                newSQL.append(sql);
                break;
            }
            sql = sql.substring(right+1);
        }
        //将两个变量组合成一个对象 返回
        return new SQLAndKey(keyList,newSQL);
    }

    //=======================================================================================

    //一个小小弟 负责帮助下面那个handleParameter方法拼接map类型
    private void setMap(PreparedStatement pstat,Object obj,ArrayList<String> keyList) throws SQLException {
        Map map = (Map)obj;
        for(int i=0;i<keyList.size();i++){
            pstat.setObject(i+1,map.get(keyList.get(i)));
        }
    }
    //另一个小小弟 负责帮助下面那个handleParameter方法拼接对象
    private void setObject(PreparedStatement pstat,Object obj,ArrayList<String> keyList) throws SQLException {
        try {
            Class clazz = obj.getClass();//atm
            for (int i = 0; i < keyList.size(); i++) {
                //每次循环找到一个key
                String key = keyList.get(i);
                //通过key去obj对象中获取属性
                Field field = clazz.getDeclaredField(key);
                //直接操作私有属性
                field.setAccessible(true);
                //获取属性值
                Object value = field.get(obj);
                //交给pstat存入即可
                pstat.setObject(i+1,value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    //设计一个方法 负责将keyList中的所有key遍历 找到对象中的值 交给pstat对象拼接完整
    //参数???     pstat(干活),obj(值),keyList(所有的key和顺序)
    //没有返回值
    void handleParameter(PreparedStatement pstat,Object obj,ArrayList<String> keyList) throws SQLException {
        //获取obj对应的那个Class类
        Class clazz = obj.getClass();
        //  clazz通常应该是什么类型的
        //  就是基础类型  int--Integer  float--Float  double--Double  String
        //  就是domain类型  atm  student
        //  map--->容器  仅次于domain类型
        if(clazz==int.class || clazz==Integer.class){
            pstat.setInt(1,(Integer)obj);
        }else if(clazz==float.class || clazz==Float.class){
            pstat.setFloat(1,(Float)obj);
        }else if(clazz==double.class || clazz==Double.class){
            pstat.setDouble(1,(Double)obj);
        }else if(clazz==String.class){
            pstat.setString(1,(String)obj);
        }else if(clazz.isArray()){
            //自己玩去 我不支持
        }else{
            //剩下的只有两种可能
            if(obj instanceof Map){//1.map
                this.setMap(pstat,obj,keyList);
            }else{//2.domain
                this.setObject(pstat,obj,keyList);
            }
        }
    }

    //======================================================================================

    //一个小小弟 负责帮助下面那个handleResult方法将rs结果集的值取出来 组合成一个map对象
    private Map getMap(ResultSet rs) throws SQLException {
        //1.创建一个新的map对象
        Map result = new HashMap<String,Object>();
        //2.将结果集的所有信息取出来
        ResultSetMetaData rsmd = rs.getMetaData();
        //3.rsmd遍历
        for(int i=1;i<=rsmd.getColumnCount();i++){
            //获取结果集中查询到的列名
            String columnName = rsmd.getColumnName(i);
            //通过列名获取 列值
            Object value = rs.getObject(columnName);
            //存入map集合
            result.put(columnName,value);
        }
        return result;
    }
    //一个小小弟 负责帮助下面那个handleResult方法将rs结果集的值取出来 组合成一个domain对象
    private Object getObject(ResultSet rs,Class resultType) throws SQLException {
        Object obj = null;
        try {
            //通过反射创建一个对象 domain类型
            obj = resultType.newInstance();//调用了一个默认无参数的构造方法创建了一个对象 new
            //rs取值 存入obj中
            ResultSetMetaData rsmd = rs.getMetaData();
            //遍历结果集
            for(int i=1;i<=rsmd.getColumnCount();i++){
                //获取结果集中的每一个列名字
                String columnName = rsmd.getColumnName(i);
                //获取列名对应的值
                Object value = rs.getObject(columnName);
                //根据columnName反射找到obj中的某一个属性  操作属性将value存入
                Field field = resultType.getDeclaredField(columnName); //获得当前类自己的属性(4权限)
                //修改属性权限
                field.setAccessible(true);//其实应该找寻属性对应的set方法  invoke那个set方法
                //直接操作私有属性 赋值
                field.set(obj,value);
                //操作属性 给属性赋值
                //Atm atm = new Atm();    Atm atm = clazz.newInstance();
                //                        Field field = clazz.getField();  filed--->aname
                //atm.setAname("zzt");    field.set(atm,"zzt");
            }
            //通过底层创建对象 Inversion Of Control
            //对象是一个空对象没有值的 自动注入属性值 Dependency Injection
            //Spring    IOC控制反转+DI依赖注入   AOP面向切面编程 面向过程 面向对象 面向切面
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return obj;
    }
    //设计一个方法 负责将rs结果集的值取出来 存入一个新的容器中  基础类型 domain map
    //  参数  rs  class
    Object handleResult(ResultSet rs,Class resultType) throws SQLException {
        Object result = null;
        if(resultType==int.class || resultType==Integer.class){
            result = rs.getInt(1);
        }else if(resultType==long.class || resultType==Long.class){
            result = rs.getLong(1);
        }else if(resultType==float.class || resultType==Float.class){
            result = rs.getFloat(1);
        }else if(resultType==double.class || resultType==Double.class){
            result = rs.getDouble(1);
        }else if(resultType==String.class){
            result = rs.getString(1);
        }else{
            //觉得 map domain
            if(resultType==Map.class){//map
                result = this.getMap(rs);
            }else{//domain
                result = this.getObject(rs,resultType);
            }
        }
        return result;
    }

    //Object类型 对象 里面有一个属性 value=????
    //Object result = 对象;

}
