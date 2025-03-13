package api.giybat.uz.entity;

import api.giybat.uz.enums.SmsType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_history")
public class SmsHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "phone")
    private String phone;
    @Column(name = "message",columnDefinition = "text")
    private String message;
    @Column(name = "code")
    private String code;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "sms_type")
    private SmsType smstype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public SmsType getSmstype() {
        return smstype;
    }

    public void setSmstype(SmsType smstype) {
        this.smstype = smstype;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
