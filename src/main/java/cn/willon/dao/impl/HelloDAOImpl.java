package cn.willon.dao.impl;

import cn.willon.dao.interfaces.HelloDAO;
import webmvc.annotation.Component;

/**
 * HelloDAO实现类
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */

@Component(name = "helloDAO")
public class HelloDAOImpl implements HelloDAO {
    @Override
    public String hello(Integer id) {
        System.out.println("dao run ..");
        return "Hello " + id;
    }
}
