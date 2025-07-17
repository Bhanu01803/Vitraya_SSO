package org.example.vitraya_sso_token.service;

import org.example.vitraya_sso_token.model.Corporate;
import org.example.vitraya_sso_token.model.SessionData;
import org.example.vitraya_sso_token.model.UserData;
import org.example.vitraya_sso_token.model.WebToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {

    @Autowired
    private JwtService jwtService;

    public SessionData createSessionData(String mobileNumber) {
        SessionData sessionData = new SessionData();

        // Create user data
        UserData userData = new UserData();
        userData.setCreatedat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")));
        userData.setIsnewuser(true);
        userData.setLastlogintype("SSO");
        userData.setCellnumber(mobileNumber);
        userData.setBlocked("false");
        userData.setUserid(generateUserId());
        userData.setEmail("abc@abc.com");

        // Create corporate data
        Corporate corporate = new Corporate();
        corporate.setId(24L);
        corporate.setName("ASTER PRIME HOSPITAL, HYDERABAD, ANDHRA PRADESH");
        corporate.setActive(true);
        corporate.setType("HOSPITAL");
        corporate.setHospitalCode("HOS-3206");
        corporate.setBillParserIdentifier(1);
        corporate.setTeritaryHospital(false);
        corporate.setCommunicationInString("{\"claim\": {\"sms\": null, \"email\": []}, \"query\": {\"sms\": null, \"email\": []}}");

        // Create web token
        SessionData.WebToken webToken = new SessionData.WebToken();
        webToken.setExpiryat("2000-01-01");
        webToken.setToken(jwtService.generateToken(mobileNumber));
        // Change: Generate refresh token as JWT
        webToken.setRefreshToken(jwtService.generateToken(mobileNumber));

        // Convert UserData to Map
        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("createdat", userData.getCreatedat());
        userDataMap.put("isnewuser", userData.isIsnewuser());
        userDataMap.put("lastlogintype", userData.getLastlogintype());
        userDataMap.put("cellnumber", userData.getCellnumber());
        userDataMap.put("blocked", userData.getBlocked());
        userDataMap.put("userid", userData.getUserid());
        userDataMap.put("email", userData.getEmail());

        // Set session data
        sessionData.setData(userDataMap);
        sessionData.setCorporate(corporate);
        sessionData.setWebtoken(webToken);
        sessionData.setId(String.valueOf(9563L));
        sessionData.setUserid(userData.getUserid());
        sessionData.setLastactivetime(new Date()); // Changed from LocalDateTime to Date

        return sessionData;
    }

    private String generateUserId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}