package com.moonsoo.controller;

import com.moonsoo.DAO.PostDAO;
import com.moonsoo.model.Post;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {""})
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Post> posts = null;
        int loginUserId = (request.getSession().getAttribute("id") != null) ? (int) request.getSession().getAttribute("id") : 0;
        posts = PostDAO.getInstance().getPosts(loginUserId, 0);//메인 페이지의 게시물 리스트 get
        request.setAttribute("posts", posts);
        request.setAttribute("lastPostId", posts.get(posts.size()-1).getPostId());
        request.getRequestDispatcher("/WEB-INF/view/index.jsp").forward(request, response);
    }
}
