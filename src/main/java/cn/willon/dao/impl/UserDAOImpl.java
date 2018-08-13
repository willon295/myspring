package cn.willon.dao.impl;

import cn.willon.dao.UserDAO;
import cn.willon.entity.User;
import willon.springframework.webmvc.annotation.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO实现类， 用于返回数据
 *
 * @author willon
 * @version 1.0
 */

@Component
public class UserDAOImpl implements UserDAO {
    @Override
    public void add(User user) {
        System.out.println("add " + user.toString());
    }

    @Override
    public void delete(User user) {
        System.out.println("delete " + user.toString());
    }

    @Override
    public void update(User user) {
        System.out.println("update " + user.toString());
    }

    @Override
    public User getById(Integer id) {
        User user = new User();
        user.setId(id);
        user.setName("Willon");
        user.setPassword("123456");
        return user;
    }

    @Override
    public List<User> listUsers() {

        ArrayList<User> users = new ArrayList<>();
        User u1 = new User(1, "A", "aaa");
        User u2 = new User(2, "B", "bbb");
        User u3 = new User(3, "C", "ccc");
        User u4 = new User(4, "D", "ddd");
        User u5 = new User(5, "E", "eee");

        users.add(u1);
        users.add(u2);
        users.add(u3);
        users.add(u4);
        users.add(u5);

        return users;
    }
}
