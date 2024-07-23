function validateUserId() {
    const userId = document.getElementById('userId').value;
    const validationMessage = document.getElementById('validationMessage');

    if (userId === '') {
        validationMessage.textContent = '아이디를 입력해주세요.';
        validationMessage.style.color = 'red';
        return;
    }

    fetch(`/user/validateId?userId=${userId}`)
        .then(response => response.json())
        .then(data => {
            if (data.valid) {
                validationMessage.textContent = '사용할 수 있는 아이디입니다.';
                validationMessage.style.color = 'green';
            } else {
                validationMessage.textContent = '이미 사용 중인 아이디입니다.';
                validationMessage.style.color = 'red';
            }
        })
        .catch(error => {
            validationMessage.textContent = '아이디 유효성 검사 중 오류가 발생했습니다.';
            validationMessage.style.color = 'red';
        });
}

function validatePassword() {
    const password1 = document.getElementById('password1').value;
    const password2 = document.getElementById('password2').value;
    const passwordMessage = document.getElementById('passwordMessage');
    const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{10,}$/;

    if (!passwordPattern.test(password1)) {
        passwordMessage.textContent = '비밀번호는 영문, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.';
        passwordMessage.style.color = 'red';
    } else {
        passwordMessage.textContent = ''; // 조건 메시지 초기화
        if (password1 !== password2) {
            passwordMessage.textContent = '비밀번호가 일치하지 않습니다.';
            passwordMessage.style.color = 'red';
        } else {
            passwordMessage.textContent = '비밀번호가 일치합니다.';
            passwordMessage.style.color = 'green';
        }
    }
}

function sendNumber() {
    $("#mail_number").css("display", "block");
    $.ajax({
        url: "/mail/isAuthenticated",
        type: "POST",
        dataType: "text", 
        data: JSON.stringify({ "mail": $("#emailPrefix").val() + "@" + $("#emailDomain").val() }),
        contentType: "application/json",
        headers: {
            "X-CSRF-TOKEN": getCsrfToken()
        },
        success: function(data) {
            alert("인증번호가 이메일로 발송되었습니다. 이메일을 확인해주세요.");
            $("#Confirm").val(data); 
        },
        error: function(xhr, status, error) {
            alert("인증번호 발송에 실패했습니다. 오류: " + xhr.responseText);
            console.log("Error Status: " + status);
            console.log("Error: " + error);
            console.log("XHR Response Text: " + xhr.responseText);
            console.log("XHR Object: ", xhr);
        }
    });
}

// 이메일인증 토큰
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function confirmNumber() {
    var number1 = $("#number").val();
    var number2 = $("#Confirm").val();

    console.log("Input Number:", number1);
    console.log("Stored Number:", number2);

    if (number1 === number2) {
        alert("인증되었습니다.");
        $("#emailVerified").val("true");
    } else {
        alert("번호가 다릅니다.");
        $("#emailVerified").val("false");
    }
}


$(document).ready(function() {
    // 폼의 submit 이벤트에 confirmDeletion 함수 추가
    $("#deleteForm").on("submit", function(event) {
        confirmDeletion(event);
    });

    // 이메일 인증 관련 폼의 ID와 클래스를 정확히 설정합니다.
    $(".create-user-form").submit(function(event) {
        var emailVerified = $("#emailVerified").val();
        if (emailVerified !== "true") {
            alert("이메일 인증이 완료되지 않았습니다.");
            event.preventDefault(); // 폼 제출 막기
        }
    });
});
