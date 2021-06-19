package com.telogix.telogixcaptain.Enums;

public enum UserRole {

    SUPER_ADMIN,
    MANAGER,
    HAULIER,
    JM,
    SCHEDULER,
    PRELOAD_INSPECTOR,
    DRIVER,
    SUPERVISOR,
    RETAILER,
    ;


    public static UserRole getRole(String roleCodeStr) {
        int roleCode = Integer.parseInt(roleCodeStr);
        switch (roleCode) {
            case 1001:
                return SUPER_ADMIN;
            case 1002:
                return MANAGER;
            case 1003:
                return HAULIER;
            case 1004:
                return JM;
            case 1005:
                return SCHEDULER;
            case 1006:
                return PRELOAD_INSPECTOR;
            case 1007:
                return DRIVER;
            case 1008:
                return SUPERVISOR;
            case 1009:
                return RETAILER;
        }

        return null;
    }


}
