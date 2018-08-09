package cn.willon.controller;

import cn.willon.service.interfaces.HelloService;
import webmvc.annotation.*;

/**
 * 测试controller
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */

@Controller
@RequestMapping(value = "/user")
public class HelloController {

    @AutoWired
    private HelloService helloService;

    @RequestMapping(value = "/h1")
    public String hello(@RequestParam(name = "id") Integer id) {
        System.out.println("h1 run  ... ");
        return helloService.hello(id);
    }

    @RequestMapping(value = "/h2")
    public String hello2(@RequestParam(name = "id") Integer id, @RequestParam(name = "name") String name) {

        System.out.println("h2 run ... ");
        return helloService.hello(id);
    }


    @RequestMapping(value = "/h3")
    public String getName(@RequestParam(name = "name") String name) {
        System.out.println("h3 run ..");
        return helloService.getName(name);
    }

}
