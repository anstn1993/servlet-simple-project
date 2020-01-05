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
            <a href="/user/edit/password" class="list-group-item active">비밀번호 변경</a>
            <a href="/user/withdrawal" class="list-group-item">회원 탈퇴</a>
        </div>
    </div>
    <div class="col-md-9 col-sm-8">
        <div class="panel panel-info">
            <div class="panel-heading text-center">비밀번호 변경</div>
            <div class="panel-body">
                <form method="post" action="/user/edit/password">
                    <input id="id" type="hidden" name="id" value="${sessionScope.id}">
                    <div class="form-group">
                        <label for="old_password">기존 비밀번호</label>
                        <label id="old_password_check"></label>
                        <input id="old_password" class="form-control" type="password" name="password"
                               placeholder="비밀번호">
                    </div>

                    <div class="form-group">
                        <label for="new_password">새 비밀번호</label>
                        <label id="new_password_check"></label>
                        <input id="new_password" class="form-control" type="password" name="password"
                               placeholder="비밀번호">
                    </div>

                    <div class="form-group">
                        <label for="new_password_match">새 비밀번호 확인</label>
                        <label id="new_password_match_check"></label>
                        <input id="new_password_match" class="form-control" type="password" name="password_match"
                               placeholder="비밀번호 확인">
                    </div>

                    <div class="form-group">
                        <input id="submit" class="form-control btn-primary" type="submit" value="변경">
                    </div>
                </form>


            </div>
        </div>
    </div>
</div>
</body>
<script src="//code.jquery.com/jquery-3.4.1.js"></script>
<script type="text/javascript">

    var isOldPasswordValid = false;
    var isNewPasswordValid = false;

    function checkOldPassword(id, oldPassword) {
        var data = {"id": id, "oldPassword": oldPassword};
        $.ajax(
            {
                url: '/edit/oldpassword/check',
                type: 'POST',
                data: {
                    "id":id,
                    "oldPassword":oldPassword
                }
            }
        ).done(function (result) {
            if (result == 0) {
                console.log("old password check status: 0");
                $('#old_password_check').css({
                    'color': 'red'
                }).html('비밀번호를 다시 확인해주세요.');
                isOldPasswordValid = false;
            } else if (result == 1) {
                console.log("old password check status: 1");
                $('#old_password_check').html('');
                isOldPasswordValid = true;
            } else {
                console.log("old password check status: 2");
                $('#old_password_check').css({
                    'color': 'red'
                }).html('유효성 검사 실패');
                isOldPasswordValid = false;
            }
        }).fail(function () {
            console.log("old password check status: fail");
            $('#old_password_check').css({
                'color': 'red'
            }).html('유효성 검사 실패');
            isOldPasswordValid = false;
        });
    }

    //비밀번호 검사
    function checkNewPassword(newPassword) {
        if (newPassword.length < 8 || newPassword.length > 16) {
            console.log("password test: length invalid");
            $('#new_password_check').css({
                'color': "red"
            }).html("비밀번호의 길이를 8자에서 16자 사이로 설정해주세요.");
            isNewPasswordValid = false;
        } else {
            $('#new_password_check').html("");
            var newPasswordMatch = $('#new_password_match').val();
            if(newPassword == newPasswordMatch) {
                isNewPasswordValid = true;
            }
        }
    }

    //비밀번호 확인 검사
    function checkNewPasswordMatch(newPassword, newPasswordMatch) {
        if (newPassword.length >= 8 && newPassword.length <= 16) {
            if (newPassword != newPasswordMatch) {
                $('#new_password_match_check').css({
                    'color': "red"
                }).html("비밀번호 불일치");
                isNewPasswordValid = false;
            } else {
                $('#new_password_match_check').html("");
                isNewPasswordValid = true;
            }
        } else {
            $('#new_password_match_check').html("");
            isNewPasswordValid = false;
        }
    }


    function changePassword(id, password) {
        $.ajax({
            url:'/user/edit/password',
            type:'POST',
            data:{
                "id":id,
                "password":password
            }
        }).done(function (result) {
            if(result == 0) {
                alert('비밀번호 변경에 실패했습니다.');
            }
            else if(result == 1) {
                alert('비밀번호 변경에 성공했습니다. 변경된 비밀번호로 다시 로그인 해주세요.');
                location.href = '/login';
            }
            else {
                alert('비밀번호 변경에 실패했습니다.');
            }
        }).fail(function () {
            alert('비밀번호 변경에 실패했습니다.');
        });
    }

    $(document).ready(function () {
        $('#old_password').on('blur', function () {
            var id = $('#id').val();//db 테이블 레코드의 사용자 id
            var oldPassword = $('#old_password').val();//기존 비밀번호
            if (oldPassword.trim() == '') {
                $('#old_password_check').html('');
                isOldPasswordValid = false;
            } else {
                checkOldPassword(id, oldPassword);
            }
        });

        $('#new_password').on('blur', function () {
            var newPassword = $('#new_password').val();
            if (newPassword.trim() == "") {
                $('#new_password_check').html("");
                isNewPasswordValid = false;
            } else {
                checkNewPassword(newPassword);
            }
        });

        $('#new_password_match').on('blur', function () {
            var newPassword = $('#new_password').val();
            var newPasswordMatch = $('#new_password_match').val();
            if (newPasswordMatch.trim() == "") {
                $('#new_password_match_check').html("");
                isNewPasswordValid = false;
            } else {
                checkNewPasswordMatch(newPassword, newPasswordMatch);
            }
        });

        $('#submit').on('click', function (e) {
            if(isNewPasswordValid && isOldPasswordValid) {
                e.preventDefault();
                var id = $('#id').val();
                var password = $('#new_password').val();
                changePassword(id, password);
            }
            else {
                alert('비밀번호를 변경할 수 없습니다. 항목들을 유효하게 입력했는지 확인해주세요.');
                return false;
            }

        })
    });
</script>
</html>