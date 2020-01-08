package com.moonsoo.controller;

import com.moonsoo.DAO.FollowDAO;
import com.moonsoo.DAO.PostDAO;
import com.moonsoo.DAO.UserDAO;
import com.moonsoo.model.Post;
import com.moonsoo.model.User;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/user")
public class UserPageController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("id") == null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('로그인 후 이용하세요.');");
            response.getWriter().println("location.href = '/login'");
            response.getWriter().println("</script>");
            return;
        }
        super.service(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id_ = request.getParameter("id");
        int id = 0;
        if(id_ != null && !id_.equals("")){
            id = Integer.parseInt(id_);
        }

        if(id <= 1) {//1번은 관리자 id
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('유효하지 않은 접근 입니다.');");
            response.getWriter().println("location.href = '/'");
            response.getWriter().println("</script>");
            return;
        }

        User user = UserDAO.getInstance().getUserData(id);
        List<Post> posts = PostDAO.getInstance().getUserPosts(id, 0);
        int followingId = (int)request.getSession().getAttribute("id");
        boolean followStatus = FollowDAO.getInstance().getFollowStatus(followingId, id);

        request.setAttribute("id", id);
        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        request.setAttribute("size", posts.size());
        request.setAttribute("followStatus", followStatus);
        request.getRequestDispatcher("/WEB-INF/view/userpage.jsp").forward(request, response);
    }
}
