package com.moonsoo.DTO;

public class UserDTO {
    private int id;
    private String account;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String emailHash;
    private String profile;
    private String introduce;

    public UserDTO(String account, String password, String name, String nickname, String email, String emailHash) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.emailHash = emailHash;
    }

    public UserDTO(String account, String password, String name, String nickname, String email, String emailHash, String profile, String introduce) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.emailHash = emailHash;
        this.profile = profile;
        this.introduce = introduce;
    }

    public UserDTO(int id, String account, String password, String name, String nickname, String email, String emailHash, String profile, String introduce) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.emailHash = emailHash;
        this.profile = profile;
        this.introduce = introduce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
