package com.soli.system.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author lizhengqiang
* @since 2026-03-05 22:10
*/
@RestController
public class TestController {
    @GetMapping("/")
    public String test() {
        return "/";
    }

    @GetMapping("/admin")
    public String admin() {
        return "/admin";
    }
}
