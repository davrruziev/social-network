package api.giybat.uz.controller;

import api.giybat.uz.dto.MessageDTO;
import api.giybat.uz.service.EmailSendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail-send")
public class EmailController {

    @Autowired
    private EmailSendingService emailSendingService;

//    @PostMapping("/simple")
//    public String sendEmail(@RequestBody MessageDTO dto) {
//        return emailSendingService.sendRegistrationEmail(dto);
//    }
}
