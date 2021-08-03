package myweb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 存储拦截器相关信息
 */
public class InterceptorInfo {
    private Object interceptor;
    private Set<String> includeUrl=new HashSet<>();
    private Set<String> excludeUrl=new HashSet<>();

    public InterceptorInfo() {}

    public InterceptorInfo(Object interceptor, Set<String> includeUrl, Set<String> excludeUrl) {
        this.interceptor = interceptor;
        this.includeUrl = includeUrl;
        this.excludeUrl = excludeUrl;
    }

    public Object getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Object interceptor) {
        this.interceptor = interceptor;
    }

    public Set<String> getIncludeUrl() {
        return includeUrl;
    }

    public void setIncludeUrl(String includeUrl) {
        if(includeUrl!=null&&!"".equals(includeUrl)){
            String[] ss=includeUrl.split(",");
            List<String> list= Arrays.asList(ss);
            this.includeUrl.addAll(list);
        }
    }

    public Set<String> getExcludeUrl() {
        return excludeUrl;
    }

    public void setExcludeUrl(String excludeUrl) {
        if(excludeUrl!=null&&!"".equals(excludeUrl)){
            String[] ss=excludeUrl.split(",");
            List<String> list= Arrays.asList(ss);
            this.excludeUrl.addAll(list);
        }
    }
}
