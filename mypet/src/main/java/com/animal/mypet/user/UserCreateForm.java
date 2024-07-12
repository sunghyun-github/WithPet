package com.animal.mypet.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

    @NotEmpty(message = "사용자 역할은 필수항목입니다.")
    private String userRole;
	
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
    
    @NotEmpty(message = "이메일 필수항목입니다.")
    @Email
    private String userEmail;
    

}
