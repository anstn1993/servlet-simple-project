package com.moonsoo.controller;

import com.moonsoo.DAO.LikeDAO;
import com.moonsoo.model.Like;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/like")
public class LikeController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(request.getSession().getAttribute("id") == null) {//로그인이 안 된 경우
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }

        super.service(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String postId_ = request.getParameter("postId");
        String userId_ = request.getParameter("userId");
        int postId = 0;
        int userId = 0;
        if(postId_ != null && !postId_.equals("") && userId_ != null && !userId_.equals("")) {
            postId = Integer.parseInt(postId_);
            userId = Integer.parseInt(userId_);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        if((int)request.getSession().getAttribute("id") != userId) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }

        int result = LikeDAO.getInstance().insert(new Like(postId, userId));
        if(result != 0 && result != -1) {
            response.getWriter().print(postId);
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            return;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String loginUserId_ = request.getParameter("loginUserId");
        String postId_ = request.getParameter("postId");
        int postId = 0;
        int loginUserId = 0;
        if(postId_ != null && !postId_.equals("") && loginUserId_ != null && !loginUserId_.equals("")) {
            postId = Integer.parseInt(postId_);
            loginUserId = Integer.parseInt(loginUserId_);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        JSONArray likeList = LikeDAO.getInstance().getLikeList(loginUserId, postId);

        if(likeList != null) {
            response.getWriter().print(likeList.toString());
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            return;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String postId_ = request.getParameter("postId");
        String userId_ = request.getParameter("userId");
        int postId = 0;
        int userId = 0;
        if(postId_ != null && !postId_.equals("") && userId_ != null && !userId_.equals("")) {
            postId = Integer.parseInt(postId_);
            userId = Integer.parseInt(userId_);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        if((int)request.getSession().getAttribute("id") != userId) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }

        int result = LikeDAO.getInstance().delete(new Like(postId, userId));
        if(result != 0 && result != -1) {
            response.getWriter().print(postId);
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            return;
        }
    }
}
