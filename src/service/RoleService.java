package service;

import dao.FunDao;
import dao.RoleDao;
import domain.LayPageInfo;
import domain.Role;
import orm.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleService {
    private RoleDao roleDao=new SqlSession().getMapper(RoleDao.class);
    private FunDao funDao=new SqlSession().getMapper(FunDao.class);

    public LayPageInfo findByPage(int page,int limit){
        //确保page下限
        page=Math.max(1,page);
        //确保page上限
        int count= roleDao.total();
        int max=(int)Math.ceil(1.0*count/limit);
        max=Math.max(1,max);//有可能为0
        page=Math.min(page, max);
        int start=(page-1)*limit;
        int length=limit;
        Map<String,Integer>param=new HashMap<>();
        param.put("start",start);
        param.put("length",length);
        List<Role> roleList= roleDao.findByPage(param);
        return new LayPageInfo(roleList,count,"",0) ;
    }

    public void update(Role role){
        roleDao.update(role);
    }

    //语雀5.1
    public List<Integer> findFidsByRole(int rid){
        return roleDao.findFidsByRole(rid);
    }

    //语雀5.1
    public void setFuns(int rid, String fids) {
        funDao.removeRelationship(rid);
        if (fids != null && !"".equals(fids)) {
            Map<String, Integer> param = new HashMap<>();
            param.put("rid", rid);
            String[] fid = fids.split(",");
            for (String f : fid) {
                param.put("fid", Integer.parseInt(f));
                funDao.addRelationship(param);
            }
        }
    }

}
