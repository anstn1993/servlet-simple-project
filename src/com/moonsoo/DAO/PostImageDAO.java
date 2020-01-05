package com.moonsoo.DAO;

import com.moonsoo.DTO.PostImageDTO;
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

    //게시물 업로드시 게시물 삽입
    public int insert(final PostImageDTO postImageDTO) {//0:실패, 1:성공, 2:에러
        String url = Mysql.getInstance().getUrl();
        String sql = "insert into post_image (post_id, file_name) values (?, ?)";
        int result = 0;//실행 결과
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            PreparedStatement pst = con.prepareStatement(sql);
            for (int i = 0; i < postImageDTO.getFileNames().size(); i++) {
                pst.setInt(1, postImageDTO.getPostId());
                pst.setString(2, postImageDTO.getFileNames().get(i));
                result = pst.executeUpdate();
            }
            pst.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;
        }
        return result;
    }

    //게시물의 이미지 파일명 삭제
    public int delete(int postId, List<String> transferredExistingFiles) {
        int result = -1;//post_image테이블에서 기존 이미지를 삭제하는 쿼리 결과
        String url = Mysql.getInstance().getUrl();
        String sql = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());

            PreparedStatement pst = null;
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

            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //게시물의 이미지 파일명 새롭게 update
    public int update(int postId, List<String> newFiles) {
        int result = 0;
        String url = Mysql.getInstance().getUrl();
        String sql = "insert into post_image (post_id, file_name) values (?, ?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            for (String newFile : newFiles) {
                pst.setString(2, newFile);
                result = pst.executeUpdate();
            }
            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //이미지 파일 명 return
    public List<String> getFiles(int postId) {
        List<String> files = new ArrayList<>();
        String url = Mysql.getInstance().getUrl();
        String sql = "select file_name from post_image where post_id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String fileName = rs.getString("file_name");
                files.add(fileName);
            }
            rs.close();
            pst.close();
            con.close();
            return files;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
