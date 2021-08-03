package myweb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelAndView {
    private String viewName;//05.jsp
    private Map<String,Object> values=new HashMap<>();

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public void addObject(String key,Object value){
        values.put(key,value);
    }

    public Object getObject(String key){
        return values.get(key);
    }

    public Set<String> getObjectNames(){
        return values.keySet();
    }
}