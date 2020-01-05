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
        int postId = Integer.parseInt(request.getParameter("postId"));

        List<String> images = PostDAO.getInstance().getPostImages(postId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("images", images);
        jsonObject.put("size", images.size());
        System.out.println(jsonObject.toString());
        response.getWriter().print(jsonObject.toString());
        /*
        * {images:[0, 0, 0, 0, 0..], size:4}
        *
        *
        * */

    }
}
