package com.revature.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.services.SwaggerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class SwaggerController {
    
    @Autowired
    private static SwaggerServices ss;
    private static ObjectMapper om = new ObjectMapper();
    
}
