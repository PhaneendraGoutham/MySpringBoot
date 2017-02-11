package com.forsrc.boot.web.security;

import com.forsrc.pojo.Role;
import com.forsrc.pojo.UserPrivacy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPrivacy user = securityService.findByUsername(username);
        if (user == null) {
            System.out.println(String.format("--> MyUserDetailsService.loadUserByUsername() --> User is not exist: %s", username));
            throw new UsernameNotFoundException(String.format("User is not exist: %s", username));
        }
        System.out.println(String.format("--> MyUserDetailsService.loadUserByUsername() --> User is : %s", username));
        Map<Long, Role> roles = getRoles();
        MyUserDetails myUserDetails = new MyUserDetails(securityService, user, roles);
        return myUserDetails;
    }

    private Map<Long, Role> getRoles() {
        Map<Long, Role> map = new HashMap<>();
        List<Role> roles = securityService.getRoles();
        for (Role role : roles) {
            map.put(role.getId(), role);
        }
        return map;
    }
}
