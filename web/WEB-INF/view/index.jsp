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
    <link rel="stylesheet" href="/css/main.css" type="text/css">
    <script
            src="https://code.jquery.com/jquery-3.4.1.min.js"
            integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">

    <input id="session_userid" type="hidden" value="${sessionScope.id}">

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
                    <li>
                        <c:if test="${sessionScope.account == null}">
                            <a href="/join">회원가입</a>
                        </c:if>
                        <c:if test="${sessionScope.account != null}">
                            <a href="/user?id=${sessionScope.id}">마이페이지</a>
                        </c:if>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <%--  게시물 컨테이너--%>
    <div class="row">
        <c:forEach var="post" items="${posts}" begin="0">
            <div class="col-sm-6 col-md-4">
                <div class="thumbnail btn-default">
                    <a href="/user?id=${post.userId}" style="color:black;">
                        <span><img class="img-circle" src="/profile/${post.profile}" width="30" height="30"
                                   style="margin-bottom: 4px"></span>
                        <span>${post.nickname}</span>
                    </a>
                    <img src="/post/${post.fileName}" alt="게시물 이미지" role="img" width="1000" height="500"
                         style="height: 250px; overflow: auto" role="button" data-toggle="modal"
                         data-target="#post_detail"
                         onclick="openPost('${post.userId}', '${post.postId}', '${post.profile}', '${post.nickname}', '${post.article}', '${post.time}')"/>
                    <div class="caption">
                        <div style="text-align: end; color:#777777">${post.time}</div>
                        <a href="#"><img src="/images/like_unclicked.png" alt="좋아요 버튼" height="30" width="30"
                                         style="display: inline"></a>
                        <a href="#"><img src="/images/comment.png" alt="댓글 버튼" height="30" width="30"
                                         style="display: inline"></a>
                    </div>
                </div>

            </div>
        </c:forEach>
    </div>

    <%--업로드 버튼--%>
    <c:if test="${sessionScope.id != null}">
        <a id="upload" href="/upload" style="position: fixed; right: 20px; bottom: 20px;">
            <img src="/images/upload.png" alt="업로드 버튼" width="50" height="50">
        </a>
    </c:if>


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
                        <div class="col-md-5 col-sm-5 col-xs-5" style="height: 450px; overflow: auto">
                            <ul class="list-inline">
                                <li><span><img id="post_detail_profile" class="img-circle" src=""
                                           width="30" height="30" style="margin-bottom: 4px"></span></li>
                                <li style="margin-right: 10px"><span id="post_detail_nickname"></span></li>
                                <li id="more_btn_box">
                                    <%--js에서 동적으로 추가--%>
                                </li>
                                <li>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span></button>
                                    <div style="border: 0.2px solid #C2C3C1"></div>
                                </li>
                            </ul>

                            <div id="comment_box" class="row">

                            </div>
                            <div style="border: 0.2px solid #C2C3C1"></div>
                        </div>
                        <div class="row">
                            <div class="col-md-4 col-sm-4 col-xs-4" style="padding-left: 0px; padding-right: 0px">
                                <textarea class="form-control" name="comment" rows="2" col="3" placeholder="댓글" style="width: 100%; height: 50px"></textarea>
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
</div>
</body>
<script src="//code.jquery.com/jquery-3.4.1.js"></script>
<script type="text/javascript">




    //모달이 열릴 때 호출
    function openPost(userId, postId, profile, nickname, article, time) {

        $('#more_btn_box').html('');
        var sessionUserId = $('#session_userid').val();
        if(sessionUserId == userId) {//현재 사용자와 게시물 작성자가 일치하는 경우
            $('#more_btn_box').html(
                '<span class="dropdown" style="margin-right: 150px">'+
                    '<img src="/images/more.png" alt="더 보기" width="20" height="20" role="button"'+
                    'data-toggle="dropdown" aria-expanded="true">'+
                    '<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">'+
                        '<li role="presentation"><a role="menuitem" tabindex="-1" onclick="editPost('+postId+')">게시물 수정</a></li>'+
                        '<li role="presentation"><a role="menuitem" tabindex="-1" style="color:red" onclick="deletePost()">게시물 삭제</a></li>'+
                    '</ul>'+
                '</span>');
        }

        $('#post_detail_profile').attr('src', '/profile/' + profile);//프로필 설정
        $('#post_detail_nickname').html(nickname);
        $('#image_box').html('');
        $('#indicator_box').html('');

        $('#article_profile').attr('src', '/profile/' + profile);
        $('#article_nickname').html(nickname);
        $('#article').html(article);

        $('#comment_box').html('');

        if (article != null && article != '') {
            for (var i = 0; i < 30; i++) {
                $('#comment_box').append(
                    '<div class="row" style="margin-right: 0px">' +
                    '<ul class="list-inline" style="margin-left: 20px; margin-top: 10px">' +
                    '<li>' +
                    '<img id="article_profile" class="img-circle" src="/profile/' + profile + '"\n' +
                    'width="30" height="30">' +
                    '</li>' +
                    '<li>' +
                    nickname +
                    '</li>' +
                    '<li>' +
                    article +
                    '</li>' +
                    '</ul>');
            }
        }

        $.ajax({
            url: '/post/detail',
            type: 'GET',
            data: {
                'postId': postId
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
    function editPost(postId) {
        console.log('post id: ' + postId);
        location.href="/edit?postId="+postId;
    }
    $(document).ready(function () {

    });
</script>
</html>