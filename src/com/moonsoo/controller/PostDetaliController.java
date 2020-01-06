package com.moonsoo.controller;

import com.moonsoo.DAO.PostDAO;
import com.moonsoo.DTO.PostDTO;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/post/detail")
public class PostDetaliController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int postId = Integer.parseInt(request.getParameter("postId"));

        JSONObject postData = PostDAO.getInstance().getPost(postId);//게시물 데이터를 json object로 리턴
        if(postData != null) {
            System.out.println(postData.toString());
            response.getWriter().print(postData.toString());
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
