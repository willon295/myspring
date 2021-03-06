package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 自动装配注解
 * 默认装配的实例 为 属性全名首字母小写
 *
 * @author willon
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWired {

}
