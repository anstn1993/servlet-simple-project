package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/user/withdrawal")
public class WithdrawalController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }
        super.service(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/view/withdrawal.jsp").forward(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId_ = request.getParameter("userId");
        String profile = request.getParameter("profile");
        int userId = 0;
        if(userId_ != null && !userId_.equals("")) {
            userId = Integer.parseInt(userId_);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        if(userId != (int)request.getSession().getAttribute("id")) {//넘어온 사용자 id와 세션 id가 다른 경우
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);//412
        }
        String profilePath = getServletContext().getRealPath("profile") + File.separator;
        String postImagePath = getServletContext().getRealPath("post") + File.separator;
        int result = UserDAO.getInstance().withdrawl(userId, profile, profilePath, postImagePath);

        if(result == -1) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            return;
        }

        request.getSession().invalidate();//세션 정보 삭제
        response.setStatus(HttpServletResponse.SC_OK);//200

    }
}
