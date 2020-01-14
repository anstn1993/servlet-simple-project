package com.moonsoo.DAO;

import com.moonsoo.model.Like;
import com.moonsoo.util.ConnectionPool;
import com.moonsoo.util.Mysql;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;

public class LikeDAO {
    private static LikeDAO likeDAO = null;

    public static LikeDAO getInstance() {
        if (likeDAO == null) {
            likeDAO = new LikeDAO();
        }
        return likeDAO;
    }

    //게시물 업로드시 게시물 삽입
    public int insert(Like like) {//0:실패, 1:성공, -1:에러
//        String url = Mysql.getInstance().getUrl();
        String sql = "insert into like_post (post_id, user_id) values (?, ?)";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, like.getPostId());
            pst.setInt(2, like.getUserId());
            result = pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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


    public int delete(Like like) {
//        String url = Mysql.getInstance().getUrl();
        String sql = "delete from like_post where post_id=? and user_id=?";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, like.getPostId());
            pst.setInt(2, like.getUserId());
            result = pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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

    public JSONArray getLikeList(final int loginUserId, final int postId) {
        JSONArray likeList = new JSONArray();
        String url = Mysql.getInstance().getUrl();
        String sql = "select user_id, user.image as profile, nickname, followed_user_id as follow_status " +
                "from like_post " +
                "join user on like_post.user_id=user.id " +
                "left join follow on follow.followed_user_id=user_id and following_user_id=? " +
                "where post_id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, loginUserId);
            pst.setInt(2, postId);
            rs = pst.executeQuery();

            while (rs.next()) {
                JSONObject data = new JSONObject();
                data.put("userId", rs.getInt("user_id"));
                data.put("profile", rs.getString("profile"));
                data.put("nickname", rs.getString("nickname"));
                data.put("followStatus", (rs.getString("follow_status") != null) ? true : false);
                likeList.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return likeList;
    }
}
