package dao;

import domain.Fun;
import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Select;

import java.util.List;
import java.util.Map;

public interface FunDao {
    @Select("select * from t_fun")
    public List<Fun> findAll();

    @Select("select count(*) from t_fun where fname = #{fname}")
    public int fnameAssert(String fname);

    @Insert("insert into t_fun values(null,#{fname},#{ftype},#{fhref},#{pid},#{auth},#{yl1},#{yl2})")
    public void save(Fun fun);

    @Delete("delete from t_role_fun where rid=#{rid}")
    public void removeRelationship(int rid);//语雀5.1

    @Insert("insert into t_role_fun values(#{rid},#{fid})")
    public void addRelationship(Map<String,Integer> param);//语雀5.1

    @Select("select * from t_fun where fid in" +
            " (select fid from t_role_fun where rid in" +
            " (select rid from t_user_role where uid = #{uid}))")
    public List<Fun> findFunByUser(int uid);//语雀6.1
}
