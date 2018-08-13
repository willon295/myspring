package willon.springframework.webmvc.bean;

import lombok.Data;

/**
 * 方法参数类
 *
 * @author willon
 * @version 1.0
 */
@Data
public class Arg {
    /**
     * 参数的位置
     */
    private Integer index;

    /**
     * 参数的类型
     */
    private Class type;
}
