package orm;

import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Select;
import orm.annotation.Update;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个单独提出来的类
 * 就是原来的小弟方法
 * 升级为大哥啦  小弟方法是通用的 可以为任何一个DAO提供支持
 */
@SuppressWarnings("all")
public class SqlSession {//这个类的对象就是那小弟啦

    private Handler handler = new Handler();

    private String className = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/qxglo7?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC";
    private String user = "root";
    private String password = "cy414ljh212,,,";

    //发现 增删改方法 执行的都是数据库写操作 三个方法几乎一样
    //设计一个方法 可以执行任何一个表格的任何一条增删改
    //  参数?  String类型SQL   Object对象??????SQL上面的问号值
    //  返回值没有
    public void update(String sql,Object... objs){//新增 包装--类型--不通用
        try {
            //必然JDBC流程
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement pstat = connection.prepareStatement(sql);
            //将SQL和上面的那些问号值拼接完整
            //?????????  拼接  Object obj = new Atm(); 造型???
            for(int i=0;i<objs.length;i++){
                pstat.setObject(i+1,objs[i]);
            }
            //执行操作啦
            pstat.executeUpdate();
            pstat.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insert(String sql,Object... objs){
        this.update(sql,objs);
    }
    public void delete(String sql,Object... objs){
        this.update(sql,objs);
    }
    //小任务
    //思考 设计一个方法 可以执行任何一个表格的任何一个单条查询
    //  参数? SQL Object[]
    //  返回值--对象--通用  Object <T>
    //  方法的执行过程中  创建那个对象??
    public <T>  T selectOne(String sql,RowMapper rm,Object... objs){
        return (T)this.selectList(sql,rm,objs).get(0);
//        T obj = null;
//        try {
//            //必然JDBC流程
//            Class.forName(className);
//            Connection connection = DriverManager.getConnection(url,user,password);
//            PreparedStatement pstat = connection.prepareStatement(sql);
//            //将SQL和上面的那些问号值拼接完整
//            for(int i=0;i<objs.length;i++){
//                pstat.setObject(i+1,objs[i]);
//            }
//            //执行操作啦
//            ResultSet rs = pstat.executeQuery();
//            if(rs.next()){
//                //将结果集中的数据获取出来--->存入一个new新的容器中
//                //          Atm对象--类型不通用
//                //策略模式
//                //  1.类的方法  用来规定流程(接口 参数)
//                //  2.类的方法  提供策略
//                //obj = 方法{将rs的值取出,存入对象中,具体怎么做不确定,抽象 等待具体传入的策略决定};
//                obj = rm.mapperRow(rs);//小弟执行到这个地方----等待策略执行
//                //          集合---可以 哪一个类型?  Map<String,Object>
////                ResultSetMetaData rsmd = rs.getMetaData();
////                int count = rsmd.getColumnCount();//获取列的个数
////                for(int i=0;i<count;i++){//遍历每一个列
////                    String key = rsmd.getColumnName(i);
////                    Object value = rs.getObject(key);
////                    map.put(key,value);
////                }
//            }
//            rs.close();
//            pstat.close();
//            connection.close();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return obj;
    }
    //设计一个方法 可以查询任何一个表格的任何多条记录
    public <T>  List<T> selectList(String sql, RowMapper rm, Object... objs){
        List<T> list = new ArrayList<>();
        try {
            //必然JDBC流程
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement pstat = connection.prepareStatement(sql);
            //将SQL和上面的那些问号值拼接完整
            for(int i=0;i<objs.length;i++){
                pstat.setObject(i+1,objs[i]);
            }
            //执行操作啦
            ResultSet rs = pstat.executeQuery();
            while(rs.next()){
                //将结果集中的数据获取出来--->存入一个new新的容器中
                //          Atm对象--类型不通用
                //策略模式
                //  1.类的方法  用来规定流程(接口 参数)
                //  2.类的方法  提供策略
                //obj = 方法{将rs的值取出,存入对象中,具体怎么做不确定,抽象 等待具体传入的策略决定};
                //小弟执行到这个地方----等待策略执行
                T obj = rm.mapperRow(rs);
                list.add(obj);
            }
            rs.close();
            pstat.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //=========================================================================
    //1.参数中的这个Objcet... 原来是个对象--[] 可读性不好
    //2.用户在使用的时候 传递参数还需要自己来指定顺序  用起来不是很方便 很舒服
    //  将那个恶心的参数设计回对象形式  Object...     Atm
    //  如何去处理原来SQL上面的问号 与 Atm对象中的属性  一一对应的问题

    //设计一个方法 处理任何一个表格的任何一个增删改操作????
    //  参数  SQL SQL上面的那些值           atm
    public void update(String sql,Object obj){//obj 基础类型 map domain
        try {
            //0.SQL解析
            //      insert into atm values(#{aname},#{apassword},#{abalance})
            //      解析这里面的每一个#{key}
            //      将所有的key拿到 找寻obj对象中对应的属性值
            //      带有#{}这样的SQL 还原回带?的SQL 交给pstat对象 让他拼接
            SQLAndKey sqlAndKey = handler.parseSQL(sql);
            //1.加载驱动
            Class.forName(className);
            //2.获取连接
            Connection connection = DriverManager.getConnection(url, user, password);
            //3.创建状态参数(sql)
            PreparedStatement pstat = connection.prepareStatement(sqlAndKey.getSQL());
            //4.将SQL和那些值拼接在一起
            if(obj!=null){
                handler.handleParameter(pstat,obj,sqlAndKey.getKeyList());
            }
            //5.执行executeUpdate操作
            pstat.executeUpdate();
            //6.关闭资源
            pstat.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insert(String sql,Object obj){this.update(sql,obj);}
    public void delete(String sql,Object obj){this.update(sql,obj);}
    //  好处比之前的方式一 传递参数的时候变短啦 可读性更好啦
    //  用户需要遵循一个SQL的规则  将原来的? -> #{key}

    //设计一个方法 处理任何一个表格的任何一个查询单条
    //  参数  SQL SQL上面所需的那些值Object (基础类型 domain map)
    //  返回值  对象<T>
    public <T> T selectOne(String sql,Object obj,Class resultType){
        return (T)this.selectList(sql,obj,resultType).get(0);
    }
    public <T> T selectOne(String sql,Class resultType){
        return this.selectOne(sql,null,resultType);
    }
    public <T> List<T> selectList(String sql,Object obj,Class resultType){
        //1.先创建一个用来存储最终返回值的变量
        List<T> list = new ArrayList();
        try {
            //2.解析SQL
            SQLAndKey sqlAndKey = handler.parseSQL(sql);
            //3.加载驱动
            Class.forName(className);
            //4.获取连接
            Connection connection = DriverManager.getConnection(url, user, password);
            //5.创建状态参数(sql)
            PreparedStatement pstat = connection.prepareStatement(sqlAndKey.getSQL());
            //6.将SQL和那些值拼接在一起
            if(obj!=null){
                handler.handleParameter(pstat,obj,sqlAndKey.getKeyList());
            }
            //7.执行
            ResultSet rs = pstat.executeQuery();
            while(rs.next()){
                //8.将结果集中的数据取出---存入一个新的容器类
                list.add((T)handler.handleResult(rs,resultType));
            }
            //8.关闭
            rs.close();
            pstat.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //返回最终的对象
        return list;
    }
    public <T> List<T> selectList(String sql,Class resultType){
        return this.selectList(sql,null,resultType);
    }


    //===========================================================================================

    //  代理对象-----代替的是谁?
    //      AtmDao  某一个Dao对象
    //  代理对象-----代替那个人做什么?
    //      原来Dao方法里面自己该做的事情
    //      原来Dao方法里  让SqlSession去做事
    //  怎么创建代理对象
    //      代理对象 = Proxy.newProxyInstance();

    //  设计一个方法 创建一个代理对象 代理对象去代替原来的Dao做事(让SqlSession干活)
    //  参数      被代理的那个类是谁???    被代理的这个类需要是一个接口
    //  返回值    代理对象
//    public <T>T getMapper(Class clazz){// AtmDao.class
//        //想要通过下面方法创建一个代理对象
//        //需要给他提供三个参数
//        //1.类加载器ClassLoader
//        ClassLoader loader = clazz.getClassLoader();
//        //2.加载的类Class[] 通常数组中就只有一个类
//        Class[] interfaces = new Class[]{clazz};
//        //3.具体这个代理对象该做的事情InvocationHandler----具体做事的实现
//        InvocationHandler handler = new InvocationHandler(){
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                //这个方法是代理做的方法
//                //方法肯定是做原来被代理对象该做的那个事情
//                return null;
//            }
//        };
//        return (T)Proxy.newProxyInstance(loader,interfaces,handler);
//    }


    public <T> T getMapper(Class clazz){
        return(T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //这个方法就是代理对象具体该做的方法
                //代替  帮助原来的DAO做事情的
                //原来的DAO调用SqlSession对象去做事
                //proxy就是这个代理对象
                //method被代理的那个方法(要方法上面注解里的Sql)
                //args被代理的那个方法的参数(存储的是Sql上的那些所需的值)

                //1.获取被代理的那个方法上面的注解
                Annotation an = method.getAnnotations()[0];//an是一个注解对象
                //2.获取这个注解的类型----->决定一会儿找SqlSession的哪个方法
                Class type = an.annotationType();//@Insert @Delete
                //3.获取这个注解里面的SQL
                Method valueMethod = type.getDeclaredMethod("value");
                //4.执行注解对象里的那个value方法
                String sql = (String)valueMethod.invoke(an);
                //5.对args做一个简单的处理   基础类型 map domain 没有
                Object param = args==null?null:args[0];//判断的目的是找寻SqlSession方法时判断重载的(具体执行哪个方法)
                //6.根据type注解的类型判断该调用sqlSession中的哪个方法
                if(type == Insert.class){
                    SqlSession.this.insert(sql,param);
                }else if(type == Delete.class){
                    SqlSession.this.delete(sql,param);
                }else if(type == Update.class){
                    SqlSession.this.update(sql,param);
                }else if(type == Select.class){
                    //根据注解名字无法确定单条 多条
                    //解析method方法的返回值
                    //获取method返回值类型
                    Class methodReturnTypeClass = method.getReturnType();
                    if(methodReturnTypeClass==List.class){//多条
                        //解析methodReturnTypeClass里面的那个泛型类
                        //获取的是方法返回值的具体类型(java.util.List<domain.Atm>)
                        Type returnType = method.getGenericReturnType();
                        //将returnType还原成可以被操作的
                        ParameterizedTypeImpl realReturnType = (ParameterizedTypeImpl)returnType;
                        //操作返回值类型中的泛型类
                        Type[] patternTypes = realReturnType.getActualTypeArguments();//获取是泛型类
                        //获取数组中的第一个类domain.Atm
                        Type patternType = patternTypes[0];
                        //强制类型转换成Class
                        Class patternClass = (Class)patternType;
                        return SqlSession.this.selectList(sql,param,patternClass);
                    }else{//单条
                        return SqlSession.this.selectOne(sql,param,methodReturnTypeClass);
                    }
                }
                return null;
            }
        });
    }

}
