package com.revature.services;

import com.revature.templates.LoadTestConfig;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class JMeterServices {

    public void loadTesting(String openAPI, LoadTestConfig ltg) {
        StandardJMeterEngine jm = new StandardJMeterEngine();

        // WHERE IS THIS FILE?
        JMeterUtils.loadJMeterProperties("../jmeter.properties");
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();

//        LOOP FOR ALL HTTP REQUESTS
//        Replace w/ info from OPENAPI
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("www.google.com");
        httpSampler.setPort(8080);
        httpSampler.setPath("path");
        httpSampler.setMethod("method");

//        OR use time duration
        TestElement loopCtrl = new LoopController();
        if (ltg.loops != 0) {
            ((LoopController) loopCtrl).setLoops(ltg.loops);
            ((LoopController) loopCtrl).addTestElement(httpSampler);
            ((LoopController) loopCtrl).setFirst(true);
        }

        SetupThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setNumThreads(ltg.threads);
        threadGroup.setRampUp(ltg.rampUp);
        threadGroup.setSamplerController((LoopController) loopCtrl);

        TestPlan testPlan = new TestPlan("MY TEST PLAN");

        HashTree hashTree = new HashTree();
        hashTree.add("testPlan", testPlan);
        hashTree.add("loopCtrl", loopCtrl);
        hashTree.add("threadGroup", threadGroup);
        hashTree.add("httpSampler", httpSampler);

        jm.configure(hashTree);

        // ResultCollector class tracks results

        jm.run();
    }
}
