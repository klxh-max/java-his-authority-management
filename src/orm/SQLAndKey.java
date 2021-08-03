package orm;

import java.util.ArrayList;

/**
 * 这是一个我们自己规定的对象
 * 对象的目的是为了解析SQL后用来存储最终结果的
 * 应该有两个属性
 *  ArrayList 按顺序存放解析出来的全部key
 *  String    用来存放还原回去的那个原来的sql
 */
public class SQLAndKey {

    private ArrayList<String> keyList = new ArrayList();
    private StringBuilder sql = new StringBuilder();//拼接时候性能更好

    public SQLAndKey(ArrayList<String> keyList, StringBuilder sql) {
        this.keyList = keyList;
        this.sql = sql;
    }

    public ArrayList<String> getKeyList() {
        return keyList;
    }
    public String getSQL() {
        return sql.toString();
    }

}
