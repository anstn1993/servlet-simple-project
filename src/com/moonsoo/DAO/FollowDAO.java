package com.moonsoo.DAO;

import com.moonsoo.model.Follow;
import com.moonsoo.model.Like;
import com.moonsoo.util.Mysql;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;

public class FollowDAO {
    private static FollowDAO followDAO = null;

    public static FollowDAO getInstance() {
        if (followDAO == null) {
            followDAO = new FollowDAO();
        }
        return followDAO;
    }

    //게시물 업로드시 게시물 삽입
    public int insert(Follow follow) {//0:실패, 1:성공, -1:에러
        String url = Mysql.getInstance().getUrl();
        String sql = "insert into like_post (post_id, user_id) values (?, ?)";
        int result = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            Statement st = con.createStatement();
//            pst.setInt(1, like.getPostId());
//            pst.setInt(2, like.getUserId());
            result = pst.executeUpdate();
            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public int delete(Follow follow) {
        String url = Mysql.getInstance().getUrl();
        String sql = "delete from like_post where post_id=? and user_id=?";
        int result = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            Statement st = con.createStatement();
//            pst.setInt(1, like.getPostId());
//            pst.setInt(2, like.getUserId());
            result = pst.executeUpdate();
            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean getFollowStatus(int followingId, int followedId) {
        boolean followStatus = false;
        String url = Mysql.getInstance().getUrl();
        String sql = "select*from follow where following_user_id=? and followed_user_id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, followingId);
            pst.setInt(2, followedId);
            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                followStatus = true;
            }

            pst.close();
            con.close();
            return followStatus;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JSONArray getFollowerList(int userId) {
        JSONArray likeList = new JSONArray();
        String url = Mysql.getInstance().getUrl();
        String sql = "select user_id, user.image as profile, nickname from like_post join user on like_post.user_id=user.id where post_id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
//            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                JSONObject data = new JSONObject();
                data.put("userId", rs.getInt("user_id"));
                data.put("profile", rs.getString("profile"));
                data.put("nickname", rs.getString("nickname"));
                likeList.add(data);
            }

            pst.close();
            con.close();
            return likeList;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
