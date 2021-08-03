package util;

import domain.Token;

import javax.servlet.ServletContext;
import java.util.Enumeration;

//扫描服务器过期cookie的线程
public class TokenScanThread extends Thread implements Runnable {
    private ServletContext application;
    public TokenScanThread(ServletContext application){
        this.application=application;
    }
    @Override
    public void run(){
        int second=0;
        while (true){
            try {
                Thread.sleep(1000);
                second+=1000;
                if(second>=20000){//每隔20秒扫描服务器
                    Enumeration<String> enums= application.getAttributeNames();
                    while (enums.hasMoreElements()){
                        String name=enums.nextElement();
                        Object value=application.getAttribute(name);
                        if(value instanceof Token){
                            //这是一个令牌对象
                            Token token=(Token) value;
                            if(token.getEnd()<System.currentTimeMillis()){//已经过期了
                                application.removeAttribute(name);
                            }
                        }
                    }
                    second=0;
                    continue;
                }
            }catch (InterruptedException  e){
                e.printStackTrace();
            }
        }
    }

}
