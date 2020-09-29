package com.revature.docutest.services;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jorphan.collections.HashTree;

import io.swagger.models.Swagger;
import io.swagger.v3.oas.models.OpenAPI;

public class JMeterService {
    
    public HTTPSampler createHTTPSampler(Swagger input) {
        // TODO implement
    }
    
    public TestElement createLoopController(/* params? */) {
        // TODO implement
    }
    
    public ThreadGroup createThreadGroup(Swagger input) {
        // TODO implement
    }
    
    public HashTree createTestConfig(String testPlanName, HTTPSampler httpSampler, LoopController loopController, ThreadGroup threadGroup) {
        // init hashtree
        HashTree jmConfig = new HashTree();
        TestPlan testPlan = new TestPlan("testPlanName");
        
        jmConfig.add("TestPlan", testPlan);
        jmConfig.add("LoopController", loopController);
        jmConfig.add("ThreadGroup", threadGroup);
        jmConfig.add("HTTPSampler", httpSampler);
        
        return jmConfig;
        
    }
    
    public void runTest(HashTree testConfig) {
        StandardJMeterEngine jmRunner = new StandardJMeterEngine();
        jmRunner.configure(testConfig);
    }

}
