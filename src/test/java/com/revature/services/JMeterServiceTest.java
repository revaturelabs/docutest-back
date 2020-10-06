package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.docutest.TestUtil;
import com.revature.models.SwaggerDocutest;
import com.revature.templates.LoadTestConfig;

class JMeterServiceTest {

    private JMeterService jm;
    private LoadTestConfig loadConfig = new LoadTestConfig();
    private static final String JMeterPropPath = "src/test/resources/test.properties";
    private static final String CSV_FILE_PATH = "./datafiles/user_0.csv";
    private static final String DIRECTORY_PATH = "./datafiles";
    private static OASService adapter = new OASService();

    private SwaggerDocutest testSpecs;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        loadConfig.setLoops(1);
        loadConfig.setRampUp(2);
        loadConfig.setThreads(10);
        loadConfig.setDuration(-1);
        loadConfig.setTestPlanName("JMeterServicesTest");

        jm = new JMeterService();
        TestUtil.initFields();

        File directory = new File(DIRECTORY_PATH);
        deleteFolder(directory);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testLoadTestingLoop() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.get));
        loadConfig.setLoops(2);
        int expectedReq = (loadConfig.getLoops() * loadConfig.getThreads());

        jm.loadTesting(testSpecs, loadConfig, JMeterPropPath);

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            int counter = getCounter(reader);
            System.out.println("Expected Request Count: " + expectedReq);
            System.out.println("Actual Request Count: " + counter);
            assertEquals(expectedReq, counter);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testLoadTestingLoopMultiReq() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.multi));
        loadConfig.setLoops(2);
        int expectedReq = (loadConfig.getLoops() * loadConfig.getThreads());

        jm.loadTesting(testSpecs, loadConfig, JMeterPropPath);
        for (int i = 0; i < 2; i++) {
            String filename = JMeterService.BASE_FILE_PATH + i + ".csv";
            System.out.println(filename);
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                int counter = getCounter(reader);
                System.out.println("Expected Request Count: " + expectedReq);
                System.out.println("Actual Request Count: " + counter);
                assertEquals(expectedReq, counter);
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
        }

    }

    @Test
    void testLoadTestingDuration() throws IOException {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.get));
        loadConfig.setDuration(3);
        loadConfig.setLoops(-1);

        jm.loadTesting(testSpecs, loadConfig, JMeterPropPath);

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            long diff = getDiff(reader);
            // flat amount + 5% of duration in ms
            System.out.println("Difference between expected and actual duration (ms): "
                    + Math.abs((loadConfig.getDuration() * 1000) - diff));
            assertTrue(Math
                    .abs((loadConfig.getDuration() * 1000) - diff) < (2000 + (loadConfig.getDuration() * 1000 / 20)));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testLoadTestingDurationMulti() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.multi));
        loadConfig.setDuration(10);
        loadConfig.setLoops(-1);

        jm.loadTesting(testSpecs, loadConfig, JMeterPropPath);

        for (int i = 0; i < 2; i++) {
            String filename = JMeterService.BASE_FILE_PATH + i + ".csv";
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                long diff = getDiff(reader);
                long expectedDuration = loadConfig.getDuration() * 1000;
                System.out.println(
                        "Difference between expected and actual duration (ms): " + Math.abs(expectedDuration - diff));
                System.out.println(diff);
                // flat amount + 5% of duration in ms
                assertTrue(Math.abs((expectedDuration) - diff) < (2000 + (loadConfig.getDuration() * 1000 / 20)));
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
        }

    }

    @Test
    void testHttpSamplerDistinctRequestCount() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.get));
        Set<HTTPSampler> samplers = jm.createHTTPSampler(testSpecs);
        assertEquals(1, samplers.size());
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.todos));
        samplers = jm.createHTTPSampler(testSpecs);
        assertEquals(7, samplers.size());
    }

    @Test
    void testHttpSamplerEndpoints() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.get));
        // get.json
        Set<String> expected = new HashSet<>();
        expected.add("/");
        Set<HTTPSampler> samplers = jm.createHTTPSampler(testSpecs);
        for (HTTPSampler sampler : samplers) {
            assertTrue(expected.contains(sampler.getPath()));
        }
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.todos));
        // todos.json
        expected.clear();
        expected.add("/todos");
        expected.add("/todos/truncate");
        expected.add("/todos/{id}"); // TODO needs to be changed once path var is implemented
        samplers = jm.createHTTPSampler(testSpecs);

        for (HTTPSampler sampler : samplers) {
            System.out.println(sampler.getPath());
            assertTrue(expected.contains(sampler.getPath()));
        }
    }

    @Test
    void testHttpSamplerNull() {
        assertEquals(0, jm.createHTTPSampler(null).size());
    }

    @Test
    void testHttpSamplerNoReq() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.blank));
        assertEquals(0, jm.createHTTPSampler(testSpecs).size());
    }

    @Test
    void testHttpSamplerNoHost() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.malformed));
        assertEquals(0, jm.createHTTPSampler(testSpecs).size());
    }

    @Test
    void testCreateLoopController() {
        testSpecs = new SwaggerDocutest(adapter.getRequests(TestUtil.todos));
        Set<HTTPSampler> samplerSet = jm.createHTTPSampler(testSpecs);
        for (HTTPSampler element : samplerSet) {
            LoopController testLC = (LoopController) jm.createLoopController(element, loadConfig.getLoops());
            assertEquals(loadConfig.getLoops(), testLC.getLoops());
            // way to check loadconfig elements?
        }

    }

    @Test
    void testCreateLoopControllerNull() {
        assertNull(jm.createLoopController(null, loadConfig.getLoops()));
    }

    // helper method to clear folder between each test
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // helper method for duration tests to get difference between latest starttime
    // and initial starttime, in ms
    public static long getDiff(BufferedReader reader) throws NumberFormatException, IOException {
        String dat;
        int counter = 0;
        long startTime = 0;
        String[] row = new String[3];
        while ((dat = reader.readLine()) != null) {
            if (counter != 0) {
                row = dat.split(",");

                String timestamp = row[0];
                if (counter == 1) {
                    startTime = Long.parseLong(timestamp);
                }
            }
            counter++;
        }
        return Long.parseLong(row[0]) - startTime;
    }

    // helper method to get number of httprequests sent for loop-based tests
    public static int getCounter(BufferedReader reader) throws IOException {
        int counter = 0;
        // number of distinct req
        // may need to revisit once S3 is implemented

        while (reader.readLine() != null) {
            counter++;
        }
        counter--; // decrement for header line
        return counter;
    }
}
