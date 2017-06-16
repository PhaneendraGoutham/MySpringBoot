package com.forsrc.boot.web.user.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.forsrc.core.web.security.MyUserDetails;
import com.forsrc.core.web.user.service.RoleService;
import com.forsrc.core.web.user.service.UserRoleService;
import com.forsrc.core.web.user.service.UserService;
import com.forsrc.pojo.Role;
import com.forsrc.pojo.User;
import com.forsrc.pojo.UserPrivacy;
import com.forsrc.pojo.UserRole;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;

    @RequestMapping(path = "/me", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Principal> user(Principal user) {
        if (!(user instanceof UsernamePasswordAuthenticationToken)) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) user;
        Object principal = upat.getPrincipal();
        if (principal instanceof MyUserDetails) {
            MyUserDetails myUserDetails = (MyUserDetails) principal;
            myUserDetails.getUserPrivacy().setPassword(myUserDetails.getPassword());
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/testuser/{id}", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<Void> get(@PathVariable("id") long id, UriComponentsBuilder ucBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(id).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<User> user(@PathVariable("id") long id) {
        User user = userService.get(id);
        if (user == null) {
            return new ResponseEntity<>(new User(), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{id}", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> userMap(@PathVariable("id") long id) {
        Map<String, Object> message = new HashMap<>();
        User user = userService.get(id);
        HttpStatus hsHttpStatus = HttpStatus.OK;
        message.put("data", user);
        if (user == null) {
            hsHttpStatus = HttpStatus.EXPECTATION_FAILED;
        }
        message.put("status", hsHttpStatus.value());
        message.put("reason", hsHttpStatus.getReasonPhrase());
        return new ResponseEntity<>(message, hsHttpStatus);
    }

    @RequestMapping(value = "/users", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<User>> users(@RequestParam("start") int start, @RequestParam("size") int size) {
        List<User> users = userService.get(start, size);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> list(@RequestParam("start") int start, @RequestParam("size") int size) {
        Map<String, Object> message = new HashMap<>();
        long count = userService.count();
        List<User> users = userService.get(start, size);
        message.put("total", count);
        message.put("start", start);
        message.put("size", size);
        message.put("data", users);
        message.put("status", HttpStatus.OK.value());
        message.put("reason", HttpStatus.OK.getReasonPhrase());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/testcache/{id}", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<User> testCache(@PathVariable("id") long id, UriComponentsBuilder ucBuilder) {
        User user = userService.get(id);
        if (user == null) {
            return new ResponseEntity<>(new User(), HttpStatus.EXPECTATION_FAILED);
        }
        userService.cacheClear();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/test/new", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<User> testNew(UriComponentsBuilder ucBuilder) {
        User user = new User();
        long time = System.currentTimeMillis();
        String username = "test/" + time;
        user.setUsername(username);
        user.setEmail(username + "@forsrc.com");
        userService.save(user, "123456".getBytes());
        Role role = new Role();
        role.setName("TEST_" + time);
        roleService.save(role);
        UserRole userRole = new UserRole();
        userRole.setRoleId(role.getId());
        userRole.setUserId(user.getId());
        userRoleService.save(userRole);
        user = userService.get(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}