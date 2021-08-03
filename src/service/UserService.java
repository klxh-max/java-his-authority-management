package service;

import dao.FunDao;
import dao.RoleDao;
import dao.UserDao;
import domain.Fun;
import domain.PageInfo;
import domain.User;
import myweb.annotation.RequestParam;
import orm.SqlSession;
import vo.FunVO;

import java.util.*;

public class UserService {
    private UserDao userDao = new UserDao();
    RoleDao roleDao = new SqlSession().getMapper(RoleDao.class);
    FunDao funDao = new SqlSession().getMapper(FunDao.class);

    public User checkLogin(String uname, String upass) {
        User user = userDao.selectOne(uname);
        if (user != null && user.getUpass().equals(upass)) {
            return user;
        }
        User user1 = null;
        return user1;
    }

    public PageInfo findUserByPage(int page, int rows) {
        if (page <= 0) {
            page = 1;
        }
        int total = userDao.total();
        int max = total % rows == 0 ? (total / rows) : (total / rows + 1);
        max = max == 0 ? 1 : max;
        if (page > max) {
            page = max;
        }
        int start = (page - 1) * rows;
        List<User> users = userDao.findUserByPage(start, rows);
        return new PageInfo(page, rows, total, max, start, (start + rows - 1), users);
    }

    public void saveUsers(User user) {
        userDao.saveUsers(user);
    }

    /*
    查询所有角色信息（List），uid这个用户上一次分配的信息（List）
    角色信息携带到网页后，需要展示，需要所有信息（List<Role>）
    上一次分配的角色信息携带到网页后，需要根据信息默认勾选（List<rid>）
    将上述所需要的信息整合在一起，List中每一条记录都包含既有角色信息+该角色是否勾选信息
     */
    public List<Map> findRoleAllByUser(int uid) {
        return roleDao.findAllByUser(uid);
    }

    public void setRolesForUser(int uid, String rids) {
        long count = userDao.assertUser(uid);
        if (count == 0) {
            //无效账号
            throw new RuntimeException("无效操作！");
        }
        roleDao.removeRelationship(uid);
        if (rids != null && !"".equals(rids)) {
            Map<String, Integer> param = new HashMap<>();
            param.put("uid", uid);
            String[] rid = rids.split(",");
            for (String r : rid) {
                param.put("rid", Integer.parseInt(r));
                roleDao.addRelationship(param);
            }
        }
    }

    public Map<String, Object> findUserAuth(int uid) {//语雀6.1
        List<Fun> funs = funDao.findFunByUser(uid);

        //funs中装载着所有的权限功能，我们想要的，一个是只装有权限范围的列表
        //一个是只装有菜单的列表（使用layui-tree组件展示，要求子父功能是包含关系）List<Fun>

        //只装有权限范围的列表
        Set<String> auths = new HashSet<>();
        for (Fun fun : funs) {
            auths.add(fun.getAuth());
        }

        //只装有菜单的列表List<Fun>->List<FunVo>
        List<FunVO> menus = reload(funs, -1);
        Map<String, Object> map = new HashMap<>();
        map.put("auths", auths);
        map.put("menus", menus);
        return map;
    }

    //在所有的功能菜单中，逐层组装，理论上从第一层    语雀6.1
    private List<FunVO> reload(List<Fun> funs, int level) {
        List<FunVO> vos = new ArrayList<>();//装载本层组装后的菜单
        for (Fun fun : funs) {
            if (fun.getFtype().equals(1) && fun.getPid().equals(level)) {
                //找到了一个属于当前层级的菜单
                //要找到当前菜单的子级，当前菜单的fid就是子级菜单的pid
                String title = "";
                if (fun.getFhref() != null && !"".equals(fun.getFhref())) {
                    //当前菜单点击后需要发送请求
                    title = "<a href='" + fun.getFhref() + "' target='content'>" + fun.getFname() + "</a>";
                } else {
                    title = fun.getFname();
                }
                FunVO funVO = new FunVO(title, fun.getFid(), reload(funs, fun.getFid()));
                vos.add(funVO);
            }
        }
        return vos;
    }


}
