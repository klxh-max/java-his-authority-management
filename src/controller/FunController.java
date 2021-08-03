package controller;

import annotation.Auth;
import domain.Fun;
import exception.FnameException;
import myweb.annotation.Controller;
import myweb.annotation.RequestMapping;
import myweb.annotation.ResponseBody;
import service.FunService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FunController {
    private FunService service=new FunService();

    @RequestMapping("/funAll")
    @ResponseBody
    @Auth("com.qxgl.auth.fun")
    public Map<String,Object> findAll(){
        List<Fun> funs=service.findAll();
        //将查询结果按照layui-treetable组件的规范组装并响应
        Map<String,Object> map=new HashMap<>();
        map.put("code",0);
        map.put("msg","出错啦");
        map.put("data",funs);
        return map;
    }

    @RequestMapping("/funAdd")
    public String funAdd(Fun fun){
            service.save(fun);
            return "fun.jsp";
    }
}
