package com.datatrees.rawdata.evidence.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorHandleController implements ErrorController{
	@Override  
    public String getErrorPath() {  
        return "/screen/error";  
    }  
       
    @RequestMapping  
    public String errorHandle(){  
        return getErrorPath();  
    } 
}
