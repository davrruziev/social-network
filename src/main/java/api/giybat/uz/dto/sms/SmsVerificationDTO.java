package api.giybat.uz.dto.sms;

import jakarta.validation.constraints.NotBlank;

public class SmsVerificationDTO {
    @NotBlank(message = "phone required")
    private String phone;
    @NotBlank(message = "code required")
    private String code;

    public @NotBlank(message = "phone required") String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "phone required") String phone) {
        this.phone = phone;
    }

    public @NotBlank(message = "code required") String getCode() {
        return code;
    }

    public void setCode(@NotBlank(message = "code required") String code) {
        this.code = code;
    }
}



