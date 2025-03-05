package api.giybat.uz.service;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
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


    public AppResponse<String> registration(RegistrationDTO dto) {

        //1. validation

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());

        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile);
                // send sms/email

            } else {
                throw new AppBadException("Username already exists");
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
        emailSendingService.sendRegistrationEmail(profile.getUsername(), profile.getId());

        return new AppResponse<>("success registration save ");
    }

    public String regVerification(String token) {
        try {
            Integer profileId = JwtUtil.decodeRegVer(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
                return "success verification";
            }
        } catch (Exception e) {

        }

        throw new AppBadException("Verification failed");
    }

    public ProfileDTO login(AuthDTO auth) {

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(auth.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadException("Username or password is wrong");
        }
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(auth.getPassword(), profile.getPassword())) {
            throw new AppBadException("Wrong password");
        }
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadException("User Status Wrong");
        }

        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode( profile.getUsername(), profile.getId(), response.getRoleList()));
        return response;
    }
}
