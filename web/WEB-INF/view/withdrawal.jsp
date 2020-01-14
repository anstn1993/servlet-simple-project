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
        <div class="list-group">
            <a href="/user/edit" class="list-group-item">
               프로필 편집
            </a>
            <a href="/user/edit/password" class="list-group-item">비밀번호 변경</a>
            <a href="/user/withdrawal" class="list-group-item active">회원 탈퇴</a>
        </div>
    </div>
    <div class="col-md-9 col-sm-8">
       <div class="alert alert-danger" role="alert">
           회원탈퇴를 하시게 되면 그동안 회원님이 업로드한 게시물과 댓글 및 팔로잉, 팔로워에 대한 정보가 모두 사라집니다. 그래도 탈퇴를 하시겠습니까?
       </div>
        <form action="/user/withdrawal" method="post">
            <input id="user_id" type="hidden" name="id" value="${sessionScope.id}">
            <input id="profile" type="hidden" name="profile" value="${sessionScope.profile}">
            <div class="form-group">
                <input id="agree" type="checkbox">
                <label for="agree">네, 탈퇴하겠습니다.</label>
            </div>
            <div class="form-group">
                <input id="withdrawl_btn" class="btn btn-danger" type="submit" value="탈퇴">
            </div>
        </form>

    </div>
</div>
</body>
<script type="text/javascript">
    $(document).ready(function(){
        $('#withdrawl_btn').on('click', function(e) {
            e.preventDefault();
            if($('input:checkbox[id="agree"]').is(':checked')) {
                $.ajax({
                    type:'DELETE',
                    url:'/user/withdrawal',
                    data:{
                        'userId':$('#user_id').val(),
                        'profile':$('#profile').val()
                    },
                    statusCode:{
                        200:function () {
                            alert('회원탈퇴가 완료되었습니다.');
                            location.href="/";
                        },
                        412:function () {
                            alert('허용되지 않은 접근 입니다.');
                        },
                        400:function() {
                            alert('유효하지 않은 접근 입니다.');
                        },
                        500:function () {
                            alert('서버에 문제가 생겼습니다. 잠시 후 다시 시도해주세요.');
                        }
                    }
                });
            }else {
                alert('체크박스에 체크를 해주세요.');
            }
        });
    });
</script>
</html>