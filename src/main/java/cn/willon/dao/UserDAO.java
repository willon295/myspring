package cn.willon.dao;

import cn.willon.entity.User;

import java.util.List;

/**
 * UserDAO 接口
 *
 * @author willon
 * @version 1.0
 */
public interface UserDAO {


    /**
     * 添加用户
     *
     * @param user 用户信息
     */
    void add(User user);

    /**
     * 删除用户
     *
     * @param user 用户信息
     */
    void delete(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    void update(User user);

    /**
     * 通过用户id获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    User getById(Integer id);


    /**
     * 查询所有用户
     *
     * @return 所有用户
     */
    List<User> listUsers();
}
