package com.moonsoo.DAO;

import com.moonsoo.DTO.PostDTO;
import com.moonsoo.DTO.PostImageDTO;
import com.moonsoo.model.Comment;
import com.moonsoo.model.Post;
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
        String url = Mysql.getInstance().getUrl();
        String sql = "insert into post (user_id, article) values (?, ?)";
        int postResult = 0;
        int postImageResult = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            Statement st = con.createStatement();
            pst.setInt(1, postDTO.getUserId());
            pst.setString(2, (postDTO.getArticle() == null) ? null : postDTO.getArticle());
            postResult = pst.executeUpdate();

            sql = "select id from post order by id desc limit 0, 1";//추가한 게시물의 id를 가져와서 post_image테이블에서 post_id에 넣어준다.
            int postId = 0;
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                postId = rs.getInt("id");
            }

            postImageDTO.setPostId(postId);

            postImageResult = PostImageDAO.getInstance().insert(postImageDTO);//post_image테이블에 레코드 추가


            pst.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;
        }
        return (postResult != 0 && postImageResult != 0) ? 1 : 0;
    }

    public JSONObject getPost(int postId) {

        String url = Mysql.getInstance().getUrl();
        String sql = "select user_id, article, date, file_name from post join post_image on post.id = post_image.post_id where post.id=?";

        JSONObject postData = new JSONObject();
        postData.put("postId", postId);

        List<String> images = new ArrayList<>();
        JSONArray comments = new JSONArray();//댓글 데이터 json 객체를 담을 json array

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                if(rs.isFirst()) {//첫 번째 행인 경우
                    postData.put("userId", rs.getInt("user_id"));
                    postData.put("article", rs.getString("article"));
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                    postData.put("time", beforeTime(date));
                    images.add(rs.getString("file_name"));
                }
                else {
                    images.add(rs.getString("file_name"));
                }
            }

            sql = "select comment.id, comment.post_id, comment.user_id, user.nickname, user.image, comment.comment, comment.date from comment join user on comment.user_id=user.id where post_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt( 1, postId);
            rs = pst.executeQuery();
            while(rs.next()) {
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
            rs.close();
            pst.close();
            con.close();
            return postData;
        } catch (ClassNotFoundException | SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    //게시물 모델 리스트를 가져온다. 매개변수로 전달된 인덱스로부터 18개씩(페이징 적용)
    public List<Post> getPosts(int startIndex) {

        List<Post> posts = new ArrayList<>();

        String url = Mysql.getInstance().getUrl();
        String sql = "select user.id, user.nickname, user.image, post.id as post_id, post.article, post.date, pi.file_name from post, (select*from post_image)pi, (select*from user)user" +
                " where post.user_id=user.id and post.id=pi.post_id group by post_id order by post_id desc limit ?, 18";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, startIndex);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("id");
                String nickname = rs.getString("nickname");
                String profile = rs.getString("image");
                String article = rs.getString("article");
                int postId = rs.getInt("post_id");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                String fileName = rs.getString("file_name");
                posts.add(new Post(userId, postId, nickname, profile, article, fileName, beforeTime(date)));
            }
            rs.close();
            pst.close();
            con.close();


            return posts;

        } catch (ClassNotFoundException | SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Post> getUserPosts(int userId, int startIndex) {
        List<Post> posts = new ArrayList<>();
        String url = Mysql.getInstance().getUrl();
        String sql = "select user.id, post.id as post_id, post.article, pi.file_name, post.date from post, (select*from post_image)pi, (select*from user)user" +
                " where post.user_id=user.id and post.id=pi.post_id and user.id=? group by post_id order by post_id desc limit ?, 18";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, startIndex);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int postId = rs.getInt("post_id");
                String article = rs.getString("article");
                String fileName = rs.getString("file_name");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                posts.add(new Post(userId, postId, article, fileName, beforeTime(date)));
            }
            rs.close();
            pst.close();
            con.close();


            return posts;

        } catch (ClassNotFoundException | SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //게시물 상세보기시 게시물의 모든 이미지를 가져오는 메소드
    public List<String> getPostImages(int postId) {
        List<String> images = new ArrayList<>();

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
                images.add(fileName);
            }
            rs.close();
            pst.close();
            con.close();
            return images;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //게시물 작성자 id를 반환하는 메소드
    public int getUserId(int postId) {
        int userId = 0;//사용자 id
        String url = Mysql.getInstance().getUrl();
        String sql = "select user_id from post where id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
            rs.close();
            pst.close();
            con.close();
            return userId;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getArticle(int postId) {
        String article = null;
        String url = Mysql.getInstance().getUrl();
        String sql = "select article from post where id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                article = rs.getString("article");
            }
            rs.close();
            pst.close();
            con.close();
            return article;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUploadTime(int postId) {
        String url = Mysql.getInstance().getUrl();
        String sql = "select date from post where id=?";
        Date date = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
            }

            rs.close();
            pst.close();
            con.close();
            return beforeTime(date);
        } catch (ClassNotFoundException | SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    //게시물 수정 update
    public int update(int postId, String article, List<String> newFiles, List<String> transferredExistingFiles) {
        int postResult = 0;//post테이블 쿼리 결과
        int imageDeleteResult = 0;//post_image테이블에서 기존 이미지 파일 명을 삭제하는 쿼리 결과
        int imageUpdateResult = 0;//post_image테이블에 새로운 이미지 파일 명을 삽입하는 쿼리 결과
        String url = Mysql.getInstance().getUrl();
        String sql = "update post set article=? where id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, article);
            pst.setInt(2, postId);
            postResult = pst.executeUpdate();

            imageDeleteResult = PostImageDAO.getInstance().delete(postId, transferredExistingFiles);//기존 이미지 파일명 테이블에서 삭제
            imageUpdateResult = PostImageDAO.getInstance().update(postId, newFiles);

            pst.close();
            con.close();

            return (postResult == 1 && imageDeleteResult != -1 && imageUpdateResult != -1) ? 1 : 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;
        }
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
        gap = (long) (gap / 1000);
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
