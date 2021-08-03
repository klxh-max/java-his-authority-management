package orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 *      用注解来充当一个搬运工的作用
 *      帮你将数据 信息搬运给另一个人(解析注解那个人)
 *
 * 如果注解类型不熟悉
 * 可以类比着接口的结构来学习(记忆)
 *      属性  public static final
 *      方法  public abstract 方法();
 *
 * 注解定义完之后 需要"大佬"来认证   元注解(为了描述注解的 元注解本身不做事)
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)// .java源文件  .class字节码文件  内存中的Class类映射
public @interface Insert {
    String value();
}
