package com.example.radio_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TrackController {
    @RequestMapping("/home")
    public String index(){
        return "App.jsx";
    }
}

