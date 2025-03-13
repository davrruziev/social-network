package api.giybat.uz.service;

import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBundleService bundleService;

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

    public Boolean check(String phoneNumber, String code, AppLanguage lang) {
         // find last sms by phoneNumber
        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findTop1ByPhoneOrderByCreatedDateDesc(phoneNumber);
        if (optional.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("sms.by.phoneNumber", lang));
        }
          //check code
        SmsHistoryEntity entity = optional.get();
        if(!entity.getCode().equals(code)) {
            throw new AppBadException(bundleService.getMessage("check.code", lang));
        }
         //check time
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if(LocalDateTime.now().isAfter(expDate)) {
            throw new AppBadException(bundleService.getMessage("check.time", lang));
        }
        return true;
    }

}
