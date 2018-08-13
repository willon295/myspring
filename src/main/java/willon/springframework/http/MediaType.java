package willon.springframework.http;

/**
 * Content-type
 *
 * @author willon
 * @version 1.0
 */
public class MediaType {

    private String name;
    private String value;

    public static MediaType ALL=new MediaType();
    public static MediaType APPLICATION_JSON =new MediaType();
    public static MediaType TEXT_PLAIN =new MediaType();
    public static MediaType MULTIPART_FORM_DATA =new MediaType();

    static {
        ALL.name = "*/*";
        ALL.value = "*/*";
        APPLICATION_JSON.name = "application/json";
        APPLICATION_JSON.value = "application/json;charset=UTF-8";
        TEXT_PLAIN.name = "text/plain";
        TEXT_PLAIN.value = "text/plain;charset=UTF-8";
        MULTIPART_FORM_DATA.name = "multipart/form-data";
        MULTIPART_FORM_DATA.value = "multipart/form-data;charset=UTF-8";
    }

    public String name() {
        return name;
    }


    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "MediaType{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
