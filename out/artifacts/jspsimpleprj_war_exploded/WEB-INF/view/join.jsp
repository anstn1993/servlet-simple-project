<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%--<c:if test="${joinResult != null}">--%>
<%--    <script>--%>
<%--        alert('회원가입 실패');--%>
<%--    </script>--%>
<%--    <c:remove var="joinResult" scope="request"/>--%>
<%--</c:if>--%>

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>회원가입</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/navigation.css">
    <script
            src="https://code.jquery.com/jquery-3.4.1.min.js"
            integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">
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
                        <a href="/login">로그인</a>
                    </li>
                    <li class="active">
                        <a href="/join">회원가입</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="col-md-6 col-md-offset-3 col-sm-6 col-sm-offset-3">
        <div class="panel panel-info">
            <div class="panel-heading text-center">회원가입</div>
            <div class="panel-body">
                <form enctype='multipart/form-data' method="post" action="/join">
                    <div class="form-group">
                        <label for="account">ID</label>
                        <label id="account_check"></label>
                        <input id="account" class="form-control" type="text" name="account" placeholder="ID">
                    </div>

                    <div class="form-group">
                        <label for="password">비밀번호</label>
                        <label id="password_check"></label>
                        <input id="password" class="form-control" type="password" name="password" placeholder="비밀번호">
                    </div>

                    <div class="form-group">
                        <label for="password_match">비밀번호 확인</label>
                        <label id="password_match_check"></label>
                        <input id="password_match" class="form-control" type="password" name="password_match"
                               placeholder="비밀번호 확인">
                    </div>

                    <div class="form-group">
                        <label for="name">이름</label>
                        <label id="name_check"></label>
                        <input id="name" class="form-control" type="text" name="name" placeholder="이름">
                    </div>

                    <div class="form-group">
                        <label for="nickname">닉네임</label>
                        <label id="nickname_check"></label>
                        <input id="nickname" class="form-control" type="text" name="nickname" placeholder="닉네임">
                    </div>

                    <div class="form-group">
                        <label for="email">이메일</label>
                        <label id="email_check"></label>
                        <input id="email" class="form-control" type="email" name="email" placeholder="이메일">
                    </div>
                    <div class="form-group">
                        <input id="submit" class="form-control btn-primary" type="submit" value="가입하기">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
<script src="//code.jquery.com/jquery-3.4.1.js"></script>
<script type="text/javascript">

    var accountForm = /^[a-zA-Z0-9]*$/;//영어와 숫자로 조합
    var nameForm = /^[가-힝]{2,}$/;//한글
    var emailForm = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;//이메일
    var isAccountValid = false;
    var isPasswordValid = false;
    var isNameValid = false;
    var isNicknameValid = false;
    var isEmailValid = false;


    //계정 검사
    function checkAccount(account) {//id 유효성 검사
        if (!accountForm.test(account)) {
            console.log("account test: invalid");
            $('#account_check').css({
                'color': "red"
            }).html("아이디는 영어와 숫자로 조합해주세요.");
        } else if (account.length < 4 || account.length > 16) {
            console.log("account test: length invalid");
            $('#account_check').css({
                'color': "red"
            }).html("아이디의 길이를 4자에서 16자 사이로 설정해주세요.");
        } else {
            //TODO: 서버에서 중복 검사 실시
            // ajax통신 api를 통해서 post방식으로 form태그에 있는 데이터 전송
            $.ajax(
                //jquery ajax api의 파라미터로 들어가는 setting객체
                {
                    url: '/join/check',
                    type: 'POST',//전송방식 post
                    data: {
                        "jsonData": JSON.stringify({
                            "type": "account",
                            "value": account
                        })
                    }
                    // dataType: "json"
                }
            ).done(function (result) {
                console.log("account check response arrived");
                if (result == 0) {
                    $('#account_check').css({
                        'color': "red"
                    }).html("이미 사용중인 ID 입니다.");
                    console.log("계정 중복");
                    isAccountValid = false;
                } else {
                    $('#account_check').css({
                        'color': "green"
                    }).html("사용 가능");
                    console.log("사용 가능한 계정");
                    isAccountValid = true;
                }
            }).fail(function () {
                console.log("account check connection fail");
                $('#account_check').css({
                    'color': "red"
                }).html("중복 확인에 실패했습니다. 인터넷 연결을 확인하세요")
                isAccountValid = false;
            });
        }
    }

    //비밀번호 검사
    function checkPassword(password) {
        if (password.length < 8 || password.length > 16) {
            console.log("password test: length invalid");
            $('#password_check').css({
                'color': "red"
            }).html("비밀번호의 길이를 8자에서 16자 사이로 설정해주세요.");
            isPasswordValid = false;
        } else {
            $('#password_check').html("");
        }
    }

    //비밀번호 확인 검사
    function checkPasswordMatch(password, password_match) {
        if (password.length >= 8 && password.length <= 16) {
            if (password != password_match) {
                $('#password_match_check').css({
                    'color': "red"
                }).html("비밀번호 불일치");
                isPasswordValid = false;
            } else {
                $('#password_match_check').html("");
                isPasswordValid = true;
            }
        } else {
            $('#password_match_check').html("");
            isPasswordValid = false;
        }
    }

    //이름 검사
    function checkName(name) {
        if (!nameForm.test(name)) {
            $('#name_check').css({
                'color': "red"
            }).html("잘못된 형식의 이름 입니다.");
            isNameValid = false;
        } else if (name.length > 30) {
            $('#name_check').css({
                'color': "red"
            }).html("이름은 최대 30자까지 입니다.");
            isNameValid = false;
        } else {
            $('#name_check').html("");
            isNameValid = true;
        }
    }

    //닉네임 검사
    function checkNickname(nickname) {
        if (nickname.length > 20) {
            $('#nickname_check').css({
                'color': "red"
            }).html("닉네임의 길이는 20자를 초과할 수 없습니다.");
            isNicknameValid = false;
        } else {
            //TODO:서버에서 중복 검사
            // ajax통신 api를 통해서 post방식으로 form태그에 있는 데이터 전송
            $.ajax(
                //jquery ajax api의 파라미터로 들어가는 setting객체
                {
                    url: '/join/check',
                    type: 'POST',//전송방식 post
                    data: {
                        "jsonData": JSON.stringify({
                            "type": "nickname",
                            "value": nickname
                        })
                    }//form태그 안에 있는 여러 태그들의 value값들을 담아서 전달.
                }
            ).done(function (result) {
                console.log("account check response arrived");
                if (result == 0) {
                    $('#nickname_check').css({
                        'color': "red"
                    }).html("이미 사용중인 닉네임 입니다.");
                    console.log("닉네임 중복");
                    isNicknameValid = false;
                } else {
                    $('#nickname_check').css({
                        'color': "green"
                    }).html("사용 가능");
                    console.log("사용 가능한 닉네임");
                    isNicknameValid = true;
                }
            }).fail(function () {
                console.log("nickname check connection fail");
                $('#nickname_check').css({
                    'color': "red"
                }).html("중복 확인에 실패했습니다. 인터넷 연결을 확인하세요");
                isNicknameValid = false;
            });
        }
    }

    //이메일 검사
    function checkEmail(email) {
        if (!emailForm.test(email)) {
            $('#email_check').css({
                'color': "red"
            }).html("잘못된 형식의 이메일 입니다.");
            isEmailValid = false;
        } else {
            $('#email_check').html("");
            isEmailValid = true;
        }
    }

    //진입점
    $(document).ready(function () {

        $('#account').on('blur', function () {
            var account = $('#account').val();
            if (account.trim() == "") {
                $('#account_check').html("");
                isAccountValid = false;
            } else {
                checkAccount(account);
            }
        });

        $('#password').on('blur', function () {
            var password = $('#password').val();
            if (password.trim() == "") {
                $('#password_check').html("");
                isPasswordValid = false;
            } else {
                checkPassword(password);
            }
        });

        $('#password_match').on('blur', function () {
            var password = $('#password').val();
            var password_match = $('#password_match').val();
            if (password_match.trim() == "") {
                $('#password_match_check').html("");
                isPasswordValid = false;
            } else {
                checkPasswordMatch(password, password_match);
            }
        });

        $('#name').on('blur', function () {
            var name = $('#name').val();
            if (name.trim() == "") {
                $('#name_check').html("");
                isNameValid = false;
            } else {
                checkName(name);
            }
        });

        $('#nickname').on('blur', function () {
            var nickname = $('#nickname').val();
            if (nickname.trim() == "") {
                $('#nickname_check').html("");
                isNicknameValid = false;
            } else {
                checkNickname(nickname);
            }
        });

        $('#email').on('blur', function () {
            var email = $('#email').val();
            if (email.length == "") {
                $('#email_check').html("");
                isEmailValid = false;
            } else {
                checkEmail(email);
            }
        });

        $('#submit').on('click', function () {
            if (isAccountValid && isPasswordValid && isNameValid && isNicknameValid && isEmailValid) {
                // $.ajax({
                //     url: '/join',
                //     type: 'POST',
                //     data: {
                //         "userData": JSON.stringify({
                //             "account": $('#account').val(),
                //             "password": $('#password').val(),
                //             "name": $('#name').val(),
                //             "nickname": $('#nickname').val(),
                //             "email": $('#email').val()
                //         })
                //     }
                // }).done(function(result){
                //     alert('회원가입에 성공하였습니다. 이메일 인증을 하셔야 로그인을 하실 수 있습니다.');
                // }).fail(function(){
                //     alert('오류: 회원가입 실패');
                // });
                return true;
            } else {
                alert('회원가입을 하실 수 없습니다. 항목을 유효하게 입력했는지 확인해주세요.');
                return false;//새로고침 방지
            }
        });

    });


</script>
</html>