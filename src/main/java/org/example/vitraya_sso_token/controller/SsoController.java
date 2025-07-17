package org.example.vitraya_sso_token.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sso")
public class SsoController {

    @GetMapping("/login")
    public String login() {
        return "sso-login";
    }
    
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "SSO test endpoint working!";
    }
}