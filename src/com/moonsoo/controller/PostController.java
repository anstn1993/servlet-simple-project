package com.moonsoo.controller;

import com.moonsoo.DAO.PostDAO;
import com.moonsoo.DAO.PostImageDAO;
import com.moonsoo.DTO.PostDTO;
import com.moonsoo.DTO.PostImageDTO;

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
@WebServlet(urlPatterns = {"/post", "/upload", "/edit"})
public class PostController extends HttpServlet {//게시물 관련 컨트롤러

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getSession().getAttribute("id") == null) {
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
        response.getWriter().println(result);
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
            } else if(part.getName().equals("postId")) {
                postId = Integer.parseInt(request.getParameter(part.getName()));//게시물 번호 설정
            } else if(part.getName().equals("article")){//게시글 파트인 경우
                article = request.getParameter(part.getName());
            } else if(part.getName().contains("existingImage")) {
                transferredExistingFiles.add(request.getParameter(part.getName()));//기존 이미지 파일 명 추가
            }
        }

        int result = 0;
//        if(newFiles.size() != 0) {//새로운 파일이 넘어온 경우
            List<String> oldFiles = PostImageDAO.getInstance().getFiles(postId);//기존 파일명을 담을 리스트
            //서버로 넘어온 기존 파일을 제외한 나머지 파일 삭제
            for(String oldFile : oldFiles) {
                int index = transferredExistingFiles.indexOf(oldFile);
                if(index == -1) {//넘어온 파일 명 리스트에 기존 파일이 존재하지 않으면 그 파일은 삭제
                    File file = new File(getServletContext().getRealPath("post") + File.separator + oldFile);
                    file.delete();
                }
            }
            result = PostDAO.getInstance().update(postId, article, newFiles, transferredExistingFiles);//0:실패, 1:성공, 2:에러
//        }
//        else {//새로운 파일이 안 넘어온 경우
//            result = PostDAO.getInstance().update(postId, article);//0:실패, 1:성공, 2:에러
//        }
        response.getWriter().println(result);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();// {/upload, /edit}
        String resource = uri.substring(uri.indexOf("/") + 1);
        System.out.println("resource: " + resource);
        String postId_ = request.getParameter("postId");

        if (resource.equals("upload")) {//게시물 업로드 페이지 요청인 경우
            request.getRequestDispatcher("WEB-INF/view/upload.jsp").forward(request, response);
        } else if (resource.equals("edit") && postId_ != null && !postId_.equals("")) {//게시물 수정 페이지 요청인 경우
            int postId = Integer.parseInt(postId_);
            int userId = PostDAO.getInstance().getUserId(postId);//게시물 작성자 id를 반환
            if ((int) request.getSession().getAttribute("id") != userId) {//게시물 작성자가 아닌 사람이 접근하는 경우 차단
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>");
                response.getWriter().println("alert('잘못된 접근입니다.');");
                response.getWriter().println("history.back()");
                response.getWriter().println("</script>");
                return;
            }
            //기존 이미지 파일, 게시글 전달
            request.setAttribute("postId", postId);//게시물 id
            request.setAttribute("article", PostDAO.getInstance().getArticle(postId));//게시글
            request.setAttribute("files", PostImageDAO.getInstance().getFiles(postId));//기존 이미지 파일 명
//            PostImageDAO.getInstance().;
            request.getRequestDispatcher("WEB-INF/view/edit.jsp").forward(request, response);
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('잘못된 접근입니다.');");
            response.getWriter().println("history.back()");
            response.getWriter().println("</script>");
            return;
        }
    }



    private String getExtension(String contentType) {
        return contentType.split("/")[1];
    }

}
