package com.moonsoo.util;

public class Mysql {
     private String url;
     private String account;
     private String password;

     private static Mysql mysql = null;

    public Mysql(String url, String account, String password) {
        this.url = url;
        this.account = account;
        this.password = password;
    }

    public static Mysql getInstance() {
         if(mysql == null) {
             mysql = new Mysql(
                     "jdbc:mysql://13.124.105.47:3306/soso?autoReconnect=true",
                     "moonsoo",
                     "Rla933466r!"
             );
         }

         return mysql;
     }

    public String getUrl() {
        return url;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

}
