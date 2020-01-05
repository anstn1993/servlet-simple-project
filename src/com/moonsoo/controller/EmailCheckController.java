package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;
import com.moonsoo.DTO.UserDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/join/email-check"})
public class EmailCheckController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String account = request.getParameter("account");
        String code = request.getParameter("code");
        if(code == null || code.equals("") || account == null || account.equals("")) {
            //TODO: exception
            return;
        }

        int authResult = UserDAO.getInstance().checkEmailAuthentication(account, code);
        if(authResult == 0) {//비인증 상태
            //TODO: 인증 성공
            UserDTO userDTO = UserDAO.getInstance().getUserData(account);
            if(userDTO != null) {
                //세션에 사용자 등록 후
                request.getSession().setAttribute("id", userDTO.getId());
                request.getSession().setAttribute("account", userDTO.getAccount());
                request.getSession().setAttribute("password", userDTO.getPassword());
                request.getSession().setAttribute("name", userDTO.getName());
                request.getSession().setAttribute("nickname", userDTO.getNickname());
                request.getSession().setAttribute("email", userDTO.getEmail());
                request.getSession().setAttribute("profile", userDTO.getProfile());
                request.getSession().setAttribute("introduce", userDTO.getIntroduce());
                //메인 화면으로 리다이렉트
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>");
                response.getWriter().println("alert('인증을 완료하셨습니다.');");
                response.getWriter().println("location.href='/'");
                response.getWriter().println("</script>");
            }
            else {
                //TODO:에러
                //메인 화면으로 리다이렉트
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>");
                response.getWriter().println("alert('오류 발생');");
                response.getWriter().println("location.href='/'");
                response.getWriter().println("</script>");
            }
        }
        else if(authResult == 1) {//인증 상태
            //TODO: 이미 인증된 이메일 입니다.
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('이미 인증하셨습니다. 로그인 하세요.');");
            response.getWriter().println("location.href='/login'");
            response.getWriter().println("</script>");
        }
        else {//에러
            //TODO: 에러 발생
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('오류 발생');");
            response.getWriter().println("location.href='/'");
            response.getWriter().println("</script>");
        }
    }
}
