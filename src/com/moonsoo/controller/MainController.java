package com.moonsoo.controller;

import com.moonsoo.DAO.PostDAO;
import com.moonsoo.DTO.PostDTO;
import com.moonsoo.model.Post;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@WebServlet(urlPatterns = {""})
public class MainController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Post> posts = PostDAO.getInstance().getPosts(0);//메인 페이지의 게시물 리스트 get
        request.setAttribute("posts", posts);
        request.getRequestDispatcher("/WEB-INF/view/index.jsp").forward(request, response);
    }


}
