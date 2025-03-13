package api.giybat.uz.service;

import api.giybat.uz.dto.sms.SmsAuthDTO;
import api.giybat.uz.dto.sms.SmsAuthResponseDTO;
import api.giybat.uz.dto.sms.SmsRequestDTO;
import api.giybat.uz.dto.sms.SmsSendResponseDTO;
import api.giybat.uz.entity.SmsProviderTokenEntity;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.SmsProviderTokenRepository;
import api.giybat.uz.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.lang.model.element.NestingKind;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsSendService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${eskiz.url}")
    private String smsURL;
    @Value("${eskiz.login}")
    private String accountLogin;
    @Value("${eskiz.password}")
    private String accountPassword;
    private Integer smsLimit = 2;

    @Autowired
    private SmsProviderTokenRepository smsProviderTokenRepository;

    @Autowired
    private SmsHistoryService smsHistoryService;

    public void sendRegistrationSms(String phoneNumber) {

        String code = RandomUtil.getRandomSmsCode();
        String message ="Bu Eskiz dan test";
        message = String.format(message, code);

        sendSms(phoneNumber,message,code,SmsType.REGISTRATION);

    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message, String code, SmsType smsType) {

        Long count = smsHistoryService.getSmsCount(phoneNumber);
        if (count >= smsLimit) {
            System.out.println("-----Sms Limit reacherd.Phone: " + phoneNumber);
            throw new AppBadException("Sms Limit reached");
        }


        SmsSendResponseDTO result = sendSms(phoneNumber, message);
        smsHistoryService.create(phoneNumber, message, code, smsType);

        return result;

    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message) {

        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        SmsRequestDTO smsRequest = new SmsRequestDTO();
        smsRequest.setMessage(message);
        smsRequest.setMobile_phone(phoneNumber);
        smsRequest.setFrom("4546");

        HttpEntity<SmsRequestDTO> httpEntity = new HttpEntity<>(smsRequest, headers);
        String url = smsURL + "message/sms/send";

        try {

            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SmsSendResponseDTO.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error sending SMS");
            }
            return response.getBody();

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        Optional<SmsProviderTokenEntity> optional = smsProviderTokenRepository.findTop1By();
        if (optional.isEmpty()) {
            String token = getTokenFromProvider();
            SmsProviderTokenEntity entity = new SmsProviderTokenEntity();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiryDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenRepository.save(entity);
            return token;
        }

        SmsProviderTokenEntity entity = optional.get();
        if (LocalDateTime.now().isBefore(entity.getExpiryDate())) {
            return entity.getToken();
        }

        String token = getTokenFromProvider();
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setExpiryDate(LocalDateTime.now().plusMonths(1));
        smsProviderTokenRepository.save(entity);

        return token;
    }

    private String getTokenFromProvider() {

        SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);

        /// 1 -usul

        try {
            System.out.println("---------SmsSendler new Token was Token----------");
            SmsAuthResponseDTO response = restTemplate.postForObject(smsURL + "auth/login", smsAuthDTO, SmsAuthResponseDTO.class);
            System.out.println(response.getData().getToken());
            return response.getData().getToken();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        //2-usul
//        String result = restTemplate.postForObject(smsURL + "auth/login", smsAuthDTO, String.class);
//        JsonNode parent = null;
//        try {
//            parent = new ObjectMapper().readTree(result);
//            JsonNode data = parent.get("data");
//            String token = data.get("token").asText();
//            System.out.println(token);
//            return token;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

    }
}
