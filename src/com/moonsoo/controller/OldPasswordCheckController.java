package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/edit/oldpassword/check")
public class OldPasswordCheckController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            int id = Integer.parseInt(request.getParameter("id"));
            String oldPassword = request.getParameter("oldPassword");
            int result = UserDAO.getInstance().checkOldPassword(id, oldPassword);//0: 잘못된 비밀번호, 1: 유효한 비밀번호 2: 에러
            response.getWriter().println(result);
    }
}
