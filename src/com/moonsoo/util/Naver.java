package com.moonsoo.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Naver extends Authenticator {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication("anstn1993@gmail.com", "rla933466r!");
        return new PasswordAuthentication("rlarpdlcm@naver.com", "rla933466r");
    }
}
