package controller;

import myweb.ModelAndView;
import myweb.annotation.Controller;
import myweb.annotation.RequestMapping;
import myweb.annotation.RequestParam;
import myweb.annotation.ResponseBody;
import service.UserService;
import vo.FunVO;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private UserService userService=new UserService();

    @RequestMapping("/toSetRole")
    public ModelAndView findRole(@RequestParam("uid") int uid){
        //此次请求需要一个设置角色网页模板
        //在模板中需要展示所有角色列表（携带角色信息）
        //在模板中需要默认勾选上一次分配的角色（携带上一次的角色信息）
        List<Map> setRoleInfoList= userService.findRoleAllByUser(uid);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("setRoleInfoList",setRoleInfoList);
        modelAndView.setViewName("setRole.jsp");
        return modelAndView;
    }

    @RequestMapping("/setRolesForUser")
    public void setRolesForUser(@RequestParam("uid") int uid,@RequestParam("rids") String rids){
        userService.setRolesForUser(uid, rids);
    }

    @RequestMapping("/findUserMenus")
    @ResponseBody
    public List<FunVO> findUserMenus(HttpSession session){
        Map<String,Object> map=(Map<String, Object>) session.getAttribute("loginAuth");
        return (List<FunVO>) map.get("menus");
    }

}
