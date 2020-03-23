package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;
import com.moonsoo.DTO.UserDTO;
import com.moonsoo.util.ConnectionPool;
import com.moonsoo.util.Naver;
import com.moonsoo.util.SHA256;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet(urlPatterns = {"/join", "/join/check", "/join/notify-emailauth"})
public class JoinController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.equals("/join")) {
            request.getRequestDispatcher("/WEB-INF/view/join.jsp").forward(request, response);
        } else {// '/join/notify-emailauth'
            request.getRequestDispatcher("/WEB-INF/view/notifyemailcheck.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/join")) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            String account = request.getParameter("account");
            String password = SHA256.getSha256(request.getParameter("password"));//sha-256 알고리즘
            String name = request.getParameter("name");
            String nickname = request.getParameter("nickname");
            String email = request.getParameter("email");
            String emailHash = SHA256.getSha256(request.getParameter("email"));//sha-256 알고리즘

            int id = UserDAO.getInstance().insert(new UserDTO(account, password, name, nickname, email, emailHash));//유저 id 값 리턴
            if (id != 0) {
                int sendResult = sendAuthEmail(account, email, emailHash);//인증 메일 전송

                if(sendResult == 1) {//이메일이 잘 전송된 경우
                    response.setStatus(HttpServletResponse.SC_CREATED);//201
                }
                else {//이메일 전송에 실패한 경우
                    UserDAO.getInstance().delete(id);//추가된 사용자를 다시 삭제
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                }

            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            }
        } else {// '/join/check'
            response.setCharacterEncoding("UTF-8");
            String type = request.getParameter("type");
            String value = request.getParameter("value");
            if(type != null && !type.equals("") && value != null && !value.equals("")) {
                int result = UserDAO.getInstance().checkValidation(type, value);
                if(result != -1) {
                    response.getWriter().println(result);
                }
                else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                    return;
                }
            }else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                return;
            }
        }
    }

    public int sendAuthEmail(String account, String email, String emailHash) {

        int result = 0;
        try {
            //        String host = "http://13.124.105.47:8080/";
            String host = "http://localhost:8080/";
            String from = "rlarpdlcm@naver.com";
            String to = email;
            String subject = "soso community 이용을 위한 인증 메일 입니다.";
            String content = "다음 링크에 접속하여 이메일 인증을 해주세요."
                    + "<a href ='" + host + "join/email-check?account=" + account + "&code=" + emailHash + "'> 이메일 인증하기 </a>";

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.naver.com");
            prop.put("mail.smtp.port", 465);
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.trust", "smtp.naver.com");


            Authenticator auth = new Naver();
            Session session = Session.getInstance(prop, auth);
            session.setDebug(true);
            MimeMessage msg = new MimeMessage(session);//메세지 객체
            Address fromAddr = null;
            fromAddr = new InternetAddress(from);
            msg.setFrom(fromAddr);
            Address toAddr = new InternetAddress(to);
            msg.addRecipient(Message.RecipientType.TO, toAddr);
            msg.setSubject(subject);
            msg.setContent(content, "text/html;charset=UTF8");//setText로 보내면 모든 문자열을 다 텍스트로 인식
            Transport.send(msg);//이메일 전송
            result = 1;
        } catch (MessagingException e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }
}
