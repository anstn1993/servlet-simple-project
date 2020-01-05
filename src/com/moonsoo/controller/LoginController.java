package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;
import com.moonsoo.DTO.UserDTO;
import com.moonsoo.util.SHA256;
import org.apache.axis.session.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        if(session.getAttribute("account") != null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('이미 로그인 상태 입니다.');");
            response.getWriter().println("history.back()");
            response.getWriter().println("</script>");
        }
        else{
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String account = request.getParameter("account");
        String password = SHA256.getSha256(request.getParameter("password"));
        int result = UserDAO.getInstance().loginCheck(account, password);//0:계정 정보 불일치, 1:로그인 성공, 2:이메일 미인증, 3:에러

        if(result == 0) {//유효하지 않은 경우
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('아이디와 비밀번호를 확인해주세요.');");
            response.getWriter().println("location.href = '/login';");
            response.getWriter().println("</script>");
        }
        else if(result == 1){//유효한 아이디, 비밀번호인 경우
            UserDTO userDTO = UserDAO.getInstance().getUserData(account);
            //세션에 사용자 등록
            request.getSession().setAttribute("id", userDTO.getId());
            request.getSession().setAttribute("account", userDTO.getAccount());
            request.getSession().setAttribute("password", userDTO.getPassword());
            request.getSession().setAttribute("name", userDTO.getName());
            request.getSession().setAttribute("nickname", userDTO.getNickname());
            request.getSession().setAttribute("email", userDTO.getEmail());
            request.getSession().setAttribute("profile", userDTO.getProfile());
            request.getSession().setAttribute("introduce", userDTO.getIntroduce());
            response.sendRedirect("/");

        }
        else if(result ==2) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('이메일 인증 후 로그인 해주세요.');");
            response.getWriter().println("location.href = '/login';");
            response.getWriter().println("</script>");
        }
        else {
            response.sendError(404);
        }
    }
}
