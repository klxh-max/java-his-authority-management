package controller;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@WebServlet("/checkcode")
public class CheckCodeController extends HttpServlet {

    private static final String source="0123456789ABCDEFG";//验证码来源
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code="";
        Random random=new Random();
        for(int i=0;i<4;i++){//产生4位随机验证码
            code+=source.charAt(random.nextInt(source.length()));
        }
        req.getSession().setAttribute("code",code);
        //使用GUI画一个验证码图片
        //一个jvm中的图片    File - 本地文件
        BufferedImage image = new BufferedImage( 80,30,BufferedImage.TYPE_INT_RGB );

        //画图笔
        Graphics g = image.createGraphics() ;

        //设置字体
        Font font = new Font("黑体", Font.BOLD,20);
        g.setFont(font);
        g.setColor(new Color(250,0,0));

        //设置背景
        g.fillRect(0,0,80,30);
        g.setColor(new Color(255,255,0));

        //写入内容
        g.drawString(code,10,20);

        //将jvm中的图片写到指定位置
        //FileOutputStream , 将图片写入本地
        //resp.getOutputStream 将图片写给浏览器
        ImageIO.write(image,"jpg",resp.getOutputStream());

    }
}
