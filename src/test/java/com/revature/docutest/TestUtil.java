package com.revature.docutest;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

public class TestUtil {
    
    public static Swagger todos;
    public static Swagger get;
    public static Swagger post;
    public static Swagger delete;
    
    
    {
        initFields();
    }
    
    public static void initFields() {  
        todos = new SwaggerParser().read("src/test/resources/example.json");
        
        // maybe don't hard coded?
        get = new SwaggerParser().read("src/test/resources/get.json");
        post = new SwaggerParser().read("src/test/resources/post.json");
        delete = new SwaggerParser().read("src/test/resources/delete.json");
    }

}
