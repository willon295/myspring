package cn.willon.controller;

import cn.willon.dto.RestResponse;
import cn.willon.entity.User;
import cn.willon.service.UserService;
import lombok.extern.slf4j.Slf4j;
import willon.springframework.boot.databind.PathVariable;
import willon.springframework.webmvc.annotation.*;

import java.util.List;

/**
 * Test Controller
 *
 * @author willon
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @AutoWired
    private UserService userService;


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RestResponse add(@RequestBody User user) {
        userService.add(user);
        RestResponse response = new RestResponse();
        response.setStatus("200");
        response.setData("add success " + user.toString());
        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResponse get(@PathVariable(name = "id") Integer id) {
        User byId = userService.getById(id);
        RestResponse response = new RestResponse();
        response.setStatus("200");
        response.setData(byId);
        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public RestResponse delete(@PathVariable(name = "id") Integer id) {
        User user = new User();
        user.setId(id);
        userService.delete(user);
        RestResponse response = new RestResponse();
        response.setStatus("200");
        response.setData("delete success" + user.toString());
        return response;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public RestResponse update(@RequestBody User user) {
        userService.update(user);
        RestResponse response = new RestResponse();
        response.setStatus("200");
        response.setData("update success " + user.toString());
        return response;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public RestResponse list() {

        log.info(String.valueOf(userService));
        List<User> users = userService.listUsers();
        RestResponse response = new RestResponse();
        response.setStatus("200");
        response.setData(users);
        return response;
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public RestResponse login(@RequestBody User user) {
        RestResponse response = new RestResponse();
        response.setStatus("200");
        response.setData(user);
        return response;
    }

}
