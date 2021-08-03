package controller;

import domain.LayPageInfo;
import domain.Role;
import myweb.annotation.Controller;
import myweb.annotation.RequestMapping;
import myweb.annotation.RequestParam;
import myweb.annotation.ResponseBody;
import service.RoleService;

import java.util.List;

@Controller
public class RoleController {

    private RoleService roleService=new RoleService();

    @RequestMapping("/roleFindAll")
    @ResponseBody
    public LayPageInfo findAll(@RequestParam("page") int page, @RequestParam("limit") int limit){
        return roleService.findByPage(page, limit);
    }

    @RequestMapping("/roleUpdate")
    public String update(Role role){
        roleService.update(role);
        return "role.jsp";
    }

    @RequestMapping("/findFidsByRole")
    @ResponseBody
    public List<Integer> findFidsByRole(@RequestParam("rid") int rid){//语雀5.1
        return roleService.findFidsByRole(rid);
    }

    @RequestMapping("/setF")
    public void setFuns(@RequestParam("rid") int rid,@RequestParam("fids") String fids){//语雀5.1
        System.out.println("setFuns123");
        roleService.setFuns(rid, fids);
    }
}
