package com.mailmak.time_registration_system.classes;

import lombok.Getter;

@Getter
public enum Role {
    NONE(0),
    EMPLOYEE(1),
    MANAGER(1 << 1);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromInt(int value) {
        for (Role role : Role.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        return null;
    }

    public static boolean hasRole(Role userRole, Role... requiredRoles) {
        int requiredRolesValue = 0;
        for (Role role : requiredRoles) {
            requiredRolesValue |= role.getValue();
        }

        return (requiredRolesValue & userRole.getValue()) != 0;
    }
}