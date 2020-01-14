package com.moonsoo.controller;

import com.moonsoo.DAO.FollowDAO;
import com.moonsoo.model.Follow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/follow","/followings", "/followers"})
public class FollowController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }
        super.service(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String followingId_ = request.getParameter("followingId");
        String followedId_ = request.getParameter("followedId");

        int followingId = 0;
        int followedId = 0;
        if (followingId_ != null && !followingId_.equals("") && followedId_ != null && !followedId_.equals("")) {
            followingId = Integer.parseInt(followingId_);
            followedId = Integer.parseInt(followedId_);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        if ((int) request.getSession().getAttribute("id") != followingId) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);//412
            return;
        }

        int result = FollowDAO.getInstance().insert(new Follow(followingId, followedId));

        if(result != -1) {
            JSONObject body = new JSONObject();
            body.put("followingId", followingId);
            body.put("followedId", followedId);
            response.getWriter().print(body.toString());
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String followingId_ = request.getParameter("followingId");
        String followedId_ = request.getParameter("followedId");

        int followingId = 0;
        int followedId = 0;
        if (followingId_ != null && !followingId_.equals("") && followedId_ != null && !followedId_.equals("")) {
            followingId = Integer.parseInt(followingId_);
            followedId = Integer.parseInt(followedId_);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        if ((int) request.getSession().getAttribute("id") != followingId) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);//412
            return;
        }

        int result = FollowDAO.getInstance().delete(new Follow(followingId, followedId));

        if(result != -1) {
            JSONObject body = new JSONObject();
            response.getWriter().print(body.toString());
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String uri = request.getRequestURI();
        if(uri.equals("/followings")) {
            String followingId_ = request.getParameter("followingId");
            int followingId = 0;
            int loginUserId = (int)request.getSession().getAttribute("id");
            if(followingId_ != null && !followingId_.equals("")) {
                followingId = Integer.parseInt(followingId_);
            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//406
                return;
            }

            JSONArray followings = FollowDAO.getInstance().getFollowingList(followingId, loginUserId);
            if(followings == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                return;
            }
            response.getWriter().print(followings.toString());
        }
        else if(uri.equals("/followers")){
            String followedId_ = request.getParameter("followedId");
            int followedId = 0;
            int loginUserId = (int)request.getSession().getAttribute("id");
            if(followedId_ != null && !followedId_.equals("")){
                followedId = Integer.parseInt(followedId_);
            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                return;
            }

            JSONArray followers = FollowDAO.getInstance().getFollowerList(followedId, loginUserId);
            if(followers == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                return;
            }
            response.getWriter().print(followers.toString());
        }
    }
}
