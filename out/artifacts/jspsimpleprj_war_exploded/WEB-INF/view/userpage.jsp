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

    <input id="session_userid" type="hidden" value="${sessionScope.id}">
    <input id="page_owner_id" type="hidden" value="${id}">

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
    <div class="col-md-3 col-sm-3">
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
                <c:if test="${sessionScope.id != user.id && followStatus}">
                    <button id="follow_btn" class="btn btn-default" value="true">팔로잉</button>
                </c:if>
                <c:if test="${sessionScope.id != user.id && !followStatus}">
                    <button id="follow_btn" class="btn btn-info" value="false">팔로우</button>
                </c:if>
            </li>
        </ul>
        <ul class="list-inline">
            <li style="width: 15%">
                게시물 <h4 style="text-align: center">${size}</h4>
            </li>
            <li role="button" style="width: 15%">
                팔로워 <h4>100</h4>
            </li>
            <li role="button" style="width: 15%">
                팔로잉 <h4>100</h4>
            </li>
        </ul>
        <p style="word-break: break-all">${user.introduce}</p>
    </div>
</div>
<div class="container">
    <div class="row">
        <c:forEach var="post" items="${posts}">
            <div id="post${post.postId}" class="col-sm-6 col-md-4">
                <a href="#" class="thumbnail">
                    <img src="/post/${post.fileName}" alt="게시물 이미지" width="500" height="500"
                         style="height: 300px; overflow: auto" role="button"
                         data-toggle="modal" data-target="#post_detail" onclick="openPost('${post.userId}', '${post.postId}', '${user.nickname}', '${user.profile}', '${post.article}', '${post.time}')">
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
            <div class="modal-body" style="padding: 0px">
                <div class="container" style="padding:0px; width: 100%">
                    <div class="row">
                        <!--좌측 이미지 박스-->
                        <div class="col-sm-5 con-sm-5" style="width: 60%;padding: 0px">
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

                        <!--우측 박스-->
                        <div class="col-sm-5 col-md-5" style="height: 500px; width: 40%; padding: 0px">
                            <div class="container" style="padding: 0px; width: 100%">
                                <!--헤더(프로필 사진, 작성자 닉네임, 더 보기 버튼, 종료 버튼)-->
                                <div class="row" style="margin-top:10px; margin-left: 5px">
                                    <ul class="list-inline">
                                        <!--프로필 사진-->
                                        <li style="width: 10%"><span><img id="post_detail_profile" class="img-circle"
                                                                          src="/profile/${user.profile}"
                                                                          width="30" height="30" style="margin-bottom: 4px"></span></li>
                                        <!--닉네임-->
                                        <li style="width: 60%"><span
                                                id="post_detail_nickname">${user.nickname}</span></li>

                                        <!--더 보기 버튼-->
                                        <li style="width: 5%">
                                            <c:if test="${sessionScope.id == user.id}">
                                    <span class="dropdown">
                                        <img src="/images/more.png" alt="더 보기" width="20" height="20" role="button"
                                             data-toggle="dropdown" aria-expanded="true">
                                        <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
                                            <li role="presentation"><a role="menuitem" tabindex="-1"
                                                                       onclick="editPost()">게시물 수정</a></li>
                                            <li role="presentation"><a role="menuitem" tabindex="-1" style="color:red"
                                                                       onclick="deletePost()">게시물 삭제</a></li>
                                        </ul>
                                    </span>
                                            </c:if>
                                        </li>

                                        <!--닫기 버튼-->
                                        <li style="width: 10%">
                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                <span aria-hidden="true">&times;</span></button>
                                        </li>
                                    </ul>
                                    <div style="border: 0.2px solid #C2C3C1; width: 90%"></div>
                                </div>

                                <!--댓글 박스-->
                                <div id="comment_box" class="row" style="height:350px;overflow-y: auto ;padding-left:30px; width: 100%">

                                </div>


                                <!--좋아요 버튼, 등록 일자-->
                                <div class="row" style="height: 40px; width: 90%">
                                    <ul class="list-inline">
                                        <!--좋아요 버튼-->
                                        <li style="padding-left: 30px;width: 15%">
                                            <img id="like_btn_in_modal" src="/images/like_unclicked.png" alt="좋아요" width="30" height="30" role="button">
                                        </li>

                                        <!--좋아요 개수-->
                                        <li id="like_count_box" role="button" style="width: 60%">
                                            좋아요 <span id="like_count_in_modal">0</span>개
                                        </li>

                                        <!--업로드 시간-->
                                        <li id="time_box" style="color:#777777">

                                        </li>
                                    </ul>
                                </div>
                                <div style="border: 0.2px solid #C2C3C1; width: 90%"></div>

                                <!--댓글 등록 메뉴 박스-->
                                <div class="row" style=" height: 60px">
                                    <div class="col-sm-9 col-md-9" style="padding-left: 15px;padding-right: 0px">
                                            <textarea class="form-control" name="comment" placeholder="댓글"
                                                      style="height: 55px"></textarea></div>
                                    <div class="col-sm-3 col-md-3" style="padding: 0px">
                                        <button id="upload_comment_button" class="btn btn-default"
                                                style="height: 55px" disabled="true">게시
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>


                </div>


            </div>
        </div>
    </div>
</div>

<!--댓글 수정 dialog-->
<div class="modal fade" id="edit_comment" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">댓글 수정</h4>
            </div>
            <div class="modal-body">
                <textarea class="form-control" id="editted_comment" cols="30" rows="3" placeholder="댓글"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
                <button id="edit_comment_btn" type="button" class="btn btn-primary">수정</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!--좋아요 리스트 dialog-->
<div class="modal fade" id="like_list" role="dialog" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">좋아요</h4>
            </div>
            <div class="modal-body" style="overflow: auto">
                <div id="like_list_box" class="container" style="padding: 0px; width: 100%">

                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</body>
<script type="text/javascript">

    var postId;//게시물 수정이나 삭제를 할 때 사용할 게시물 번호
    var myId = $('#session_userid').val();//자기 자신의 id
    var ownerId = $('#page_owner_id').val();//페이지 주인의 id
    //프로필 사진이나 닉네임을 누를 시 사용자 페이지로 이동
    function requestUserPage(userId) {
        location.href = '/user?id=' + userId;
    }

    //댓글 수정
    function editComment(commentId, userId) {
        var existingComment = $('#comment'+commentId).html();
        console.log("commentId: " + commentId);
        console.log("userId: " + userId);
        console.log("existingComment: " + existingComment);
        $('#editted_comment').val(existingComment);//기존 댓글을 먼저 textarea에 설정

        $('#edit_comment_btn').on('click', function () {
            var edittedComment = $('#editted_comment').val();
            if (edittedComment == '') {//댓글이 없는 경우
                alert('댓글을 입력해주세요.');
                return;
            }

            $.ajax({
                type: 'PUT',
                url: '/comment',
                data: {
                    'commentId': commentId,
                    'userId': userId,
                    'comment': edittedComment
                }
            }).done(function (result) {
                alert('댓글이 수정되었습니다.');
                $('#edit_comment').modal('hide');
                $('#comment'+result).html(edittedComment);
            }).fail(function (e) {
                if (e.status == 406) {
                    alert('유효하지 않은 접근 입니다.');
                } else if (e.status == 500) {
                    alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.');
                } else if (e.status == 403) {
                    alert('로그인 후 이용하세요.');
                    location.href = '/login';
                }
            });

        });
    }

    //댓글 삭제
    function deleteComment(commentId, userId) {
        var confirm = window.confirm("정말로 댓글을 삭제하시겠습니까?");
        if (confirm) {
            $.ajax({
                type: "DELETE",
                url: '/comment',
                data: {
                    'commentId': commentId,
                    'userId': userId
                }
            }).done(function (result) {
                alert('댓글이 삭제되었습니다.');
                $('#comment_box' + result).remove();//해당 댓글 태그 삭제
            }).fail(function (e) {
                if (e.status == 406) {
                    alert('유효하지 않은 접근 입니다.');
                } else if (e.status == 500) {
                    alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.');
                } else if (e.status == 403) {
                    alert('로그인 후 이용하세요.');
                    location.href='/login';
                }
            });
        }
    }


    //모달이 열릴 때 호출
    function openPost(userId, id, nickname, profile, article, time) {
        postId = id;
        $('#image_box').html('');//이미지 초기화
        $('#indicator_box').html('');//이미지 표시자 초기화
        $('#time_box').html('');//시간 초기화
        $('#comment_box').html('');//댓글 초기화
        $('#like_btn_in_modal').attr('src', '/images/like_unclicked.png').attr('value', 'false');//좋아요 상태 초기화(false)
        $('#like_count_in_modal').html('0');//좋아요 개수 초기화(0)
        if (article != null && article != '') {//게시글 존재시 게시글을 댓글 박스 최상단에 추가
            $('#comment_box').append(
                '<div id="comment_box' + id + '" class="row" style="margin-right: 0px; width:100%">' +
                <!--좌측 박스-->
                '<div class="col-md-5 col-sm-5 col-xs-5">' +
                <!--프로필 사진, 닉네임, 게시 시간 박스-->
                '<div class="row" style="margin-top: 10px">' +
                <!--프로필 사진-->
                '<div class="col-md-12 col-sm-12 col-xs-12" role="button" onclick="requestUserPage(' + userId + ')">' +
                '<img id="article_profile" class="img-circle" src="/profile/' + profile + '" width="30" height="30">' +
                '<span style="margin-left: 10px">' + nickname + '</span>' +
                '</div>' +
                '</div>' +
                <!--게시 시간-->
                '<div class="row">' +
                '<div class="col-md-12 col-sm-12 col-xs-12 text-center" style="font-size: 10px">' + time + '</div>' +
                '</div>' +
                '</div>' +
                <!--우측 박스-->
                '<div class="col-md-7 col-sm-7 col-xs-7">' +
                '<div class="row" style="height: auto;padding-top:15px; word-break: break-all"><span>' + article + '</span></div>' +
                '</div>');
        }

        $.ajax({
            url: '/post/detail',
            type: 'GET',
            data: {
                'postId': id
            }
        }).done(function (result) {
            var data = JSON.parse(result);
            $('#time_box').html(data.time);//업로드 시간 입력

            console.log(data);
            console.log(data.size);
            for (var i = 0; i < data.size; i++) {//이미지 삽입
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

            //좋아요 버튼 설정
            if(data.likeStatus) {//좋아요를 누른 경우
                $('#like_btn_in_modal').attr('src', '/images/like_clicked.png').attr('value', 'true');
            }

            //좋아요 개수 설정
            $('#like_count_in_modal').html(data.likeCount);


            for (var i = 0; i < data.comments.length; i++) {//댓글 삽입
                var commentId = data.comments[i].id;
                var profile = data.comments[i].profile;
                var nickname = data.comments[i].nickname;
                var comment = data.comments[i].comment;
                var time = data.comments[i].time;
                var userId = data.comments[i].userId;
                if(myId != null && myId == userId) {
                    $('#comment_box').append(
                        '<div id="comment_box' + commentId + '" class="row" style="margin-right: 0px; width:100%">' +
                        <!--좌측 박스-->
                        '<div class="col-md-5 col-sm-5 col-xs-5">' +
                        <!--프로필 사진, 닉네임, 게시 시간 박스-->
                        '<div class="row" style="margin-top: 10px">' +
                        <!--프로필 사진-->
                        '<div id="profile_box" role="button" class="col-md-12 col-sm-12 col-xs-12" onclick="requestUserPage(' + userId + ')">' +
                        '<img id="article_profile" class="img-circle" src="/profile/' + profile + '" width="30" height="30">' +
                        '<span style="margin-left: 10px">' + nickname + '</span>' +
                        '</div>' +

                        <!--게시 시간-->
                        '<div class="row">' +
                        '<div class="col-md-12 col-sm-12 col-xs-12 text-center" style="font-size: 10px">' + time + '</div>' +
                        '</div>' +
                        <!--수정 삭제 버튼-->

                        '<div class="row">' +
                        '<div class="col-md-12 col-sm-12 col-xs-12" style="font-size: 12px; margin-left: 20px"><span role="button" style="margin-right: 10px" data-target="#edit_comment" data-toggle="modal" onclick="editComment(' + commentId + ',' + userId + ')">수정</span><span role="button" onclick="deleteComment(' + commentId + ',' + userId + ')">삭제</span></div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +

                        <!--우측 박스-->
                        '<div class="col-md-7 col-sm-7 col-xs-7">' +
                        '<div id="comment' + commentId + '" class="row" style="height: auto;padding-top:15px; word-break: break-all">' + comment + '</div>' +
                        '<div class="row">' +
                        '</div>' +
                        '</div>');
                }
                else {
                    $('#comment_box').append(
                        '<div id="comment_box' + commentId + '" class="row" style="margin-right: 0px; width:100%">' +
                        <!--좌측 박스-->
                        '<div class="col-md-5 col-sm-5 col-xs-5">' +
                        <!--프로필 사진, 닉네임, 게시 시간 박스-->
                        '<div class="row" style="margin-top: 10px">' +
                        <!--프로필 사진-->
                        '<div id="profile_box" role="button" class="col-md-12 col-sm-12 col-xs-12" onclick="requestUserPage(' + userId + ')">' +
                        '<img id="article_profile" class="img-circle" src="/profile/' + profile + '" width="30" height="30">' +
                        '<span style="margin-left: 10px">' + nickname + '</span>' +
                        '</div>' +

                        <!--게시 시간-->
                        '<div class="row">' +
                        '<div class="col-md-12 col-sm-12 col-xs-12 text-center" style="font-size: 10px">' + time + '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +

                        <!--우측 박스-->
                        '<div class="col-md-7 col-sm-7 col-xs-7">' +
                        '<div id="comment' + commentId + '" class="row" style="height: auto;padding-top:15px; word-break: break-all">' + comment + '</div>' +
                        '<div class="row">' +
                        '</div>' +
                        '</div>');
                }

            }

        }).fail(function () {

        });
    }

    //게시물 수정 페이지로 이동
    function editPost() {
        console.log('post id: ' + postId);
        location.href = "/edit?postId=" + postId;
    }

    //게시물 삭제 페이지로 이동
    function deletePost() {
        var confirm = window.confirm('정말로 삭제하시겠습니까?');
        if(confirm) {
            $.ajax({
                type:'DELETE',
                url:'/post',
                data:{
                    'userId':myId,
                    'postId':postId
                }
            }).done(function (result) {
                alert('게시물이 삭제되었습니다.');
                $('#post_detail').modal('hide');
                $('#post'+result).remove();//게시물 삭제
            }).fail(function (e) {
                if (e.status == 406) {
                    alert('유효하지 않은 접근 입니다.');
                } else if (e.status == 500) {
                    alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.');
                } else if (e.status == 403) {
                    alert('로그인 후 이용하세요.');
                    location.href = '/login';
                }
            });
        }
    }

    //좋아요 개수 클릭시 모달 창 open
    function showLikeList(postId) {
        $.ajax({
            type:'GET',
            url:'/like',
            data:{
                'postId':postId
            }
        }).done(function (result) {
            data = JSON.parse(result);
            console.log(data);
            $('#like_list_box').html('');//좋아요 리스트 초기화
            for(var i = 0; i < data.length; i ++) {
                if(myId != data[i].userId) {
                    $('#like_list_box').append(
                        '<div class="row" style="margin-bottom: 15px" role="button" onclick="requestUserPage('+data[i].userId+')">'+
                        '<div class="col-md-1">'+
                        '<img src="/profile/'+data[i].profile+'" alt="프로필 사진" width="40" height="40" class="img-circle">'+
                        '</div>'+
                        '<div class="col-md-9">'+
                        '<h4>'+data[i].nickname+'</h4>'+
                        '</div>'+
                        '<div class="col-md-1">' +
                        '<button class="btn btn-info">팔로우</button>' +
                        '</div>'+
                        '</div>'
                    );
                }
                else {
                    $('#like_list_box').append(
                        '<div class="row" style="margin-bottom: 15px" role="button" onclick="requestUserPage('+data[i].userId+')">'+
                        '<div class="col-md-1">'+
                        '<img src="/profile/'+data[i].profile+'" alt="프로필 사진" width="40" height="40" class="img-circle">'+
                        '</div>'+
                        '<div class="col-md-9">'+
                        '<h4>'+data[i].nickname+'</h4>'+
                        '</div>'+
                        '</div>'
                    );
                }
            }
            $('#like_list').modal('show');

        }).fail(function (e) {
            var status = e.status;
            if(status == 403) {
                alert('로그인 후 이용하세요.');
                location.href='/login';
            }
            else if(status == 406) {
                alert('유효하지 않은 접근 입니다.');
            }
            else {//500
                alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.');
            }
        });
    }

    function processFollow(myId, ownerId){
        var status = $('#follow_btn').val();
        if(status == 'false') {//팔로우를 한 경우
            $('#follow_btn').val('true');//팔로우 상태를 true로 전환
            $('#follow_btn').attr('class', 'btn btn-default');
            $('#follow_btn').html('팔로잉');
        }
        else {//팔로우를 취소한 경우
            $('#follow_btn').val('false');//팔로우 상태를 false로 전환
            $('#follow_btn').attr('class', 'btn btn-info');
            $('#follow_btn').html('팔로우');

        }
    }

    $(document).ready(function () {
        $('textarea').on('keyup', function () {
            var comment = $(this).val();
            if (comment.trim() != '') {//글자가 존재하는 경우
                $('#upload_comment_button').attr('disabled', false);//댓글 등록 버튼을 활성화
            } else {
                $('#upload_comment_button').attr('disabled', true);//댓글 등록 버튼을 비활성화
            }
        });

        $('#upload_comment_button').on('click', function () {
            var comment = $('textarea').val();
            $.ajax({
                type: 'POST',
                url: '/comment',
                data: {
                    'postId': postId,
                    'comment': comment
                }
            }).done(function (result) {
                console.log('comment post success');
                console.log(result);
                alert('댓글을 등록하셨습니다.');
                $('textarea').val('');//댓글 입력 창 clear
                var commentData = JSON.parse(result);
                var commentId = commentData.id;
                var nickname = commentData.nickname;
                var profile = commentData.profile;
                var time = commentData.time;
                var userId = commentData.userId;
                $('#comment_box').append(
                    '<div id="comment_box' + commentId + '" class="row" style="margin-right: 0px; width:100%">' +
                    <!--좌측 박스-->
                    '<div class="col-md-5 col-sm-5 col-xs-5">' +
                    <!--프로필 사진, 닉네임, 게시 시간 박스-->
                    '<div class="row" style="margin-top: 10px">' +
                    <!--프로필 사진-->
                    '<div role="button" class="col-md-12 col-sm-12 col-xs-12" onclick="requestUserPage(' + userId + ')">' +
                    '<img id="article_profile" class="img-circle" src="/profile/' + profile + '" width="30" height="30">' +
                    '<span style="margin-left: 10px">' + nickname + '</span>' +
                    '</div>' +

                    <!--게시 시간-->
                    '<div class="row">' +
                    '<div class="col-md-12 col-sm-12 col-xs-12 text-center" style="font-size: 10px">' + time + '</div>' +
                    '</div>' +
                    <!--수정 삭제 버튼-->
                    '<div class="row">' +
                    '<div class="col-md-12 col-sm-12 col-xs-12" style="font-size: 12px; margin-left: 20px"><span role="button" style="margin-right: 10px" data-target="#edit_comment" data-toggle="modal" onclick="editComment(' + commentId + ',' + userId + ')">수정</span><span role="button"  onclick="deleteComment(' + commentId + ',' + userId + ')">삭제</span></div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +

                    <!--우측 박스-->
                    '<div class="col-md-7 col-sm-7 col-xs-7">' +
                    '<div id="comment'+commentId+'" class="row" style="height: auto;padding-top:15px; word-break: break-all">' + comment + '</div>' +
                    '<div class="row">' +
                    '</div>' +
                    '</div>');
            }).fail(function (e) {
                console.log('comment post fail');
                console.log('status code: ' + e.status);
                if (e.status == 403) {
                    alert('로그인 후 이용하세요.');
                    location.href = '/login';
                } else if (e.status == 500) {
                    alert('서버측의 문제로 인해 댓글 등록에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            });
        });

        //모달 창의 좋아요 버튼을 누른 경우
        $('#like_btn_in_modal').on('click', function () {
            processLike(postId, myId);
        });

        $('#like_count_box').on('click', function () {//좋아요 개수 클릭 리스너
            showLikeList(postId);
        });

        $('#follow_btn').on('click', function () {//팔로우 버튼 클릭 리스너
            processFollow(myId, ownerId);
        });

    });

    //좋아요 처리
    function processLike(postId, userId) {
        console.log($('#like_btn_in_modal').attr('value'));
        console.log('postId: ' + postId);
        console.log('userId: ' + userId);
        var status = $('#like_btn_in_modal').attr('value');
        if (status == 'false') {//좋아요를 한 경우
            $('#like_btn_in_modal').attr('value', 'true');//좋아요 상태를 true로 변경
            console.log('좋아요');
            $.ajax({
                type:'POST',
                url:'/like',
                data:{
                    'postId':postId,
                    'userId':userId
                }
            }).done(function (result) {
                $('#like_btn_in_modal').attr('src', '/images/like_clicked.png');
                var likeCount = Number($('#like_count_in_modal').html()) + 1;
                $('#like_count_in_modal').html(String(likeCount));
            }).fail(function (e) {
                var status = e.status;
                if(status == 403) {
                    alert('로그인 후 이용하세요.');
                    location.href='/login';
                }
                else if(status == 406) {
                    alert('유효하지 않은 접근 입니다.');
                }
                else {//500
                    alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.');
                }

            });

        } else {//좋아요를 취소한 경우
            $('#like_btn_in_modal').attr('value', 'false');
            console.log('좋아요 취소');
            $.ajax({
                type: 'DELETE',
                url: '/like',
                data: {
                    'postId': postId,
                    'userId': userId
                }
            }).done(function (result) {
                $('#like_btn_in_modal').attr('src', '/images/like_unclicked.png');
                var likeCount = Number($('#like_count_in_modal').html()) - 1;
                $('#like_count_in_modal').html(String(likeCount));
            }).fail(function (e) {
                var status = e.status;
                if (status == 403) {
                    alert('로그인 후 이용하세요.');
                    location.href = '/login';
                } else if (status == 406) {
                    alert('유효하지 않은 접근 입니다.');
                } else {//500
                    alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.');
                }
            });
        }
    }

</script>
</html>