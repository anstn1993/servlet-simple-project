package com.moonsoo.DAO;

import com.moonsoo.model.Follow;
import com.moonsoo.util.ConnectionPool;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String sql = "insert into follow (following_user_id, followed_user_id) values (?, ?)";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, follow.getFollowingUserId());
            pst.setInt(2, follow.getFollowedUserId());
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


    public int delete(Follow follow) {
        String sql = "delete from follow where following_user_id=? and followed_user_id=?";
        int result = 0;

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, follow.getFollowingUserId());
            pst.setInt(2, follow.getFollowedUserId());
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

    public boolean getFollowStatus(int followingId, int followedId) {
        boolean followStatus = false;
        String sql = "select*from follow where following_user_id=? and followed_user_id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, followingId);
            pst.setInt(2, followedId);
            rs = pst.executeQuery();

            if (rs.next()) {
                followStatus = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if(pst != null) {
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if(con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return followStatus;
    }

    public int getFollowingCount(int id) {
        String sql = "select count(*) as following_count from follow where following_user_id=?";
        int count = 0;

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                count = rs.getInt("following_count");
            }

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
        return count;
    }

    public JSONArray getFollowingList(final int followingId, final int loginUserId) {
        JSONArray followings = new JSONArray();

        String sql = "select user.id as user_id, nickname, image, f2.following_user_id as following_status " +
                "from follow f1 " +
                "left join follow f2 on f1.followed_user_id=f2.followed_user_id and f2.following_user_id=? " +
                "left join user on f1.followed_user_id=user.id " +
                "where f1.following_user_id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, loginUserId);
            pst.setInt(2, followingId);
            rs = pst.executeQuery();

            while (rs.next()) {
                JSONObject userData = new JSONObject();
                userData.put("userId", rs.getString("user_id"));
                userData.put("nickname", rs.getString("nickname"));
                userData.put("profile", rs.getString("image"));
                userData.put("followingStatus", (rs.getString("following_status") != null) ? true : false);
                followings.add(userData);
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
        return followings;
    }

    public JSONArray getFollowerList(final int followedId, final int loginUserId) {
        JSONArray followers = new JSONArray();
        String sql = "select user.id as user_id, nickname, image, f2.following_user_id as following_status " +
                "from follow f1 " +
                "left join follow f2 on f1.following_user_id=f2.followed_user_id and f2.following_user_id=? " +
                "left join user on f1.following_user_id=user.id where f1.followed_user_id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, loginUserId);
            pst.setInt(2, followedId);
            rs = pst.executeQuery();

            while (rs.next()) {
                JSONObject userData = new JSONObject();
                userData.put("userId", rs.getInt("user_id"));
                userData.put("profile", rs.getString("image"));
                userData.put("nickname", rs.getString("nickname"));
                userData.put("followingStatus", (rs.getString("following_status") != null) ? true : false);
                followers.add(userData);
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
        return followers;
    }

    public int getFollowerCount(int id) {
        String sql = "select count(*) as follower_count from follow where followed_user_id=?";
        int count = 0;

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                count = rs.getInt("follower_count");
            }

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
        return count;
    }
}
