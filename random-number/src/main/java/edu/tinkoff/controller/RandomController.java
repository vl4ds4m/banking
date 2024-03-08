package edu.tinkoff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/random")
public class RandomController {
    private int randomNumber = 0;

    @Autowired
    public void setRandomNumber(int number) {
        randomNumber = number;
    }

    @GetMapping("/num")
    public int getRandomNumber() {
        System.out.println("GET-request: random number");
        return randomNumber;
    }
}
