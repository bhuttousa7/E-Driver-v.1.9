package com.telogix.telogixcaptain.Utils;

/**
 * Created by TALHA on 19-Dec-16.
 */

public class connection {
//    public static String Baseurl="http://192.168.0.105/mealon";
//public static String Baseurl="http://telogix.mangotechsoftwares.com";
  // public static String Baseurl="http://110.93.209.21";
    public static String Baseurl="http://110.93.209.23";
    public static String BaseurlOLD="http://110.93.209.23";

//    public static String Baseurl="http://25.104.152.120";
//    public static String BaseurlOLD="http://25.104.152.120";
    public static String GoogleAPIKEY= "AIzaSyDiYLlYtvo76RREjchYHgq1yanzYk_LzYo";
//mealon/
  //public static String Baseurl="http://edriver.mangotechsoftwares.com";
    public static String completeurl(String url)
    {
        return Baseurl.concat("/"+url);
    }
}
