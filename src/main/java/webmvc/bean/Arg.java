package webmvc.bean;

/**
 * 参数实体类
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-9
 */
public class Arg {

    /**
     * 参数在的方法中的位置
     */
    private Integer index;

    /**
     * 参数的类型
     */
    private Class<?> type;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Arg(Integer index, Class<?> type) {
        this.index = index;
        this.type = type;
    }

    public Arg() {
    }
}
