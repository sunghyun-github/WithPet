// 사용자 아이디 검증
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

// 비밀번호 검증
function validatePassword() {
    const password1 = document.getElementById('password1') ? document.getElementById('password1').value : document.getElementById('newPassword').value;
    const password2 = document.getElementById('password2') ? document.getElementById('password2').value : document.getElementById('confirmNewPassword').value;
    const passwordMessage = document.getElementById('passwordMessage');
    
    // 대문자를 포함하는 경우
    const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{10,}$/;
    const passwordPatternLowercase = /^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[a-z\d@$!%*?&]{10,}$/;

    if (!passwordPattern.test(password1)) {
        passwordMessage.textContent = '비밀번호는 영문, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.';
        passwordMessage.style.color = 'red';
    } else if (password1 !== password2) {
        passwordMessage.textContent = '비밀번호가 일치하지 않습니다.';
        passwordMessage.style.color = 'red';
    } else {
        passwordMessage.textContent = '비밀번호가 일치합니다.';
        passwordMessage.style.color = 'green';
    }
}

// 비밀번호 찾기
function changevalidatePassword() {
    const newPassword = document.getElementById('newPassword').value;
    const confirmNewPassword = document.getElementById('confirmNewPassword').value;
    const passwordMessage = document.getElementById('passwordMessage');
    
    // 대문자를 제외한 영문 소문자, 숫자, 특수문자를 포함하여 10자 이상이어야 하는 정규식
    const passwordPattern = /^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[a-z\d@$!%*?&]{10,}$/;

    if (!passwordPattern.test(newPassword)) {
        passwordMessage.textContent = '비밀번호는 소문자, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.';
        passwordMessage.style.color = 'red';
    } else if (newPassword !== confirmNewPassword) {
        passwordMessage.textContent = '비밀번호가 일치하지 않습니다.';
        passwordMessage.style.color = 'red';
    } else {
        passwordMessage.textContent = '비밀번호가 일치합니다.';
        passwordMessage.style.color = 'green';
    }
}

// 폼 검증
function validateForm() {
    const passwordMessage = document.getElementById('passwordMessage').textContent;
    if (passwordMessage.includes('비밀번호가 일치하지 않습니다.') || 
        passwordMessage.includes('비밀번호는 영문, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.') || 
        passwordMessage.includes('비밀번호는 소문자, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.')) {
        return false; // 폼 제출 방지
    }
    return true; // 폼 제출 허용
}

// 이메일 인증
let isNumberSent = false; // 인증번호 전송 여부를 추적하는 변수

function sendNumber() {
    const emailPrefix = $("#emailPrefix").val();
    const emailDomain = $("#emailDomain").val();
    
    if (!emailPrefix || !emailDomain) {
        alert("이메일 주소를 입력해 주세요.");
        return;
    }

    $("#mail_number").css("display", "block");
    $.ajax({
        url: "/mail/isAuthenticated",
        type: "POST",
        dataType: "text", 
        data: JSON.stringify({ "mail": emailPrefix + "@" + emailDomain }),
        contentType: "application/json",
        headers: {
            "X-CSRF-TOKEN": getCsrfToken()
        },
        success: function(data) {
            alert("인증번호가 이메일로 발송되었습니다. 이메일을 확인해주세요.");
            $("#Confirm").val(data);
            isNumberSent = true; // 인증번호 전송 완료 상태로 설정
        },
        error: function(xhr, status, error) {
            alert("인증번호 발송에 실패했습니다. 오류: " + xhr.responseText);
            console.log("Error Status: " + status);
            console.log("Error: " + error);
            console.log("XHR Response Text: " + xhr.responseText);
            console.log("XHR Object: ", xhr);
            isNumberSent = false; // 인증번호 전송 실패 상태로 설정
        }
    });
}

// CSRF Token 가져오기
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

// 이메일 인증 번호 확인
function confirmNumber() {
    if (!isNumberSent) {
        alert("먼저 인증번호를 전송해 주세요.");
        return;
    }

    const number1 = $("#number").val();
    const number2 = $("#Confirm").val();

    if (number1 === number2) {
        alert("인증되었습니다.");
        $("#emailVerified").val("true");
    } else {
        alert("번호가 다릅니다.");
        $("#emailVerified").val("false");
    }
}

$(document).ready(function() {
    // 이메일 입력값이 변경될 때 확인 버튼 활성화/비활성화
    $("#emailPrefix, #emailDomain").on("input", function() {
        const emailPrefix = $("#emailPrefix").val();
        const emailDomain = $("#emailDomain").val();
        if (emailPrefix && emailDomain) {
            $("#sendBtn").prop("disabled", false);
        } else {
            $("#sendBtn").prop("disabled", true);
        }
    });

    // 이메일 인증 관련 폼의 ID와 클래스를 정확히 설정합니다.
    $(".create-user-form").submit(function(event) {
        const emailVerified = $("#emailVerified").val();
        if (emailVerified !== "true") {
            alert("이메일 인증이 완료되지 않았습니다.");
            event.preventDefault(); // 폼 제출 막기
        }
    });
});
function confirmNumber() {
    const number1 = $("#number").val();
    const number2 = $("#Confirm").val();

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
        const emailVerified = $("#emailVerified").val();
        if (emailVerified !== "true") {
            alert("이메일 인증이 완료되지 않았습니다.");
            event.preventDefault(); // 폼 제출 막기
        }
    });
});
