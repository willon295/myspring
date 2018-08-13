package cn.willon.conf;

import cn.willon.entity.User;
import willon.springframework.boot.databind.Bean;
import willon.springframework.boot.databind.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring   @Configuration  @Bean 测试用
 *
 * @author willon
 * @version 1.0
 */
@Configuration
public class Config {

    @Bean(name = "testUser")
    public User user() {
        return new User(888, "Willon", "123456");
    }


    @Bean(name = "users")
    public List<User> users() {
        ArrayList<User> users = new ArrayList<>();
        User u1 = new User(1001, "Jack", "jack");
        User u2 = new User(1002, "Tom", "tom");
        users.add(u1);
        users.add(u2);

        return users;

    }
}
