package com.moonsoo.controller;

import com.moonsoo.DAO.CommentDAO;
import com.moonsoo.model.Comment;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

@WebServlet("/comment")
public class CommentController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("id") == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);//로그인을 하지 않았기 때문에 403 에러를 던져준다.
            return;
        }
        super.service(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        int postId = Integer.parseInt(request.getParameter("postId"));
        String commentStr = request.getParameter("comment");

        Comment comment = new Comment(postId, (int) request.getSession().getAttribute("id"), commentStr);
        int result = CommentDAO.getInstance().insert(comment);

        if (result != 1) {//db에 저장한 후 댓글 데이터를 가져오는 데 실패한 경우
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        JSONObject commentData = new JSONObject();
        commentData.put("id", comment.getId());
        commentData.put("userId", comment.getUserId());
        commentData.put("postId", comment.getPostId());
        commentData.put("comment", comment.getComment());
        commentData.put("nickname", comment.getNickname());
        commentData.put("profile", comment.getProfile());
        commentData.put("time", comment.getTime());
        response.getWriter().print(commentData.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commentId_ = request.getParameter("commentId");
        String userId_ = request.getParameter("userId");
        String comment = request.getParameter("comment");

        int commentId = 0;
        int userId = 0;

        if(commentId_ != null && !commentId_.equals("") && userId_ != null && !userId_.equals("") && comment != null && !comment.equals("")) {//유효한 쿼리 스트링
            commentId = Integer.parseInt(commentId_);
            userId = Integer.parseInt(userId_);
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);//406
            return;
        }

        if(request.getSession().getAttribute("id") != null && userId != (int)request.getSession().getAttribute("id")) {//다른 사람의 댓글 수정 요청을 하는 경우를 차단
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);//406
            return;
        }

        int result = CommentDAO.getInstance().updateComment(commentId, comment);
        if(result == 2 || result == 0) {//db에서 처리가 잘 되지 않은 경우
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(commentId);

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");


        String commentId_ = request.getParameter("commentId");
        String userId_ = request.getParameter("userId");

        System.out.println("commentId: " + commentId_);
        System.out.println("userId: " + userId_);
        int commentId = 0;
        int userId = 0;
        if(userId_ != null && !userId_.equals("") && commentId_ != null && !commentId_.equals("")) {//유효한 query string
            commentId = Integer.parseInt(commentId_);
            userId = Integer.parseInt(userId_);
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);//406
            return;
        }

        if(request.getSession().getAttribute("id") != null && userId != (int)request.getSession().getAttribute("id")) {//다른 사람의 댓글 삭제 요청을 하는 경우를 차단
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);//406
            return;
        }

        int result = CommentDAO.getInstance().deleteComment(commentId);
        if(result == 2 || result == 0) {//db에서 처리가 잘 되지 않은 경우
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(commentId);
    }
}
