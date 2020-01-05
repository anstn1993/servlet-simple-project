<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>게시물 업로드</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/navigation.css" type="text/css">
    <script
            src="https://code.jquery.com/jquery-3.4.1.min.js"
            integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <style>
        .upload_image {
            margin-right: 10px;
            margin-bottom: 10px;
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


    <form action="/post" method="post" enctype="multipart/form-data">
        <input id="file_input" type="file" multiple="multiple" name="uploadFile[]" accept="image/*"
               style="position:absolute; clip: rect(0px 0px 0px 0px)">
        <div class="col-md-6 col-sm-6 col-xs-6 col-md-offset-3 col-sm-offset-3 col-xs-offset-3"
             style="border: 1px solid black; padding-top:10px">
            <ul class="list-inline">
                <li>
                    <label id="image_select_button" for="file_input" class="btn btn-default">이미지 선택</label>
                </li>
                <li id="delete_image_btn_box">
                    <%--js에서 동적으로 추가--%>
                </li>
                <li id="select_delete_image_btn_box">
                    <%-- js에서 동적으로 추가--%>
                </li>
            </ul>

            <div id="upload_image_box" style="padding-top: 10px">
                <%--js에서 동적으로 추가--%>
            </div>
            <br>
            <div class="form-group">
                <label for="article">게시글</label>
                <textarea class="form-control" name="article" id="article" cols="30" rows="10"
                          placeholder="게시글"></textarea>
            </div>
            <div class="form-group">
                <input id="submit" class="form-control btn-info" type="submit" value="업로드">
            </div>
        </div>
    </form>


</div>
</body>
<script src="//code.jquery.com/jquery-3.4.1.js"></script>
<script type="text/javascript">
    var finalFiles = new Array();

    function dataURLToBlob(dataURL) {
        var BASE64_MARKER = ';base64,';
        //base64로 인코딩을 하지 않은 경우
        if (dataURL.indexOf(BASE64_MARKER) == -1) {
            var parts = dataURL.split(',');
            var contentType = parts[0].split(':')[1];
            var raw = parts[1];
            return new Blob([raw], {type: contentType});
        }
        var parts = dataURL.split(BASE64_MARKER);
        var contentType = parts[0].split(':')[1];
        var raw = window.atob(parts[1]);
        var rawLength = raw.length;
        var uint8Array = new Uint8Array(rawLength);
        for (var i = 0; i < rawLength; i++) {
            uint8Array[i] = raw.charCodeAt(i);
        }
        return new Blob([uint8Array], {type: contentType});
    }

    function deleteImage() {
        var selectedImages = document.getElementsByName("selectedImage[]");
        var isSelected = false;
        var newIndex = 0;

        for (var i = 0; i < selectedImages.length; i++) {//삭제할 이미지의 index 식별
            if (selectedImages[i].checked) {//이미지 삭제
                isSelected = true;
                console.log('selected image ' + i + ': ' + selectedImages[i]);
                finalFiles[i] = 0;//선택된 이미지는 0으로 바꿔서 이후에 0인 값을 찾아서 다 삭제
            }
        }

        if (!isSelected) {//삭제할 이미지를 선택하지 않은 경우
            alert('삭제할 이미지를 선택해 주세요');
            return;
        }

        for (var i = 0; i < finalFiles.length; i++) {//실제로 이미지 태그 삭제 후 남아있는 이미지의 index 재할당
            //img태그, checkbox 제거 및 finalFiles에서 제거
            if (finalFiles[i] == 0) {
                $('#image' + i).remove();

            } else {
                $('#image' + i).attr('id', 'image' + newIndex);
                $('#selector_label'+ i).attr('id', 'selector_label'+newIndex).attr('for', 'selector'+newIndex);

                newIndex++;
            }
        }

        while(true){//finalFiles배열에서 삭제된 이미지 제거
            var deletedIndex = finalFiles.indexOf(0);
            if(deletedIndex != -1) {
                finalFiles.splice(deletedIndex, 1);
            }
            else {
                break;
            }
        }

        $('.selector_box').remove();
        $('#select_delete_image_btn_box').html('');

        if(finalFiles.length == 0) {//이미지 파일이 모두 삭제된 경우
            $('#delete_image_btn_box').empty();
        }
        else {//이미지 파일이 남아있는 경우
            $('#delete_image_btn').val('이미지 삭제');
        }
    }

    //이미지 삭제 모드 실행
    function executeDeleteMode() {
        if ($('#delete_image_btn').val() == '이미지 삭제') {
            for (var i = 0; i < finalFiles.length; i++) {
                $('#image' + i).append('<span class="selector_box" style="position:relative; left:-50px;"><input class="selector" id="selector' + i + '" type="checkbox" name="selectedImage[]"></span>');
            }
            $('#select_delete_image_btn_box').html('<input id="delete_image_complete_btn" type="button" class="btn btn-default" value="삭제 완료" onclick="deleteImage()">');
            $('#delete_image_btn').val('취소');

        } else {
            // for(var i = 0; i < finalFiles.length; i ++) {
            //     $('.selector_box').remove('.selector');
            // }
            $('.selector_box').remove();
            $('#select_delete_image_btn_box').html('');
            $('#delete_image_btn').val('이미지 삭제');
        }
        // finalFiles = [];//압축 파일된 데이터 배열 clear
        // $('#file_input').val();
        // $('#upload_image_box').html('');//img태그 삭제
        // $('#delete_image_btn_box').html('');//이미지 삭제 버튼 제거
    }

    //서버로 데이터 전송
    function uploadData() {
        var article = $('#article').val();
        console.log(article);
        var formData = new FormData();
        for (var i = 0; i < finalFiles.length; i++) {
            formData.append('imageFile' + (i + 1), finalFiles[i]);
        }

        if (article != null && article != '') {
            formData.append('article', article);
        }

        $.ajax({
            url: '/post',
            type: 'POST',
            processData: false,
            contentType: false,
            data: formData
        }).done(function (result) {
            console.log('success');
            if (result == 0) {
                alert('업로드 실패');
            } else if (result == 1) {
                alert('업로드 성공');
                location.href = '/';
            } else {
                alert('업로드 실패');
            }

        }).fail(function () {
            console.log('fail');
            alert('업로드 실패');
        });
    }

    $(document).ready(function () {

        $('#file_input').on('change', function () {
            // finalFiles.length = 0;//최종 파일 배열 비우기
            var fileData = document.querySelector('#file_input');
            var files = fileData.files;

            //사진 수는 6장으로 제한
            if (finalFiles.length + files.length > 6) {
                alert('사진은 6장까지 업로드할 수 있습니다.');
                $('#file_input').val();//파일을 비워준다.
                return;
            }
            var filesArr = Array.prototype.slice.call(files);

            var index = finalFiles.length;
            filesArr.forEach(function (f) {
                var file = f;
                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function () {
                    var tempImage = new Image();
                    tempImage.src = reader.result;
                    tempImage.onload = function () {
                        var canvas = document.createElement('canvas');
                        var canvasContext = canvas.getContext('2d');

                        var maxSize = 720;

                        var width = tempImage.width;
                        var height = tempImage.height;

                        if (width > height) {
                            if (width > maxSize) {
                                height *= maxSize / width;
                                width = maxSize;
                            }
                        } else {
                            if (height > maxSize) {
                                width *= maxSize / height;
                                height = maxSize;
                            }
                        }

                        canvas.width = width;
                        canvas.height = height;

                        canvasContext.drawImage(this, 0, 0, width, height);

                        var dataURI = canvas.toDataURL("image/*");
                        finalFiles[index] = dataURLToBlob(dataURI);//배열에 uri 추가
                        $('#upload_image_box').append(
                            '<span id="image' + index + '">' +
                            '<span style="position:relative">' +
                            '<label id="selector_label'+index+'" for="selector' + index + '">' +
                            '<img src=' + dataURI + ' alt="이미지" width="75" height="75" class ="upload_image">' +
                            '</label>' +
                            '</span>' +
                            '</span>'
                        );

                        index++;

                        //이미지 삭제 버튼 삽입
                        $('#delete_image_btn_box').html('<input id="delete_image_btn" type="button" class="btn btn-danger" value="이미지 삭제" onclick="executeDeleteMode()">');
                        console.log('finalFiles count: ' + finalFiles.length);
                        for (var i = 0; i < finalFiles.length; i++) {
                            console.log('finalFiles' + i + ': ' + finalFiles[i]);
                        }
                    };
                };

            });
        });

        //이미지 삭제 버튼을 눌러서 이미지 삭제 모드로 진입한 상태에서 이미지 선택을 누르면 이미지 삭제 모드 취소
        $('#image_select_button').on('click', function () {
            $('.selector_box').remove();
            $('#select_delete_image_btn_box').html('');
            $('#delete_image_btn').val('이미지 삭제');
        });

        $('#submit').on('click', function (e) {
            e.preventDefault();
            if (finalFiles.length == 0) {
                alert('이미지를 선택해주세요');
            } else {
                uploadData();
            }
        })
    });
</script>
</html>