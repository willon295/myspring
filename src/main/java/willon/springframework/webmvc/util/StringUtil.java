package willon.springframework.webmvc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By willon
 *
 * @author willon
 * @version 1.0
 */
public class StringUtil {
    public static Pattern compile = Pattern.compile("\\{(\\w+)}");

    public static String firstNameLower(String src) {
        char i = (char) (src.charAt(0) + 32);
        return src.replace(src.charAt(0), i);
    }

    public static List<String> findAllPlaceHolder(String uri) {
        ArrayList<String> strings = new ArrayList<>();
        Matcher matcher = compile.matcher(uri);
        while (matcher.find()) {
            strings.add(matcher.group(1));
        }
        return strings;
    }

    /**
     * 寻找 requestURI ,mappingURI 占位符对应关系 ，如
     * requestURI: /user/chen/23
     * mappingURI:  /user/{name}/{age}
     * <p>
     * map 返回的是  <name,chen>, <age,23>
     *
     * @param requestURI 请求 URI
     * @param mappingURI rest风格映射URI
     * @return <name,chen>, <age,23> 此类的 map
     */
    public static Map<String, String> getMapping(String requestURI, String mappingURI) {
        Map<String, String> map = new HashMap<>(8);
        String[] resplit = requestURI.split("/");
        String[] mapsplit = mappingURI.replaceAll("}", "").split("/");
        for (int i = 0; i < mapsplit.length; i++) {
            if (mapsplit[i].contains("{")) {
                map.put(mapsplit[i].replaceAll("\\{", ""), resplit[i]);
            }
        }
        return map;
    }

    public static <T> T castString2Target(String src, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<T> constructor = type.getConstructor(String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(src);


    }

}
