package com.moonsoo.DAO;

import com.moonsoo.DTO.UserDTO;
import com.moonsoo.model.User;
import com.moonsoo.util.Mysql;
import com.moonsoo.util.SHA256;

import java.sql.*;
import java.util.HashMap;

public class UserDAO {
    private static UserDAO userDAO = null;

    public static UserDAO getInstance() {
        if (userDAO == null) {
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    //회원가입시 유저 데이터 삽입
    public boolean insert(final UserDTO userDTO) {
        String url = Mysql.getInstance().getUrl();
        String sql = "insert into user (account, password, name, nickname, email, email_hash) values (?, ?, ?, ?, ?, ?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, userDTO.getAccount());
            pst.setString(2, userDTO.getPassword());
            pst.setString(3, userDTO.getName());
            pst.setString(4, userDTO.getNickname());
            pst.setString(5, userDTO.getEmail());
            pst.setString(6, userDTO.getEmailHash());
            pst.executeUpdate();

            pst.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //이메일 인증 검증
    public int checkEmailAuthentication(final String account, final String code) {//0:비인증, 1:이미 인증된 상태, 2: 오류
        String url = Mysql.getInstance().getUrl();
        String sql = "select*from user where account =? and email_hash=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, account);
            pst.setString(2, code);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int emailChecked = rs.getInt("email_checked");
                if (emailChecked == 0) {
                    sql = "update user set email_checked=? where account=?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, 1);
                    pst.setString(2, account);
                    pst.executeUpdate();
                    pst.close();
                    con.close();
                    return 0;
                } else {
                    //TODO:이미 인증된 이메일
                    return 1;
                }
            } else {
                return 2;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;
        }

    }

    //사용자 정보 추출
    public UserDTO getUserData(final String account) {
        String url = Mysql.getInstance().getUrl();
        String sql = "select*from user where account =?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, account);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String nickname = rs.getString("nickname");
                String email = rs.getString("email");
                String emailHash = rs.getString("email_hash");
                String profile = rs.getString("image");
                String introduce = rs.getString("introduce");
                rs.close();
                pst.close();
                con.close();
                return new UserDTO(id, account, password, name, nickname, email, emailHash, profile, introduce);
            } else {
                rs.close();
                pst.close();
                con.close();
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserData(int id) {
        String url = Mysql.getInstance().getUrl();
        String sql = "select*from user where id =?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String nickname = rs.getString("nickname");
                String profile = rs.getString("image");
                String introduce = rs.getString("introduce");
                rs.close();
                pst.close();
                con.close();
                return new User(id, name, nickname, profile, introduce);
            }
            rs.close();
            pst.close();
            con.close();
            return null;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //로그인 검사
    public int loginCheck(final String account, final String password) {
        String url = Mysql.getInstance().getUrl();
        String sql = "select*from user where account=? and password=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, account);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            int result = 0;
            if (rs.next()) {//로그인 성공
                int emailChecked = rs.getInt("email_checked");
                if (emailChecked == 0) {//미인증 상태
                    result = 2;
                } else {//인증 상태
                    result = 1;
                }
            }
            rs.close();
            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 3;//에러
        }
    }

    //프로필 편집시 사용자 데이터 업데이트를 해주는 메소드
    public int updateUserData(HashMap<String, String> userData) {//0: 실패  1: 성공  2: 에러
        String url = Mysql.getInstance().getUrl();
        String sql = "update user set name=?, nickname=?, email=?, image=?, introduce=?, email_hash=? where id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, userData.get("name"));
            pst.setString(2, userData.get("nickname"));
            pst.setString(3, userData.get("email"));
            pst.setString(4, (userData.get("profile") != null) ? userData.get("profile") : null);
            pst.setString(5, (userData.get("introduce") != null) ? userData.get("introduce") : null);
            pst.setString(6, userData.get("emailHash"));
            pst.setString(7, userData.get("id"));
            int result = pst.executeUpdate();
            System.out.println(result);
            pst.close();
            con.close();
            return result;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;//에러
        }
    }

    //비밀번호 변경에서 기존 비밀번호의 유효성 검사 실시하는 메소드
    public int checkOldPassword(int id, String oldPassword) {
        String url = Mysql.getInstance().getUrl();
        String sql = "select*from user where id=? and password=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, SHA256.getSha256(oldPassword));
            ResultSet rs = pst.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = 1;
            }
            pst.close();
            con.close();
            return result;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;//에러
        }
    }

    public int updatdPassword(int id, String password) {
        String url = Mysql.getInstance().getUrl();
        String sql = "update user set password=? where id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, SHA256.getSha256(password));
            pst.setInt(2, id);
            int result = pst.executeUpdate();
            System.out.println(result);
            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;//에러
        }
    }
}
