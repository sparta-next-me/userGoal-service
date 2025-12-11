package org.nextme.userGoal_service.test;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/report")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("test")
    public void test(){
        testService.test();
    }
}
