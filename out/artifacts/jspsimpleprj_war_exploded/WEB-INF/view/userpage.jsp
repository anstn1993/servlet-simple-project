<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title> soso 커뮤니티</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/navigation.css" type="text/css">
    <script
            src="https://code.jquery.com/jquery-3.4.1.min.js"
            integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <style>
        .divider {
            border: 0.25px solid black;
        }
    </style>
</head>
<body>
<div class="container">

    <nav>

    </nav>
    <nav class="navbar navbar-inverse">
        <div class="container-fluid">
            <div class="navbar-header"><!--네비게이션 헤더-->
                <button type="button" class="collapsed navbar-toggle" data-toggle="collapse" data-target="#nav_menu"
                        aria-expanded="false">
                    <span class="sr-only">Toggle Navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="/" class="navbar-brand">
                    soso community
                </a>
            </div>
            <div class="collapse navbar-collapse" id="nav_menu">
                <ul class="nav navbar-nav navbar-right">
                    <li>
                        <c:if test="${sessionScope.account == null}">
                            <a href="/login">로그인</a>
                        </c:if>
                        <c:if test="${sessionScope.account != null}">
                            <a href="/logout">로그아웃</a>
                        </c:if>
                    </li>

                    <c:if test="${sessionScope.account == null}">
                        <li>
                            <a href="/join">회원가입</a>
                        </li>
                    </c:if>
                    <c:if test="${sessionScope.id != null}">
                        <c:if test="${requestScope.id == sessionScope.id}">
                            <li class="active">
                                <a href="/user?id=${sessionScope.id}">마이페이지</a>
                            </li>
                        </c:if>
                        <c:if test="${requestScope.id != sessionScope.id}">
                            <li>
                                <a href="/user?id=${sessionScope.id}">마이페이지</a>
                            </li>
                        </c:if>
                    </c:if>

                </ul>
            </div>
        </div>
    </nav>
    <div class="col-md-3 col-sm-4">
        <c:if test="${user.profile == null}">
            <img src="/images/profile.png" alt="프로필 사진" class="img-circle" width="200" height="200">
        </c:if>
        <c:if test="${user.profile != null}">
            <img src="/profile/${user.profile}" alt="프로필 사진" class="img-circle" width="200" height="200">
        </c:if>
    </div>
    <div class="col-md-6 col-sm-6">
        <ul class="list-inline">
            <li>
                <h3>${user.nickname}(${user.name})</h3>
            </li>
            <li>
                <c:if test="${sessionScope.id == user.id}">
                    <a href="user/edit" class="btn btn-default">프로필 편집</a>
                </c:if>
            </li>
        </ul>
        <p>${user.introduce}</p>
    </div>
</div>
<div class="container">
    <div class="row">
        <c:forEach var="post" items="${posts}">
            <div class="col-sm-6 col-md-4">
                <a href="#" class="thumbnail">
                    <img src="/post/${post.fileName}" alt="게시물 이미지" width="500" height="500"
                         style="height: 300px; overflow: auto" role="button"
                         data-toggle="modal" data-target="#post_detail" onclick="openPost(${post.postId})">
                </a>
            </div>
        </c:forEach>
    </div>

</div>

<!-- 게시물 상세보기 모달 -->
<div class="modal fade" id="post_detail" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg" style="width: 70%; height: 50%">
        <div class="modal-content">
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-7 col-sm-7 col-xs-7">
                        <div id="post_image_slide" class="carousel slide" data-ride="carousel"
                             data-interval="false">
                            <!-- Indicators -->
                            <ol id="indicator_box" class="carousel-indicators">
                                <%-- 동적으로 추가--%>
                            </ol>

                            <!-- Wrapper for slides -->
                            <div id="image_box" class="carousel-inner" role="listbox">
                                <%--게시물 이미지 동적으로 추가--%>
                            </div>

                            <!-- Controls -->
                            <a class="left carousel-control" href="#post_image_slide" role="button"
                               data-slide="prev">
                                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                                <span class="sr-only">Previous</span>
                            </a>
                            <a class="right carousel-control" href="#post_image_slide" role="button"
                               data-slide="next">
                                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                                <span class="sr-only">Next</span>
                            </a>
                        </div>
                    </div>
                    <div class="col-md-5 col-sm-5 col-xs-5" style="height: 400px; overflow: auto">
                        <ul class="list-inline">
                            <li><span><img id="post_detail_profile" class="img-circle" src="/profile/${user.profile}"
                                           width="30" height="30" style="margin-bottom: 4px"></span></li>
                            <li style="margin-right: 10px"><span id="post_detail_nickname">${user.nickname}</span></li>
                            <li>
                                <c:if test="${sessionScope.id == user.id}">
                                    <span class="dropdown" style="margin-right: 160px">
                                        <img src="/images/more.png" alt="더 보기" width="20" height="20" role="button"
                                             data-toggle="dropdown" aria-expanded="true">
                                        <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
                                            <li role="presentation"><a role="menuitem" tabindex="-1" onclick="editPost()">게시물 수정</a></li>
                                            <li role="presentation"><a role="menuitem" tabindex="-1" style="color:red" onclick="deletePost()">게시물 삭제</a></li>
                                        </ul>
                                    </span>
                                </c:if>
                            </li>
                            <li>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span></button>
                            </li>
                        </ul>


                        <div style="border: 0.2px solid #C2C3C1"></div>
                        <div id="comment_box" class="row">

                        </div>
                        <div style="border: 0.2px solid #C2C3C1"></div>
                    </div>

                    <div class="row">
                        <div class="row">asdf</div>
                        <div class="col-md-4 col-sm-4 col-xs-4" style="padding-left: 0px; padding-right: 0px">
                            <textarea class="form-control" name="comment" rows="2" col="3" placeholder="댓글"
                                      style="width: 100%; height: 50px"></textarea>
                        </div>
                        <div class="col-md-1 col-sm-1 col-xs-1" style="padding-left: 0px; padding-right: 0px">
                            <button class="btn btn-default" style=" width:70%; height: 50px">등록</button>
                        </div>
                    </div>
                </div>


            </div>
        </div>
    </div>
</div>


</body>
<script src="//code.jquery.com/jquery-3.4.1.js"></script>
<script type="text/javascript">

    var postId;//게시물 수정이나 삭제를 할 때 사용할 게시물 번호

    //모달이 열릴 때 호출
    function openPost(id) {
        postId = id;
        $('#image_box').html('');
        $('#indicator_box').html('');

        $.ajax({
            url: '/post/detail',
            type: 'GET',
            data: {
                'postId': id
            }
        }).done(function (result) {
            var data = JSON.parse(result);
            console.log(data);
            console.log(data.size);
            for (var i = 0; i < data.size; i++) {
                console.log("반복문 실행");
                var imageName = data.images[i];
                if (i == 0) {
                    $('#image_box').append('<div class="item active"><img src="/post/' + imageName + '" alt="이미지" style="width: 100%; height: 500px"></div>');
                    $('#indicator_box').append('<li data-target="#post_image_slide" data-slide-to="' + i + '" class="active"></li>');
                } else {
                    $('#image_box').append('<div class="item"><img src="/post/' + imageName + '" alt="이미지" width="2000" style="width: 100%; height: 500px"></div>');
                    $('#indicator_box').append('<li data-target="#post_image_slide" data-slide-to="' + i + '"></li>');
                }
            }

        }).fail(function () {

        });
    }

    //게시물 수정 페이지로 이동
    function editPost() {
        console.log('post id: ' + postId);
        location.href="/edit?postId="+postId;
    }

    //게시물 삭제 페이지로 이동
    function deletePost() {
        console.log('post id: ' + postId);
    }

    $(document).ready(function () {

    });
</script>
</html>