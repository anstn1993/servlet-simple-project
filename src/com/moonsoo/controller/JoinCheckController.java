package com.moonsoo.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(urlPatterns = {"/join/check"})
public class JoinCheckController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");

        try {
            String url = "jdbc:mysql://13.124.105.47:3306/soso";
            String sql = null;


            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, "moonsoo", "Rla933466r!");
            //Statement st = con.createStatement();
            //ResultSet rs = st.executeQuery(sql);
            PreparedStatement pst = null;
            String jsonData = request.getParameter("jsonData");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
            String type = (String)jsonObject.get("type");//중복 검사 요청 데이터 유형
            System.out.println(type);
            String value = (String)jsonObject.get("value");//검사할 데이터
            System.out.println(value);
            if(type.equals("account")) {
                sql = "select*from user where account=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, value);
            }
            else if(type.equals("nickname")) {
                sql = "select*from user where nickname=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, value);
            }
            ResultSet rs = pst.executeQuery();

            if(rs.next()){//데이터가 존재하면 중복하는 데이터
                response.getWriter().println("0");
            }
            else {//데이터가 존재하지 않으면 사용 가능한 데이터
                response.getWriter().println("1");
            }

            rs.close();
            pst.close();
            con.close();
        } catch (ParseException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
