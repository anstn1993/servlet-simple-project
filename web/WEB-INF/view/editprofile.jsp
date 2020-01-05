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
            <a href="/user/edit" class="list-group-item active">
                프로필 편집
            </a>
            <a href="/user/edit/password" class="list-group-item">비밀번호 변경</a>
            <a href="/user/withdrawal" class="list-group-item">회원 탈퇴</a>
        </div>
    </div>
    <div class="col-md-9 col-sm-8">
        <div class="panel panel-info">
            <div class="panel-heading text-center">프로필 수정</div>
            <div class="panel-body">
                <form class="md-form" method="post" action="/user/edit" enctype="multipart/form-data">
                    <input id="id" type="hidden" name="id" value="${sessionScope.id}">
                    <!--프로필 사진 -->
                    <div class="form-group">
                        <c:if test="${sessionScope.profile == null}">
                            <img id="profile" src="/images/profile.png" alt="프로필 사진" class="img-circle" width="90"
                                 height="90">
                        </c:if>
                        <c:if test="${sessionScope.profile != null}">
                            <img id="profile" src="/profile/${sessionScope.profile}" alt="프로필 사진" class="img-circle"
                                 width="90" height="90">
                        </c:if>
                        <div class="form-group form-inline">
                            <br>
                            <input id="profile_input" filestyle="" type="file" name="profileFile" accept="image/*"
                                   data-class-button="btn btn-default" data-class-input="form-control"
                                   class="form-control" tabindex="-1"
                                   style="position: absolute; clip: rect(0px 0px 0px 0px);"><%--position이 absolute이거나 flex일 때 clip의 rect()를 쓰면 특정 부분을 가려버릴 수 있다.--%>
                            <c:if test="${sessionScope.profile != null}">
                                <input type="text" id="profile_name" class="form-control" disabled="true"
                                       name="profileName" value="${sessionScope.profile}">
                            </c:if>
                            <c:if test="${sessionScope.profile == null}">
                                <input type="text" id="profile_name" class="form-control" disabled="true"
                                       name="profileName">
                            </c:if>
                            <label class="btn btn-default" for="profile_input">이미지 선택</label>
                            <span id="delete_profile_box">

                            </span>
                        </div>
                    </div>

                    <!--이름 -->
                    <div class="form-group">
                        <label for="name">이름</label>
                        <label id="name_check"></label>
                        <input id="name" class="form-control" type="text" name='name' placeholder="이름"
                               value="${sessionScope.name}"/>
                    </div>

                    <!--닉네임-->
                    <div class="form-group">
                        <label for="nickname">닉네임</label>
                        <label id="nickname_check"></label>
                        <input id="nickname" class="form-control" type="text" name="nickname" placeholder="닉네임"
                               value="${sessionScope.nickname}"/>
                    </div>

                    <!--이메일 -->
                    <div class="form-group">
                        <label for="email">이메일</label>
                        <label id="email_check"></label>
                        <input id="email" class="form-control" type="email" name="email" placeholder="이메일"
                               value="${sessionScope.email}"/>
                    </div>

                    <!--소개 -->
                    <div class="form-group">
                        <label for="introduce">소개</label>
                        <textarea class="form-control" name="introduce" id="introduce" cols="30" rows="2"
                                  placeholder="소개" maxlength="150"><c:if
                                test="${sessionScope.introduce != null}">${sessionScope.introduce}</c:if></textarea>
                    </div>

                    <div class="form-group">
                        <input id="submit" class="form-control btn-primary" type="submit" value="수정">
                    </div>
                </form>


            </div>
        </div>
    </div>
</div>
</body>
<script src="//code.jquery.com/jquery-3.4.1.js"></script>
<script type="text/javascript">
    var currentName = $('#name').val();
    var currentNickname = $('#nickname').val();
    var currentEmail = $('#email').val();
    var nameForm = /^[가-힝]{2,}$/;//한글
    var emailForm = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;//이메일
    var isNameValid = true;
    var isNicknameValid = true;
    var isEmailValid = true;
    var isNewImage = false;
    var dataURI;//압축된 데이터 uri
    //이미지 삭제 버튼 클릭시 호출되는 메소드
    function deleteImg() {
        console.log('이미지 삭제 버튼 클릭');
        $('#profile').attr("src", "/images/profile.png");//기본 이미지로 전환
        $('#profile_input').val('');//파일 삭제
        $('#profile_name').val('');//파일 명 input 값 삭제
        $('#delete_profile_box').html('');//이미지 삭제 버튼 제거
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
        } else if (nickname == currentNickname) {//기존 닉네임과 동일한 경우
            $('#nickname_check').html('');
            isNicknameValid = true;
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

    //데이터 전송


    function sendData() {
        var id = $('#id').val();
        var name = $('#name').val();
        var nickname = $('#nickname').val();
        var email = $('#email').val();
        var introduce = $('#introduce').val();
        var profileName = $('#profile_name').val();
        var profileFile = $('#profile_input').val();
        var formData = new FormData();
        formData.append("id", id);
        formData.append("name", name);
        formData.append("nickname", nickname);
        formData.append("email", email);
        formData.append("introduce", introduce);
        if(profileName != null && profileName != '') {
            formData.append("profileName", profileName);
        }
        if(profileFile != null && profileFile != ''){
            formData.append("profileFile", dataURLToBlob(dataURI));
        }
        //ajax통신 시작
        $.ajax(
            {
                url: '/user/edit',
                type: 'POST',
                processData: false,
                contentType: false,
                data: formData
            }
        ).done(function(result){
            if(result == 0) {
                alert('프로필 수정 실패');
            }
            else {
                console.log('프로필 수정 통신 성공');
                alert('프로필 수정 완료');
            }
        }).fail(function () {
            console.log('프로필 수정 통신 실패');
            alert('프로필 수정 실패');
        });
    }

    //canvas의 data url을 blob 객체로 변환해서 file로 업로드하기 위한 데이터로 변환
    var dataURLToBlob = function (dataURL) {
        var BASE64_MARKER = ';base64,';
        //base 64로 인코딩되어있지 않은 경우
        if (dataURL.indexOf(BASE64_MARKER) == -1) {
            var parts = dataURL.split(',');
            var contentType = parts[0].split(':')[1];//mime type(media type)
            var raw = parts[1];//데이터 그 자체
            return new Blob([raw], {type: contentType});
        }
        var parts = dataURL.split(BASE64_MARKER);
        var contentType = parts[0].split(':')[1];
        var raw = window.atob(parts[1]);//window.atob()는 base 64를 디코딩하는 메소드
        var rawLength = raw.length;
        var uInt8Array = new Uint8Array(rawLength);
        for (var i = 0; i < rawLength; ++i) {
            uInt8Array[i] = raw.charCodeAt(i);
        }
        return new Blob([uInt8Array], {type: contentType});
    };


    $(document).ready(function () {

        if($('#profile_name').val() != '') {
            $('#delete_profile_box').html("<input id='delete_profile_btn' class='btn btn-danger' type='button' value='이미지 삭제' onclick='deleteImg()'>");//이미지 삭제 버튼 생성
        }

        var file = document.querySelector('#profile_input');
        $('#profile_input').on('change', function () {
            console.log('파일 changed');
            var filename;
            if (window.FileReader) {  // modern browser
                filename = $(this)[0].files[0].name;
            } else {  // old IE
                filename = $(this).val().split('/').pop().split('\\').pop();  // 파일명만 추출
            }
            // 추출한 파일명 삽입
            $("#profile_name").val(filename);

            var fileLists = file.files;//선택한 이미지 파일 리스트

            var reader = new FileReader();//파일 읽기
            reader.readAsDataURL(fileLists[0]);//이미지 파일 읽을 때 쓰는 메소드로 이 메소드가 호출되면 reader의 onLoad이밴트 발생

            reader.onload = function () {
                console.log('reader에 load됨');
                //thumbnail 이미지 생성
                var tempImage = new Image();//thumbnail을 만들기 위한 이미지 객체 생성
                tempImage.src = reader.result;//data-uri를 이미지 객체에 주입->onload이벤트 발생
                tempImage.onload = function () {
                    //이미지 리사이즈를 위한 캔버스 객체 생성
                    var canvas = document.createElement('canvas');
                    var canvasContext = canvas.getContext('2d');//실제로 이미지를 그릴 때 사용할 변수

                    var maxSize = 720;//이미지 파일의 최대 크기

                    var width = tempImage.width;
                    var height = tempImage.height;

                    if(width > height) {
                        if(width > maxSize) {
                            height *= maxSize/width;
                            width = maxSize;
                        }
                    }
                    else {
                        if(height > maxSize) {
                            width *= maxSize/height;
                            height = maxSize;
                        }
                    }

                    //캔버스 크기 설정
                    canvas.width = width;
                    canvas.height = height;

                    //tempImage를 canvas위에 그린다.
                    canvasContext.drawImage(this, 0, 0, width, height);

                    //이미지 객체를 다시 data-uri형태로 바꿔서 img태그에 로드할 수 있게 함
                    dataURI = canvas.toDataURL("image/*");
                    console.log(dataURI);
                    $('#profile').attr("src", dataURI);
                    $('#delete_profile_box').html("<input id='delete_profile_btn' class='btn btn-danger' type='button' value='이미지 삭제' onclick='deleteImg()'>");//이미지 삭제 버튼 생성
                }

            };//end of reader.onload
        });//end of profile input change


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

        $('#submit').on('click', function (e) {
            if (isNameValid && isNicknameValid && isEmailValid) {
                e.preventDefault();
                sendData();//데이터 전송 ajax시작
                // return true;
            } else {
                alert('프로필 수정을 하실 수 없습니다. 항목을 유효하게 입력했는지 확인해주세요.');
                return false;//새로고침 방지
            }
        });


        //이미지 삭제 버튼 클릭 이밴트
        // $('#delete_profile_btn').on('click', function () {
        //     console.log('이미지 삭제 버튼 클릭');
        //     $('#profile').attr("src", "/images/profile.png");//기본 이미지로 전환
        //     $('#profile_input').val('');//파일 삭제
        //     $('#profile_name').val('');//파일 명 input 값 삭제
        //     $('#delete_profile_box').html('');//이미지 삭제 버튼 제거
        // });
    });

</script>

</html>