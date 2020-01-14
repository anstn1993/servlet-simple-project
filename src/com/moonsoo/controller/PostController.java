package com.moonsoo.controller;

import com.moonsoo.DAO.CommentDAO;
import com.moonsoo.DAO.PostDAO;
import com.moonsoo.DAO.PostImageDAO;
import com.moonsoo.DTO.PostDTO;
import com.moonsoo.DTO.PostImageDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@MultipartConfig(
        location = "",
        maxFileSize = -1,
        maxRequestSize = -1,
        fileSizeThreshold = 1024
)
@WebServlet(urlPatterns = {"/post", "/upload", "/edit", "/posts", "/user/posts", "/post/detail"})
public class PostController extends HttpServlet {//게시물 관련 컨트롤러

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getSession().getAttribute("id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }
        super.service(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();
        PostDTO postDTO = new PostDTO((int) request.getSession().getAttribute("id"));//사용자 id
        PostImageDTO postImageDTO = new PostImageDTO();
        int imageIndex = 1;
        for (Part part : parts) {
            System.out.println("content-type: " + part.getContentType() + ", key: " + part.getName() + ", size: " + part.getSize());
            if (part.getContentType() != null && part.getContentType().contains("image/") && part.getSize() > 0) {//이미지 파트인 경우
                String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
                String extension = getExtension(part.getContentType());//jpg, png....
                String newFileName = request.getSession().getAttribute("account") + timeStamp + imageIndex + "." + extension;
                imageIndex++;
                part.write(getServletContext().getRealPath("post") + File.separator + newFileName);//이미지 서버의 post디렉토리에 저장
                postImageDTO.add(newFileName);//모델 클래스에 이미지 파일 명 추가
                part.delete();//임시 이미지 데이터 삭제
            } else {//게시글 파트인 경우
                String article = request.getParameter(part.getName());
                postDTO.setArticle(article);
            }
        }

        int result = PostDAO.getInstance().insert(postDTO, postImageDTO);//0:실패, 1:성공, 2:에러
        if (result == 1) {
            response.getWriter().println(result);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("put요청");
        Collection<Part> parts = request.getParts();
        int postId = 0;
        String article = null;
        List<String> newFiles = new ArrayList<>();//새롭게 넘어온 파일명을 담을 리스트
        List<String> transferredExistingFiles = new ArrayList<>();//기존 이미지 파일 명 리스트

        int imageIndex = 1;
        for (Part part : parts) {
            System.out.println("content-type: " + part.getContentType() + ", key: " + part.getName() + ", size: " + part.getSize());
            if (part.getContentType() != null && part.getContentType().contains("image/") && part.getSize() > 0) {//이미지 파트인 경우
                String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
                String extension = getExtension(part.getContentType());//jpg, png....
                String newFileName = request.getSession().getAttribute("account") + timeStamp + imageIndex + "." + extension;
                imageIndex++;
                part.write(getServletContext().getRealPath("post") + File.separator + newFileName);//이미지 서버의 post디렉토리에 저장
                newFiles.add(newFileName);//모델 클래스에 이미지 파일 명 추가
                part.delete();//임시 이미지 데이터 삭제
            } else if (part.getName().equals("postId")) {
                postId = Integer.parseInt(request.getParameter(part.getName()));//게시물 번호 설정
            } else if (part.getName().equals("article")) {//게시글 파트인 경우
                article = request.getParameter(part.getName());
            } else if (part.getName().contains("existingImage")) {
                transferredExistingFiles.add(request.getParameter(part.getName()));//기존 이미지 파일 명 추가
            }
        }

        int result = 0;
        List<String> oldFiles = PostImageDAO.getInstance().getFiles(postId);//기존 파일명을 담을 리스트
        //서버로 넘어온 기존 파일을 제외한 나머지 파일 삭제
        for (String oldFile : oldFiles) {
            int index = transferredExistingFiles.indexOf(oldFile);
            if (index == -1) {//넘어온 파일 명 리스트에 기존 파일이 존재하지 않으면 그 파일은 삭제
                File file = new File(getServletContext().getRealPath("post") + File.separator + oldFile);
                file.delete();
            }
        }
        result = PostDAO.getInstance().update(postId, article, newFiles, transferredExistingFiles);//1:성공, -1:에러

        if (result == -1) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);//200
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();// {/upload, /edit}
        String postId_ = request.getParameter("postId");

        if (uri.equals("/upload")) {//게시물 업로드 페이지 요청인 경우
            request.getRequestDispatcher("WEB-INF/view/upload.jsp").forward(request, response);
        } else if (uri.equals("/edit") && postId_ != null && !postId_.equals("")) {//게시물 수정 페이지 요청인 경우
            int postId = Integer.parseInt(postId_);
            int userId = PostDAO.getInstance().getUserId(postId);//게시물 작성자 id를 반환
            if ((int) request.getSession().getAttribute("id") != userId) {//게시물 작성자가 아닌 사람이 접근하는 경우 차단
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);//412
                return;
            }
            //기존 이미지 파일, 게시글 전달
            request.setAttribute("postId", postId);//게시물 id
            request.setAttribute("article", PostDAO.getInstance().getArticle(postId));//게시글
            request.setAttribute("files", PostImageDAO.getInstance().getFiles(postId));//기존 이미지 파일 명
//            PostImageDAO.getInstance().;
            request.getRequestDispatcher("WEB-INF/view/edit.jsp").forward(request, response);
        } else if (uri.equals("/posts")) {//다음 게시물 요청인 경우
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            int loginUserId = (request.getSession().getAttribute("id") != null) ? (int) request.getSession().getAttribute("id") : 0;
            String lastPostId_ = request.getParameter("lastPostId");
            int lastPostId = 0;
            if (lastPostId_ != null & !lastPostId_.equals("")) {
                lastPostId = Integer.parseInt(lastPostId_);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                return;
            }

            JSONArray posts = PostDAO.getInstance().getNextPosts(loginUserId, lastPostId);//메인 페이지의 게시물 리스트 get
            if (posts == null) {//db에서 정상적인 처리가 이루어지지 않은 경우
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                return;
            } else if (posts.size() == 0) {//게시물이 더 이상 존재하지 않는 경우
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);//204
                return;
            }

            response.getWriter().print(posts);

        } else if (uri.equals("/user/posts")) {//사용자 페이지에서 다음 게시물 요청
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            String userId_ = request.getParameter("id");
            String lastPostId_ = request.getParameter("lastPostId");
            int userId = 0;
            int lastPostId = 0;
            if (userId_ != null && !userId_.equals("") && lastPostId_ != null && !lastPostId_.equals("")) {
                userId = Integer.parseInt(userId_);
                lastPostId = Integer.parseInt(lastPostId_);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                return;
            }

            JSONArray posts = PostDAO.getInstance().getNextPostsInUserPage(userId, lastPostId);
            if (posts == null) {//db에서 정상적인 처리가 이루어지지 않은 경우
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                return;
            } else if (posts.size() == 0) {//게시물이 더 이상 존재하지 않는 경우
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);//204
                return;
            }
            response.getWriter().print(posts.toString());
        } else if (uri.equals("/post/detail")) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            int postId = Integer.parseInt(request.getParameter("postId"));
            JSONObject postData = null;
            if (request.getSession().getAttribute("id") == null) {
                postData = PostDAO.getInstance().getPost(postId);//게시물 데이터를 json object로 리턴
            } else {
                int loginUserId = (int) request.getSession().getAttribute("id");
                postData = PostDAO.getInstance().getPost(loginUserId, postId);//게시물 데이터를 json object로 리턴
            }

            if (postData == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                return;
            }

            System.out.println(postData.toString());
            response.getWriter().print(postData.toString());

        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);//404
            return;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId_ = request.getParameter("userId");
        String postId_ = request.getParameter("postId");

        int userId = 0;
        int postId = 0;

        if (userId_ != null && !userId_.equals("") && postId_ != null && !postId_.equals("")) {
            userId = Integer.parseInt(userId_);
            postId = Integer.parseInt(postId_);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            return;
        }

        if ((int) request.getSession().getAttribute("id") != userId) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }
        //게시물 이미지 파일 삭제
        List<String> images = PostDAO.getInstance().getPostImages(postId);
        for (String image : images) {
            File file = new File(getServletContext().getRealPath("post") + File.separator + image);//파일 객체 생성
            file.delete();//파일 삭제
        }

        //댓글 삭제
        int commentDeleteResult = CommentDAO.getInstance().deleteComments(postId);
        //게시물 삭제
        int postDeleteResult = PostDAO.getInstance().deletePost(postId);

        if (commentDeleteResult != -1 && postDeleteResult != -1) {
            response.getWriter().print(postId);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
        }
    }

    private String getExtension(String contentType) {
        return contentType.split("/")[1];
    }

}
