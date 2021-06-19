package com.telogix.telogixcaptain.Enums;

public enum ResponseCode {


    ALLHAZARDS,
    ALLROUTES,
    ROUTEBYID,
    ALLCOMMODITES,
    ALLVEHICLES,
    ADDROUTE,
    EDITROUTE,
    ASSIGNLOAD,
    ALLDECANTING,
    ENDJOURNEY,
    SIGNOFF,
    ASSIGNROUTE,
    VIEWLOAD,
    EDITLOAD,
    DELETELOAD,
    TIMEOUTMARKED,
    TIMEINMARKED,
    STOPRIDE,
    SAVEDEVICE,
    DECANTEDVEHICLES,
    DECANTINGFORSIGNUP,
    RETAILERRATINGSAVED, HAULIERSLIST,
    PENDINGUSERS,
    USERAPPROVED;


    public static int getResponseCode(ResponseCode roleCode) {
        switch (roleCode) {
            case ALLHAZARDS:
                return 6002;
            case ROUTEBYID:
                return 1103;
            case ALLROUTES:
                return 1102;
            case ASSIGNROUTE:
                return 1201;
            case EDITROUTE:
                return 1104;
            case ADDROUTE:
                return 1101;
            case ENDJOURNEY:
                return 2201;
            case SIGNOFF:
                return 1;
            case STOPRIDE:
                return 1604;
            case VIEWLOAD:
                return 8003;
            case EDITLOAD:
                return 8004;
            case DELETELOAD:
                return 8009;
            case TIMEOUTMARKED:
                return 2402;
            case TIMEINMARKED:
                return 2401;
            case SAVEDEVICE:
                return 2701;
            case DECANTEDVEHICLES:
                return 2302;
            case RETAILERRATINGSAVED:
                return 2602;
            case DECANTINGFORSIGNUP:
                return 3005;
            case HAULIERSLIST:
                return 5005;
            case PENDINGUSERS:
                return 2003;
            case USERAPPROVED:
                return 2002;
        }

        return -1;
    }


}
