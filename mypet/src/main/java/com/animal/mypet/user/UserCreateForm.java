package com.animal.mypet.user;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

    @Size(min=3, max=25)
    @NotEmpty(message = "사용자 ID는 필수항목입니다.")
    private String userId;
    
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String userPassword;
    
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String userPassword2;
    
    @NotEmpty(message = "사용자 이름은 필수항목입니다.")
    private String userName;
    
    @NotEmpty(message = "사용자 전화번호는 필수항목입니다.")
    private String userPhone;
    
    
    private String emailPrefix; // 개인 이메일의 앞부분
    private String emailDomain; // 개인 이메일의 도메인
    
    private boolean emailVerified; // 이메일 인증 여부를 저장하는 필드 추가

}
