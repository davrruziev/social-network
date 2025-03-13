package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.dto.sms.SmsVerificationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private ResourceBundleService bundleService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsHistoryService smsHistoryService;


    public AppResponse<String> registration(RegistrationDTO dto, AppLanguage lang) {

        //1. validation

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());

        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile);
                // send sms/email

            } else {
                throw new AppBadException(bundleService.getMessage("email.phone.exist", lang));
            }
        }

        ProfileEntity profile = new ProfileEntity();
        profile.setName(dto.getName());
        profile.setUsername(dto.getUsername());
        profile.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));  // TODO
        profile.setStatus(GeneralStatus.IN_REGISTRATION);
        profile.setVisible(true);
        profile.setCreatedDate(LocalDateTime.now());
        profileRepository.save(profile);

        profileRoleService.created(profile.getId(), ProfileRole.ROLE_USER);
        //  emailSendingService.sendRegistrationEmail(profile.getUsername(), profile.getId(), lang);
        smsSendService.sendRegistrationSms(profile.getUsername());

        return new AppResponse<>(bundleService.getMessage("email.confirm.send", lang));
    }

    public String registrationEmailVerification(String token, AppLanguage lang) {
        try {
            Integer profileId = JwtUtil.decodeRegVer(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
                return bundleService.getMessage("success.verification", lang);
            }
        } catch (Exception e) {

        }

        throw new AppBadException(bundleService.getMessage("failed.verification", lang));
    }

    public ProfileDTO login(AuthDTO auth, AppLanguage lang) {

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(auth.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("username.password.wrong", lang));
        }
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(auth.getPassword(), profile.getPassword())) {
            throw new AppBadException(bundleService.getMessage("password.wrong", lang));
        }
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadException(bundleService.getMessage("user.status.wrong", lang));
        }

        return getLoginInResponse(profile);
    }

    public ProfileDTO registrationSmsVerification(SmsVerificationDTO dto, AppLanguage lang) {


        Optional<ProfileEntity> optinal = profileRepository.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optinal.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("profile.not.found", lang));
        }
        ProfileEntity profile = optinal.get();
        if (!profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new AppBadException(bundleService.getMessage("check.status.sms", lang));
        }
         //check code
        smsHistoryService.check(dto.getPhone(), dto.getCode(), lang);
           //Active
        profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
          //response
        return getLoginInResponse(profile);

    }

    private ProfileDTO getLoginInResponse(ProfileEntity profile) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList()));
        return response;
    }
}
