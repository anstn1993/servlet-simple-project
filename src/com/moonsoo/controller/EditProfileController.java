package com.moonsoo.controller;

import com.moonsoo.DAO.UserDAO;
import com.moonsoo.util.SHA256;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

@MultipartConfig(
        location = "",
        maxFileSize = -1,
        maxRequestSize = -1,
        fileSizeThreshold = 1024)
@WebServlet("/user/edit")
public class EditProfileController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/editprofile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


//        if((profileFile == null || profileFile.equals("")) && (profileName == null || profileName.equals(""))) {//파일이 안 넘어옴 && 파일 명도 없는 경우
//            if(session.getAttribute("profile") != null && session.getAttribute("profile").equals("")) {//기존 이미지가 있다면
//                //기존 이미지 삭제
//                String formerProfile = (String) session.getAttribute("profile");
//                String profileDir = getServletContext().getRealPath("profile");
//                File file = new File(profileDir+ "/" + formerProfile);
//                file.delete();
//                //세션의 프로필 사진 데이터 null로 업데이트
//                session.setAttribute("profile", null);
//                //TODO:db도 수정
//            }
//        }
//        else if(profileFile != null && !profileFile.equals("")){//파일이 넘어온 경우
//            if(session.getAttribute("profile") != null && session.getAttribute("profile").equals("")) {//기존 이미지가 있다면
//                //기존 이미지 삭제
//                String formerProfile = (String) session.getAttribute("profile");
//                String profileDir = getServletContext().getRealPath("profile");
//                File file = new File(profileDir+ "/" + formerProfile);
//                file.delete();
//            }
//
//            String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//            String newProfileName = request.getSession().getAttribute("account") + timeStamp +".jpg" ;//새로운 프로필 사진 명
//
//            //세션 수정
//        }
//
//
//        if(profile == null || profile.equals("")) {
//            String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//            String newProfileName = request.getSession().getAttribute("account") + timeStamp +".jpg" ;//새로운 프로필 사진 명
//            request.getSession().setAttribute("profile", newProfileName);//세션에 프로필 사진 업데이트
//        }
//        else {
//
//        }


//        String saveDir = getServletContext().getRealPath("profile");//프로필 저장 디렉토리
//        System.out.println(saveDir);
//        int maxSize = 10*1024*1024;//3mb
//        String encoding = "UTF-8";
//        MultipartRequest multipartRequest = new MultipartRequest(request, saveDir, maxSize, encoding, new FileUploadRename());
//        System.out.println("파일 명: " + multipartRequest.getOriginalFileName("profile"));
//        System.out.println("파일 타입: " + multipartRequest.getContentType("profile"));



    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        String contentType = request.getContentType();

        HashMap<String, String> userData = new HashMap<>();//수정된 사용자 데이터를 저장할 해시 맵

        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            Collection<Part> parts = request.getParts();

            String newProfileName = null;

            for (Part part : parts) {
                System.out.println("파라미터 명: " + part.getName() + ", contentType: " + part.getContentType() + ", size: " + part.getSize() + "bytes");

                if (part.getContentType() != null && part.getContentType().contains("image/") && part.getSize() > 0) {
                    System.out.println(part.getHeader("content-Disposition"));
                    String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
                    String extension =  getExtension(part.getContentType());
                    newProfileName = request.getSession().getAttribute("account") + timeStamp +"."+ extension;//새로운 프로필 사진 명
                    System.out.println("업로드 파일 명: "+ newProfileName);
                    part.write(request.getServletContext().getRealPath("profile")+ File.separator+newProfileName);
                    part.delete();
                    userData.put("profile", newProfileName);//userData에 프로필 사진 추가
                }
                else {
                    System.out.println("key: " + part.getName()+", value: " + request.getParameter(part.getName()));
                    userData.put(part.getName(), request.getParameter(part.getName()));//사용자 정보 설정
                    if(part.getName().equals("id")) {//id인 경우에는 value를 숫자로
                        session.setAttribute(part.getName(), Integer.parseInt(request.getParameter(part.getName())));//세션 업데이트
                    }
                    else {
                        session.setAttribute(part.getName(), request.getParameter(part.getName()));//세션 업데이트
                    }
                    if(part.getName().equals("email")) {//이메일 데이터 part인 경우 userData에 email hash값도 추가
                        userData.put("emailHash", SHA256.getSha256(request.getParameter(part.getName())));
                    }
                }
            }


            for(String key : userData.keySet()) {
                System.out.println("key : "+ key + ", value: " + userData.get(key));
            }

            if(session.getAttribute("profile") != null && newProfileName != null) {//기존 프로필 사진O && 새로운 프로필 사진O
                File file = new File(getServletContext().getRealPath("profile")+File.separator+session.getAttribute("profile"));
                file.delete();//기존 프로필 사진 제거
                session.setAttribute("profile", newProfileName);//세션에 새로운 프로필 사진 명으로 변경
            }

            if(session.getAttribute("profile") != null
                    && userData.get("profileName") != null
                    && userData.get("profileName").equals(session.getAttribute("profile"))){//기존 프로필 사진에서 수정하지 않은 경우
                userData.put("profile", (String)session.getAttribute("profile"));
            }

            if(session.getAttribute("profile") != null && newProfileName == null && userData.get("profileName") == null) {//프로필 사진을 지운 경우
                File file = new File(getServletContext().getRealPath("profile")+File.separator+session.getAttribute("profile"));
                file.delete();//기존 프로필 사진 제거
                session.setAttribute("profile", null);//세션에서 프로필 사진 명 제거
            }

            if(session.getAttribute("profile") == null && newProfileName != null) {//프로필 사진이 없다가 생긴 경우
                session.setAttribute("profile", newProfileName);//세션에 새로운 프로필 사진 명 등록
            }

//

            int result = UserDAO.getInstance().updateUserData(userData);
            if(result == 0 || result == 2) {
                response.getWriter().println("0");
            }
            else {
                response.getWriter().println("1");
            }
        }
    }

    //확장자 return
    private String getExtension(String contentType) {
//        for(String cd : header.split(";")) {
//            System.out.println(cd);
//            if(cd.trim().startsWith("filename")){
//                int index = cd.lastIndexOf(".");
//                return cd.substring(index+1).replace("\"", "");
//            }
//        }
        return contentType.split("/")[1];
    }


}
