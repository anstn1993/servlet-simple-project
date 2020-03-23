package com.moonsoo.DAO;

import com.moonsoo.DTO.PostDTO;
import com.moonsoo.DTO.PostImageDTO;
import com.moonsoo.model.Post;
import com.moonsoo.util.ConnectionPool;
import com.moonsoo.util.Mysql;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PostDAO {
    private static PostDAO postDAO = null;

    public static PostDAO getInstance() {
        if (postDAO == null) {
            postDAO = new PostDAO();
        }
        return postDAO;
    }

    //게시물 업로드시 게시물 삽입
    public int insert(final PostDTO postDTO, final PostImageDTO postImageDTO) {//0:실패, 1:성공, 2:에러
        String sql = "insert into post (user_id, article) values (?, ?)";

        Connection con = null;
        PreparedStatement pst = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();//커넥션 풀에서 커넥션 객체를 불러옴

            pst = con.prepareStatement(sql);

            pst.setInt(1, postDTO.getUserId());
            pst.setString(2, postDTO.getArticle());
            pst.executeUpdate();

            sql = "select id from post order by id desc limit 0, 1";//추가한 게시물의 id를 가져와서 post_image테이블에서 post_id에 넣어준다.
            int postId = 0;
            st = con.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                postId = rs.getInt("id");
            }

            postImageDTO.setPostId(postId);

            sql = "insert into post_image (post_id, file_name) values (?, ?)";
            pst = con.prepareStatement(sql);
            for (int i = 0; i < postImageDTO.getFileNames().size(); i++) {
                pst.setInt(1, postImageDTO.getPostId());
                pst.setString(2, postImageDTO.getFileNames().get(i));
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        } finally {
            if(st != null)
                try { st.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(pst != null)
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(rs != null)
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(con != null)
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return 1;
    }

    public JSONObject getPost(int postId) {//로그아웃 상태에서 요청

        String sql = "select post.user_id as user_id, article, date from post where post.id=?";

        JSONObject postData = new JSONObject();
        postData.put("postId", postId);

        List<String> images = new ArrayList<>();
        JSONArray comments = new JSONArray();//댓글 데이터 json 객체를 담을 json array

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();


            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            if (rs.next()) {
                postData.put("userId", rs.getInt("user_id"));
                postData.put("article", rs.getString("article"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                postData.put("time", beforeTime(date));
                postData.put("likeStatus", false);
            }

            sql = "select count(*) as like_count from like_post where post_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            if (rs.next()) {
                postData.put("likeCount", rs.getInt("like_count"));
            }

            sql = "select file_name from post_image where post_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("file_name"));
            }

            sql = "select comment.id, comment.post_id, comment.user_id, user.nickname, user.image, comment.comment, comment.date from comment join user on comment.user_id=user.id where post_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            while (rs.next()) {
                JSONObject comment = new JSONObject();
                comment.put("id", rs.getInt("id"));
                comment.put("postId", rs.getInt("post_id"));
                comment.put("userId", rs.getInt("user_id"));
                comment.put("nickname", rs.getString("nickname"));
                comment.put("profile", rs.getString("image"));
                comment.put("comment", rs.getString("comment"));
                comment.put("time", beforeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"))));
                comments.add(comment);
            }

            postData.put("images", images);
            postData.put("size", images.size());
            postData.put("comments", comments);

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(rs != null)
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(pst != null)
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(con != null)
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return postData;
    }


    public JSONObject getPost(int loginUserId, int postId) {//로그인 상태에서 요청

        String sql = "select post.user_id as user_id, article, date, like_post.post_id as like_status from post left join like_post on post.id=like_post.post_id and like_post.user_id=? where post.id=?";

        JSONObject postData = new JSONObject();
        postData.put("postId", postId);

        List<String> images = new ArrayList<>();
        JSONArray comments = new JSONArray();//댓글 데이터 json 객체를 담을 json array

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, loginUserId);
            pst.setInt(2, postId);

            rs = pst.executeQuery();
            if (rs.next()) {
                postData.put("userId", rs.getInt("user_id"));
                postData.put("article", rs.getString("article"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                postData.put("time", beforeTime(date));
                boolean likeStatus = false;
                if (rs.getString("like_status") != null) likeStatus = true;
                postData.put("likeStatus", likeStatus);
            }

            sql = "select count(*) as like_count from like_post where post_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            if (rs.next()) {
                postData.put("likeCount", rs.getInt("like_count"));
            }

            sql = "select file_name from post_image where post_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("file_name"));
            }

            sql = "select comment.id, comment.post_id, comment.user_id, user.nickname, user.image, comment.comment, comment.date from comment join user on comment.user_id=user.id where post_id=? limit 0, 4";
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            while (rs.next()) {
                JSONObject comment = new JSONObject();
                comment.put("id", rs.getInt("id"));
                comment.put("postId", rs.getInt("post_id"));
                comment.put("userId", rs.getInt("user_id"));
                comment.put("nickname", rs.getString("nickname"));
                comment.put("profile", rs.getString("image"));
                comment.put("comment", rs.getString("comment"));
                comment.put("time", beforeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"))));
                comments.add(comment);
            }

            postData.put("images", images);
            postData.put("size", images.size());
            postData.put("comments", comments);

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(rs != null)
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(pst != null)
                try { pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            if(con != null)
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return postData;
    }

    //게시물 모델 리스트를 가져온다. 매개변수로 전달된 인덱스로부터 18개씩(로그인 상태에서 실행)
    public List<Post> getPosts(int loginUserId, int startIndex) {

        List<Post> posts = new ArrayList<>();

        String sql = "select user.id, user.nickname, user.image, post.id as post_id, post.article, post.date, post_image.file_name, like_post.post_id as like_status " +
                "from post " +
                "left join user on post.user_id=user.id " +
                "left join post_image on post.id=post_image.post_id " +
                "left join like_post on post.id=like_post.post_id and like_post.user_id=? " +
                "group by post_id " +
                "order by post_id desc " +
                "limit ?, 6";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = ConnectionPool.getConnection();//커넥션 풀에서 커넥션 객체를 하나 가져온다.

            pst = con.prepareStatement(sql);
            pst.setInt(1, loginUserId);
            pst.setInt(2, startIndex);
            rs = pst.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("id");
                String nickname = rs.getString("nickname");
                String profile = rs.getString("image");
                String article = rs.getString("article");
                int postId = rs.getInt("post_id");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                String fileName = rs.getString("file_name");
                boolean likeStatus = false;
                if (rs.getString("like_status") != null) likeStatus = true;
                posts.add(new Post(userId, postId, nickname, profile, article, fileName, beforeTime(date), likeStatus));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }finally {
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
        return posts;
    }

    //페이징으로 다음 게시물을 가져올 때 호출되는 메소드
    public List<Post> getUserPosts(int userId, int startIndex) {
        List<Post> posts = new ArrayList<>();
        String sql = "select user.id, post.id as post_id, post.article, pi.file_name, post.date from post, (select*from post_image)pi, (select*from user)user" +
                " where post.user_id=user.id and post.id=pi.post_id and user.id=? group by post_id order by post_id desc limit ?, 6";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, startIndex);
            rs = pst.executeQuery();
            while (rs.next()) {
                int postId = rs.getInt("post_id");
                String article = rs.getString("article");
                String fileName = rs.getString("file_name");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                posts.add(new Post(userId, postId, article, fileName, beforeTime(date)));
            }
        } catch (SQLException | ParseException e) {
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
        return posts;
    }

    public JSONArray getNextPosts(int loginUserId, int lastPostId) {

        JSONArray posts = new JSONArray();

        String sql = "select count(*) as start_index from post where id>=?";


        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection con = null;

        try {
            con = ConnectionPool.getConnection();//커넥션 풀에서 커넥션 객체를 하나 가져온다.

            int startIndex = 0;//게시물 테이블 레코드의 시작 index
            if (lastPostId != 0) {
                pst = con.prepareStatement(sql);
                pst.setInt(1, lastPostId);
                rs = pst.executeQuery();
                if (rs.next()) {
                    startIndex = rs.getInt("start_index");
                }
            }

            sql = "select user.id, user.nickname, user.image, post.id as post_id, post.article, post.date, post_image.file_name, like_post.post_id as like_status " +
                    "from post " +
                    "left join user on post.user_id=user.id " +
                    "left join post_image on post.id=post_image.post_id " +
                    "left join like_post on post.id=like_post.post_id and like_post.user_id=? " +
                    "group by post_id " +
                    "order by post_id desc " +
                    "limit ?, 6";
            pst = con.prepareStatement(sql);
            pst.setInt(1, loginUserId);
            pst.setInt(2, startIndex);

            rs = pst.executeQuery();
            while (rs.next()) {
                JSONObject post = new JSONObject();
                post.put("userId", rs.getInt("id"));
                post.put("nickname", rs.getString("nickname"));
                post.put("profile", rs.getString("image"));
                post.put("article", rs.getString("article"));
                post.put("postId", rs.getInt("post_id"));
                post.put("profile", rs.getString("image"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                post.put("time", beforeTime(date));
                post.put("fileName", rs.getString("file_name"));
                post.put("likeStatus", rs.getString("like_status") != null);
                posts.add(post);
            }

        } catch (SQLException | ParseException e) {
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
        return posts;
    }

    //페이징으로 다음 게시물을 가져올 때 호출되는 메소드(사용자 페이지)
    public JSONArray getNextPostsInUserPage(int userId, int lastPostId) {
        JSONArray posts = new JSONArray();
        String sql = null;


        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection con = null;

        try {
            con = ConnectionPool.getConnection();//커넥션 풀에서 커넥션 객체를 하나 가져온다.

            int startIndex = 0;//게시물 테이블 레코드의 시작 index
            if (lastPostId != 0) {
                sql = "select count(*) as start_index from post where user_id=? and id>=?";
                pst = con.prepareStatement(sql);
                pst.setInt(1, userId);
                pst.setInt(2, lastPostId);
                rs = pst.executeQuery();
                if (rs.next()) {
                    startIndex = rs.getInt("start_index");
                }
            }

            sql = "select user.nickname, user.image as profile, post.id as post_id, post.article, pi.file_name, post.date from post, (select*from post_image)pi, (select*from user)user" +
                    " where post.user_id=user.id and post.id=pi.post_id and user.id=? group by post_id order by post_id desc limit ?, 6";
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, startIndex);

            rs = pst.executeQuery();
            while (rs.next()) {
                JSONObject post = new JSONObject();
                post.put("userId", userId);
                post.put("nickname", rs.getString("nickname"));
                post.put("profile", rs.getString("profile"));
                post.put("postId", rs.getInt("post_id"));
                post.put("article", rs.getString("article"));
                post.put("fileName", rs.getString("file_name"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                post.put("time", beforeTime(date));
                posts.add(post);
            }

        } catch (SQLException | ParseException e) {
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

        return posts;
    }


    //게시물 상세보기시 게시물의 모든 이미지를 가져오는 메소드
    public List<String> getPostImages(int postId) {
        List<String> images = new ArrayList<>();

        String sql = "select file_name from post_image where post_id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            rs = pst.executeQuery();
            while (rs.next()) {
                String fileName = rs.getString("file_name");
                images.add(fileName);
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
        return images;
    }

    //게시물 작성자 id를 반환하는 메소드
    public int getUserId(int postId) {

        int userId = 0;//사용자 id
        String sql = "select user_id from post where id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);

            rs = pst.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
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
        return userId;
    }

    public String getArticle(int postId) {

        String article = null;
        String sql = "select article from post where id=?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();

            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);

            rs = pst.executeQuery();
            if (rs.next()) {
                article = rs.getString("article");
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
        return article;
    }

    //게시물 수정 update
    public int update(int postId, String article, List<String> newFiles, List<String> transferredExistingFiles) {
        int result = 1;//post테이블 쿼리 결과
        String sql = "update post set article=? where id=?";

        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false);

            pst = con.prepareStatement(sql);
            pst.setString(1, article);
            pst.setInt(2, postId);
            pst.executeUpdate();


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
            pst.executeUpdate();

            sql = "insert into post_image (post_id, file_name) values (?, ?)";pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            for (String newFile : newFiles) {
                pst.setString(2, newFile);
                pst.executeUpdate();
            }

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

            result = -1;
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

    public int deletePost(int postId) {
        int result = -1;
        String sql = "delete from post where id=?";

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = ConnectionPool.getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
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

    public int getPostCount(int id) {
        int postCount = 0;
        String sql = "select count(*) as post_count from post where user_id=?";
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = ConnectionPool.getConnection();
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if(rs.next()) {
                postCount = rs.getInt("post_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        return postCount;
    }

    //업로드된 게시물이 몇 분 전에 만들어진 게시물인지를 리턴해주는 메소드
    public String beforeTime(Date date) {

        //캘린더 클레스는 추상 클레스라서 객체를 생성할 수 없다.
        //대신 getInstance()메소드를 통해서 객체 생성이 가능하다.
        Calendar c = Calendar.getInstance();

        //캘린더 객체에서 getTimeInMillis()메소드를 사용해 현재 시간을 가져옴
        long now = c.getTimeInMillis();
        //date에서 시간만 가져온다. 여기서 중요한 점은 now변수는 계속해서 현재시간을 반환하기 때문에 변하는 수이고
        //date는 내가 선언한 순간의 시간을 가져오기 때문에 고정된 시간이다.
        long dateM = date.getTime();

        //이 변수는 위에서 봤듯이 현재의 시간에서 내가 이 메소드를 호출한 시간을 뺀 시간을 의미한다.
        long gap = now - dateM;

        String ret = "";

//        초       분   시
//        1000    60  60
        gap = gap / 1000;
        long hour = gap / 3600;
        gap = gap % 3600;
        long min = gap / 60;
        long sec = gap % 60;

        if (hour > 24) {
            ret = hour / 24 + "일 전";
        } else if (hour > 0) {
            ret = hour + "시간 전";
        } else if (min > 0) {
            ret = min + "분 전";
        } else if (sec > 0) {
            ret = sec + "초 전";
        } else {
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        return ret;

    }



}
