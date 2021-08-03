package orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * row行
 * mapper mapping 映射关系   一行记录 数据库  <---> 一个对象domain
 *
 * 接口的目的是为了定义规则(定义策略)
 */
public interface RowMapper {

    //设计一个方法
    //将结果集的值取出来 存入一个对象中
    public abstract <T>T mapperRow(ResultSet rs) throws SQLException;

}
