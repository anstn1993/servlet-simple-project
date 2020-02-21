package com.moonsoo.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Naver extends Authenticator {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("your email address", "your password");
    }
}
