package com.revature.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.refs.RefFormat;
import io.swagger.parser.SwaggerParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class SwaggerfileController {

    @GetMapping("/test")
    public ResponseEntity<Void> test() {
        System.out.println("Hello world!");
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadSwaggerFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        
        InputStream jsonStream = file.getInputStream();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonStream);
        
        Swagger swag = new SwaggerParser().read(node);
        
        swag.getDefinitions();
        System.out.println(swag.getInfo().getDescription());
        
        for (Map.Entry<String, Path> entry : swag.getPaths().entrySet()) {
            System.out.println(entry.getKey());
            printOperations(swag, entry.getValue().getOperationMap());
        }
        
        return ResponseEntity.ok().build();
    }
    
    private static void printOperations(Swagger swag, Map<HttpMethod, Operation> operationMap) {
        for (Map.Entry<HttpMethod, Operation> op : operationMap.entrySet()) {
            System.out.println(op.getKey() + " - " + op.getValue().getOperationId());
            System.out.println("Parameters: ");
            for (Parameter p : op.getValue().getParameters()) {
                System.out.println(p.getName() + ", ");
                
                if (p instanceof BodyParameter) {
                    BodyParameter bp = (BodyParameter) p;
                    Model schema = bp.getSchema();
                    Map<String, Property> map = schema.getProperties();
                    
                    
                    System.out.println(schema.getReference());
                }
                
            }
            System.out.println();
        }
    }
    
}
