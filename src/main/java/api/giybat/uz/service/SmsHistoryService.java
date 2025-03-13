package api.giybat.uz.service;

import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository smsHistoryRepository;

    public void create(String phoneNumber, String message, String code, SmsType smstype) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhone(phoneNumber);
        entity.setMessage(message);
        entity.setSmstype(smstype);
        entity.setCode(code);
        entity.setCreatedDate(LocalDateTime.now());
        smsHistoryRepository.save(entity);
    }

    public Long getSmsCount(String phoneNumber) {
        LocalDateTime now = LocalDateTime.now();
        return smsHistoryRepository.countByPhoneAndCreatedDateBetween(phoneNumber, now.minusMinutes(2), now);
    }

}
