package com.moonsoo.DAO;

import com.moonsoo.DTO.UserDTO;
import com.moonsoo.model.User;
import com.moonsoo.util.ConnectionPool;
import com.moonsoo.util.Mysql;
import com.moonsoo.util.SHA256;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDAO {
    private static UserDAO userDAO = null;

    public static UserDAO getInstance() {
        if (userDAO == null) {
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    //회원가입시 유저 데이터 삽입
    public int insert(final UserDTO userDTO) {
        int id = 0;
        String sql = "insert into user (account, password, name, nickname, email, email_hash) values (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setString(1, userDTO.getAccount());
            pst.setString(2, userDTO.getPassword());
            pst.setString(3, userDTO.getName());
            pst.setString(4, userDTO.getNickname());
            pst.setString(5, userDTO.getEmail());
            pst.setString(6, userDTO.getEmailHash());
            pst.executeUpdate();

            sql = "select id from user where account=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, userDTO.getAccount());
            rs = pst.executeQuery();

            if(rs.next()) {
                id = rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return id;
    }

    //유저 정보 삭제(회원 가입시 인증 메일 전송 실패시)
    public void delete(final int userId) {
        String sql = "delete from user where id=?";

        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = ConnectionPool.getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

    }

    //계정, 닉네임 중복 체크
    public int checkValidation(String type, String value) {
        int result = 0;
        String sql = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();
            if (type.equals("account")) {
                sql = "select*from user where account=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, value);
            } else if (type.equals("nickname")) {
                sql = "select*from user where nickname=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, value);
            }
            rs = pst.executeQuery();

            if (rs.next()) {//데이터가 존재하면 중복하는 데이터
                result = 0;
            } else {//데이터가 존재하지 않으면 사용 가능한 데이터
                result = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = -1;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //이메일 인증 검증
    public int checkEmailAuthentication(final String account, final String code) {//0:비인증, 1:이미 인증된 상태, 2: 오류
        String sql = "select*from user where account =? and email_hash=?";
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setString(1, account);
            pst.setString(2, code);
            rs = pst.executeQuery();
            if (rs.next()) {
                int emailChecked = rs.getInt("email_checked");
                if (emailChecked == 0) {
                    sql = "update user set email_checked=? where account=?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, 1);
                    pst.setString(2, account);
                    pst.executeUpdate();
                    return 0;
                } else {
                    //TODO:이미 인증된 이메일
                    return 1;
                }
            } else {
                return 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

    }

    //사용자 정보 추출
    public UserDTO getUserData(final String account) {
        String sql = "select*from user where account =?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();
            pst = con.prepareStatement(sql);
            pst.setString(1, account);
            rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String nickname = rs.getString("nickname");
                String email = rs.getString("email");
                String emailHash = rs.getString("email_hash");
                String profile = rs.getString("image");
                String introduce = rs.getString("introduce");
                return new UserDTO(id, account, password, name, nickname, email, emailHash, profile, introduce);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public User getUserData(int id) {
        String sql = "select*from user where id =?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String nickname = rs.getString("nickname");
                String profile = rs.getString("image");
                String introduce = rs.getString("introduce");
                return new User(id, name, nickname, profile, introduce);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    //로그인 검사
    public int loginCheck(final String account, final String password) {
        String sql = "select*from user where account=? and password=?";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setString(1, account);
            pst.setString(2, password);
            rs = pst.executeQuery();
            if (rs.next()) {//로그인 성공
                int emailChecked = rs.getInt("email_checked");
                if (emailChecked == 0) {//미인증 상태
                    result = 2;
                } else {//인증 상태
                    result = 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 3;//에러
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    //프로필 편집시 사용자 데이터 업데이트를 해주는 메소드
    public int updateUserData(HashMap<String, String> userData) {//0: 실패  1: 성공  2: 에러
        String sql = "update user set name=?, nickname=?, email=?, image=?, introduce=?, email_hash=? where id=?";
        int result = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setString(1, userData.get("name"));
            pst.setString(2, userData.get("nickname"));
            pst.setString(3, userData.get("email"));
            pst.setString(4, (userData.get("profile") != null) ? userData.get("profile") : null);
            pst.setString(5, (userData.get("introduce") != null) ? userData.get("introduce") : null);
            pst.setString(6, userData.get("emailHash"));
            pst.setString(7, userData.get("id"));
            result = pst.executeUpdate();
            System.out.println(result);

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;//에러
        } finally {
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    //비밀번호 변경에서 기존 비밀번호의 유효성 검사 실시하는 메소드
    public int checkOldPassword(int id, String oldPassword) {
        String sql = "select*from user where id=? and password=?";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, SHA256.getSha256(oldPassword));
            rs = pst.executeQuery();
            if (rs.next()) {
                result = 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;//에러
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    public int updatdPassword(int id, String password) {
        String sql = "update user set password=? where id=?";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setString(1, SHA256.getSha256(password));
            pst.setInt(2, id);
            result = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;//에러
        } finally {
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    //회원 탈퇴
    public int withdrawl(final int userId, final String profile, final String profilePath, final String postImagePath) {
        int result = 1;
        List<Integer> postIds = new ArrayList<>();//삭제해야 될 게시물 id 리스트
        List<String> fileNames = new ArrayList<>();//삭제해야 할 게시물 이미지 파일 명 리스트
        String sql = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false);

            sql = "select post.id as post_id, file_name from post left join post_image on post.id=post_image.post_id where user_id=?";

            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            rs = pst.executeQuery();
            while(rs.next()) {
                int postId = rs.getInt("post_id");
                if(postIds.indexOf(postId) == -1) {
                    postIds.add(postId);
                }

                fileNames.add(rs.getString("file_name"));
            }

            sql = "delete from post_image where post_id=?";//게시물 id에 해당하는 게시물 이미지 데이터를 테이블에서 삭제
            for(int postId : postIds) {
                pst = con.prepareStatement(sql);
                pst.setInt(1, postId);
                pst.executeUpdate();
            }

            sql = "delete from post where user_id=?";//탈퇴한 사용자의 게시물 데이터를 모두 삭제
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.executeUpdate();

            sql = "delete from comment where user_id=?";//탈퇴한 사용자가 작성한 댓글 데이터를 모두 삭제
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.executeUpdate();

            sql = "delete from follow where followed_user_id=? or following_user_id=?";//탈퇴한 사용자의 팔로워 팔로잉 데이터 모두 삭제
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, userId);
            pst.executeUpdate();

            sql = "delete from like_post where user_id=?";//탈퇴한 사용자의 좋아요 데이터 모두 삭제
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.executeUpdate();

            sql = "delete from user where id=?";//탈퇴한 사용자의 정보를 유저 테이블에서 삭제
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.executeUpdate();

            con.commit();
            con.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1;
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

        if(profile != null && !profile.equals("")) {
            File file = new File(profilePath + profile);//프로필 사진 파일 삭제
            file.delete();
        }

        for(String fileName : fileNames) {//게시물 파일 삭제
            File file = new File(postImagePath+fileName);
            file.delete();
        }

        return result;
    }
}
