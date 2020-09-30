package com.revature.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.docutest.TestUtil;
import com.revature.templates.LoadTestConfig;

class JMeterServicesTest {
    
    private JMeterServices jm;
    private LoadTestConfig loadConfig = new LoadTestConfig();
    private TestUtil td = new TestUtil();

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        loadConfig.loops = 2;
        loadConfig.rampUp = 2;
        loadConfig.threads = 20;
        
        jm = new JMeterServices();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testTemp() {
        jm.loadTesting(td.swag1, loadConfig);
    }

}
