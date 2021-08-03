package util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerStartListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce){
        new TokenScanThread(sce.getServletContext()).start();
    }
}
