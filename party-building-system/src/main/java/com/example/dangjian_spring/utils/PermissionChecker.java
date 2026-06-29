package com.example.dangjian_spring.utils;

import com.example.dangjian_spring.entity.User;

public class PermissionChecker {
    public static boolean hasPermission(User user, int permissionIndex) {
        if (user.getPermissions() == null || user.getPermissions().length() <= permissionIndex) {
            return false;
        }
        return user.getPermissions().charAt(permissionIndex) == '1';
    }
}