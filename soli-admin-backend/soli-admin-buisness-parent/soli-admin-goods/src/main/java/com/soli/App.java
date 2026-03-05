package com.soli;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@RestController
public class App 
{
    @GetMapping("/goods/test")
    public String test() {
        return "goods.test";
    }
}
