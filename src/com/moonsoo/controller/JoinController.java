package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;
import com.moonsoo.DTO.UserDTO;
import com.moonsoo.util.Naver;
import com.moonsoo.util.SHA256;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet(urlPatterns = {"/join"})
public class JoinController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/join.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String account = request.getParameter("account");
        String password = SHA256.getSha256(request.getParameter("password"));//sha-256 알고리즘
        String name = request.getParameter("name");
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        String emailHash = SHA256.getSha256(request.getParameter("email"));//sha-256 알고리즘

        boolean result = UserDAO.getInstance().insert(new UserDTO(account, password, name, nickname, email, emailHash));
        if (result) {

//            response.sendRedirect("/join/send-auth-email");//인증 이메일 전송 api로 이동
            String host = "http://13.124.105.47:8080/";
//            String host = "http://localhost:8080/";
            String from = "rlarpdlcm@naver.com";
            String to = email;
            String subject = "soso community 이용을 위한 인증 메일 입니다.";
            String content = "다음 링크에 접속하여 이메일 인증을 해주세요."
                    + "<a href ='" + host + "join/email-check?account=" + account + "&code=" + emailHash + "'> 이메일 인증하기 </a>";

            Properties prop = new Properties();
//            prop.put("mail.smtp.user", from);
//            prop.put("mail.smtp.host", "smtp.gmail.com");
//            prop.put("mail.smtp.port", 465);
//            prop.put("mail.smtp.starttls.enable", "true");
//            prop.put("mail.smtp.auth", "true");
//            prop.put("mail.smtp.debug", "true");
//            prop.put("mail.smtp.socketFactory.port", 465);
//            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            prop.put("mail.smtp.socketFactory.fallback", "false");
//            prop.put("mail.smtp.ssl.enable", "true");
//            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            prop.put("mail.smtp.host", "smtp.naver.com");
            prop.put("mail.smtp.port", 465);
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.trust", "smtp.naver.com");


            try {
                Authenticator auth = new Naver();
                Session session = Session.getInstance(prop, auth);
                session.setDebug(true);
                MimeMessage msg = new MimeMessage(session);//메세지 객체
                Address fromAddr = new InternetAddress(from);
                msg.setFrom(fromAddr);
                Address toAddr = new InternetAddress(to);
                msg.addRecipient(Message.RecipientType.TO, toAddr);
                msg.setSubject(subject);
                msg.setContent(content, "text/html;charset=UTF8");//setText로 보내면 모든 문자열을 다 텍스트로 인식
                Transport.send(msg);//이메일 전송

                //이메일 인증 안내 페이지로 forward
                request.getRequestDispatcher("/WEB-INF/view/notifyemailcheck.jsp").forward(request, response);

            } catch (Exception e) {
                e.printStackTrace();
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println("<script>");
                response.getWriter().println("alert('오류가 발생했습니다.');");
                response.getWriter().println("history.back();");
                response.getWriter().println("</script>");
                return;
            }

        } else {
//            request.setAttribute("joinResult", false);
//            request.getRequestDispatcher("/WEB-INF/view/join.jsp").forward(request, response);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('회원가입 실패');");
            response.getWriter().println("history.back();");
            response.getWriter().println("</script>");
        }
    }
}
