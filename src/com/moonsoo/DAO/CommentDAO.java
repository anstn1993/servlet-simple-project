package com.moonsoo.DAO;

import com.moonsoo.model.Comment;
import com.moonsoo.util.Mysql;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommentDAO {
    private static CommentDAO commentDAO = null;

    public static CommentDAO getInstance() {
        if (commentDAO == null) {
            commentDAO = new CommentDAO();
        }
        return commentDAO;
    }

    //댓글 업로드시 게시물 삽입
    public int insert(Comment comment) {//0:실패, 1:성공, 2:에러
        String url = Mysql.getInstance().getUrl();
        String sql = "insert into comment (post_id, user_id, comment) values (?, ?, ?)";
        int result = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, comment.getPostId());
            pst.setInt(2, comment.getUserId());
            pst.setString(3, comment.getComment());
            result = pst.executeUpdate();

            Statement st = con.createStatement();
            ResultSet rs = null;
            if (result != 0) {
                sql = "select comment.id, comment.date, user.nickname, user.image from comment join user on comment.user_id=user.id order by comment.id desc limit 0, 1";
                rs = st.executeQuery(sql);
            }

            while(rs != null && rs.next()) {
                comment.setId(rs.getInt("id"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                comment.setTime(beforeTime(date));
                comment.setNickname(rs.getString("nickname"));
                comment.setProfile(rs.getString("image"));
                result = 1;
            }

            if(rs != null) rs.close();
            st.close();
            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException | ParseException e) {
            e.printStackTrace();
            return 2;
        }
    }

    //댓글 수정
    public int updateComment(int commentId, String comment) {
        String url = Mysql.getInstance().getUrl();
        String sql = "update comment set comment=? where id=?";
        int result = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, comment);
            pst.setInt(2, commentId);
            result = pst.executeUpdate();

            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    //댓글 삭제
    public int deleteComment(int commentId) {
        String url = Mysql.getInstance().getUrl();
        String sql = "delete from comment where id=?";
        int result = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, commentId);
            result = pst.executeUpdate();

            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    //게시물을 삭제할 때 게시물에 달린 모든 댓글을 삭제하는 메소드
    public int deleteComments(int postId) {
        String url = Mysql.getInstance().getUrl();
        String sql = "delete from comment where post_id=?";
        int result = -1;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Mysql.getInstance().getAccount(), Mysql.getInstance().getPassword());
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, postId);
            result = pst.executeUpdate();

            pst.close();
            con.close();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return -1;
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
