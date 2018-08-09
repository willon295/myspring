package cn.willon.service.impl;

import cn.willon.dao.interfaces.HelloDAO;
import cn.willon.service.interfaces.HelloService;
import webmvc.annotation.Resource;
import webmvc.annotation.Service;

/**
 * HelloService实现类
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */

@Service(name = "helloService")
public class HelloServiceImpl implements HelloService {

    @Resource(name = "helloDAO")
    private HelloDAO helloDAO;

    @Override
    public String hello(Integer id) {
        return helloDAO.hello(id);
    }

    @Override
    public String getName(String name) {
        return "Hello " + name;
    }
}
