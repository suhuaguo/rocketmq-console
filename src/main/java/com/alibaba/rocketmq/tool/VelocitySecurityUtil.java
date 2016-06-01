package com.alibaba.rocketmq.tool;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User:jiandan
 * Date:2016/1/14.
 * Time:16:29.
 * INFO: admin:管理员
 * guest:游客
 * <p/>
 * INFO:安全框架工具类
 */
public class VelocitySecurityUtil {

    public static String getPrincipal() {

        Object obj = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (obj instanceof UserDetails) {
            return ((UserDetails) obj).getUsername();
        } else {
            return "anonymous";
        }
    }

    // 判断是否授权
    public static boolean isAuthenticated() {

        return !getPrincipal().equals("anonymous");
    }

    // 判断是否管理员,如果是管理员，那么，返回 true 。否者，将返回 false
    public static boolean isAdminRole() {
        return getPrincipal().equals("admin");
    }

    public static boolean allGranted(String[] checkForAuths) {
        Set<String> userAuths = getUserAuthorities();
        for (String auth : checkForAuths) {
            if (userAuths.contains(auth))
                continue;
            return false;
        }
        return true;
    }

    public static boolean anyGranted(String[] checkForAuths) {
        Set<String> userAuths = getUserAuthorities();
        for (String auth : checkForAuths) {
            if (userAuths.contains(auth))
                return true;
        }
        return false;
    }

    public static boolean noneGranted(String[] checkForAuths) {
        Set<String> userAuths = getUserAuthorities();
        for (String auth : checkForAuths) {
            if (userAuths.contains(auth))
                return false;
        }
        return true;
    }

    // 判断用户角色  ROLE_ADMIN  ROLE_GUEST 等等角色。。。
    private static Set<String> getUserAuthorities() {
        Object obj = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Set<String> roles = new HashSet<String>();
        if (obj instanceof UserDetails) {
            @SuppressWarnings("unchecked")
            Collection<GrantedAuthority> gas = (Collection<GrantedAuthority>) ((UserDetails) obj)
                    .getAuthorities();
            for (GrantedAuthority ga : gas) {
                roles.add(ga.getAuthority());
            }
        }
        return roles;
    }


}
