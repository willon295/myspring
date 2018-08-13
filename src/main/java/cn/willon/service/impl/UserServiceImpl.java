package cn.willon.service.impl;

import cn.willon.dao.UserDAO;
import cn.willon.entity.User;
import cn.willon.service.UserService;
import willon.springframework.webmvc.annotation.AutoWired;
import willon.springframework.webmvc.annotation.Service;

import java.util.List;

/**
 * UserService 实现类
 *
 * @author willon
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @AutoWired
    private UserDAO userDAO;

    @Override
    public void add(User user) {
        userDAO.add(user);
    }

    @Override
    public void delete(User user) {
        userDAO.delete(user);
    }

    @Override
    public void update(User user) {

        userDAO.update(user);
    }

    @Override
    public User getById(Integer id) {
        return userDAO.getById(id);
    }

    @Override
    public List<User> listUsers() {
        return userDAO.listUsers();
    }
}
