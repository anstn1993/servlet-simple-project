package com.moonsoo.DAO;

import com.moonsoo.DTO.PostImageDTO;
import com.moonsoo.util.ConnectionPool;
import com.moonsoo.util.Mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostImageDAO {
    private static PostImageDAO postImageDAO = null;

    public static PostImageDAO getInstance() {
        if (postImageDAO == null) {
            postImageDAO = new PostImageDAO();
        }
        return postImageDAO;
    }


    //게시물의 이미지 파일명 삭제
    public int delete(int postId, List<String> transferredExistingFiles) {
        int result = -1;//post_image테이블에서 기존 이미지를 삭제하는 쿼리 결과
//        String url = Mysql.getInstance().getUrl();
        String sql = null;

        Connection con = null;
        PreparedStatement pst = null;
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            con = ConnectionPool.getConnection();

            int count = transferredExistingFiles.size();
            switch (count) {
                case 0:
                    sql = "delete from post_image where post_id=?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    break;
                case 1:
                    sql = "delete from post_image where post_id=? and file_name<>?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.setString(2, transferredExistingFiles.get(0));
                    break;
                case 2:
                    sql = "delete from post_image where post_id=? and file_name<>? and file_name<>?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.setString(2, transferredExistingFiles.get(0));
                    pst.setString(3, transferredExistingFiles.get(1));
                    break;
                case 3:
                    sql = "delete from post_image where post_id=? and file_name<>? and file_name<>? and file_name<>?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.setString(2, transferredExistingFiles.get(0));
                    pst.setString(3, transferredExistingFiles.get(1));
                    pst.setString(4, transferredExistingFiles.get(2));
                    break;
                case 4:
                    sql = "delete from post_image where post_id=? and file_name<>? and file_name<>? and file_name<>? and file_name<>?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.setString(2, transferredExistingFiles.get(0));
                    pst.setString(3, transferredExistingFiles.get(1));
                    pst.setString(4, transferredExistingFiles.get(2));
                    pst.setString(5, transferredExistingFiles.get(3));
                    break;
                case 5:
                    sql = "delete from post_image where post_id=? and file_name<>? and file_name<>? and file_name<>? and file_name<>? and file_name<>?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.setString(2, transferredExistingFiles.get(0));
                    pst.setString(3, transferredExistingFiles.get(1));
                    pst.setString(4, transferredExistingFiles.get(2));
                    pst.setString(5, transferredExistingFiles.get(3));
                    pst.setString(6, transferredExistingFiles.get(4));
                    break;
                case 6:
                    sql = "delete from post_image where post_id=? and file_name<>? and file_name<>? and file_name<>? and file_name<>? and file_name<>? and file_name<>?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.setString(2, transferredExistingFiles.get(0));
                    pst.setString(3, transferredExistingFiles.get(1));
                    pst.setString(4, transferredExistingFiles.get(2));
                    pst.setString(5, transferredExistingFiles.get(3));
                    pst.setString(6, transferredExistingFiles.get(4));
                    pst.setString(7, transferredExistingFiles.get(5));
                    break;
            }

            result = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if(pst != null)
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(con != null)
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    //게시물의 이미지 파일명 새롭게 update
    public int update(int postId, List<String> newFiles) {
        int result = 0;
//        String url = Mysql.getInstance().getUrl();
        String sql = "insert into post_image (post_id, file_name) values (?, ?)";

        Connection con = null;
        PreparedStatement pst = null;

        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            for (String newFile : newFiles) {
                pst.setString(2, newFile);
                result = pst.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if(pst != null)
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(con != null)
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    //이미지 파일 명 return
    public List<String> getFiles(int postId) {
        List<String> files = new ArrayList<>();
//        String url = Mysql.getInstance().getUrl();
        String sql = "select file_name from post_image where post_id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            while (rs.next()) {
                String fileName = rs.getString("file_name");
                files.add(fileName);
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
        return files;
    }

}
