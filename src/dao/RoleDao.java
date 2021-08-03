package dao;

import domain.Role;
import orm.SqlSession;
import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Select;
import orm.annotation.Update;

import java.util.List;
import java.util.Map;

public interface RoleDao {

    @Select("select count(*) from t_role")
    public int total();

    @Select("select * from t_role limit #{start},#{length}")
    public List<Role> findByPage(Map<String,Integer> param);

    @Update("update t_role set rname=#{rname} , rdescription=#{rdescription} where rid = #{rid}")
    public void update(Role role);

    @Select("select r.* , if(ur.rid is null,0,1) flag from t_role r left join " +
            "(select rid from t_user_role where uid = #{uid}) ur on r.rid = ur.rid")
    public List<Map> findAllByUser(int uid);

    @Delete("delete from t_user_role where uid=#{uid}")
    public void removeRelationship(int uid);

    @Insert("insert into t_user_role values(#{uid},#{rid})")
    public void addRelationship(Map<String,Integer> param);

    @Select("select fid from t_role_fun where rid = #{rid}")
    public List<Integer> findFidsByRole(int rid);//语雀5.1




}
