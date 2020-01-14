<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>로그인</title>
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
                    <li class="active">
                        <a href="/login">로그인</a>
                    </li>
                    <li>
                        <a href="/join">회원가입</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default" style="height: 250px;">
            <div class="panel-heading text-center">로그인</div>
            <div class="panel-body">
                <form action="/login" method="post">
                    <br>
                    <div class="form-group">
                        <input class="form-control" id="account" type="text" name="account" placeholder="ID">
                    </div>

                    <div class="form-group">
                        <input class="form-control" id="password" type="password" name="password" placeholder="비밀번호">
                    </div>

                    <div class="form-group">
                        <input class="form-control btn-info" id="login_btn" type="submit" value="로그인">
                    </div>
                </form>
            </div>
        </div>
    </div>

</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        $('#login_btn').on('click', function () {
            var account = $('#account').val();
            var password = $('#password').val();
            if (account.trim() != "" && password.trim() != "") {
                return true;
            } else {
                alert('아이디와 비밀번호 모두 입력해주세요.');
                return false;
            }
        });
    });
</script>
</html>