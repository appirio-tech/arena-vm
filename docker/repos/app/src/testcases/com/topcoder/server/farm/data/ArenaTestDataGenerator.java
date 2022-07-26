/*
 * TestDataGenerator
 *
 * Created 08/03/2006
 */
package com.topcoder.server.farm.data;

import org.hibernate.classic.Session;

import com.topcoder.farm.controller.dao.hibernate.HibernateUtil;
import com.topcoder.farm.controller.model.ClientData;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.processor.processorproperties.PropertiesBuilder;
import com.topcoder.farm.test.common.DataGenerator;
import com.topcoder.server.farm.compiler.CompilerPropertiesBuilder;
import com.topcoder.server.farm.longtester.LongTestPropertiesBuilder;
import com.topcoder.server.farm.tester.TesterPropertiesBuilder;
import com.topcoder.shared.common.ServicesConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ArenaTestDataGenerator.java 71140 2008-06-10 05:24:27Z dbelfer $
 */
public class ArenaTestDataGenerator extends DataGenerator {
    private static final int[] ALL_COMPILE_ACTIONS = new int[] {
                                        ServicesConstants.CONTEST_COMPILE_ACTION,
                                        ServicesConstants.MPSQAS_COMPILE_ACTION,
                                        ServicesConstants.LONG_COMPILE_ACTION};

    private static final int[] ALL_TESTER_ACTIONS = new int[] {
        ServicesConstants.USER_TEST_ACTION,
        ServicesConstants.CHALLENGE_TEST_ACTION,
        ServicesConstants.SYSTEM_TEST_ACTION,
        ServicesConstants.PRACTICE_TEST_ACTION,
        ServicesConstants.MPSQAS_TEST_ACTION};

    private String[] prefixes;
    private boolean realData;
    private boolean devData;



    public ArenaTestDataGenerator(String hibernateConfig, boolean realData, boolean devData, String[] prefixes) {
        super(hibernateConfig);
        this.realData = realData;
        this.devData = devData;
        this.prefixes = prefixes;
    }

    protected void generateData(Session session) throws Exception {
        if (realData) {
            addRealData(session);
        }
        if (devData) {
            addDevData(session);
        }
    }

    protected void addRealData(Session session) throws Exception {
        //Processors: Linux MM compilers an tester
        addMMTesterProcessors(session);
        addIMMTesterProcessors(session);
        addGenericProcessors(session);
        addClients(session, "");
        for (int i = 0; i < prefixes.length; i++) {
            addClients(session, prefixes[i]);
        }

    }

    private void addClients(Session session, String prefix) {
        ClientData client;

        //MAX-NAME size = 30

        //LongTester Client on TestService
        client = createOrFindClient(prefix+"LongTester-TestSvc", session);
        client.setPriority(500);
        client.setTtl(10*24*3600*1000);
        client.setAssignationTtl(10*60*1000);
        session.saveOrUpdate(client);

        //LongTester Client on MPSQAS TestService
        client = createOrFindClient(prefix+"LongTester-MPSQAS", session);
        client.setPriority(450);
        client.setTtl(10*24*3600*1000);
        client.setAssignationTtl(10*60*1000);
        session.saveOrUpdate(client);


        //Client for compiler on MPSQAS
        client = createOrFindClient(prefix+"Compiler-MPSQAS", session);
        client.setPriority(200);
        client.setTtl(30000);
        client.setAssignationTtl(25000);
        session.saveOrUpdate(client);

        //Client for LongSubmitter compiler in TestServices
        client = createOrFindClient(prefix+"Compiler-LS-TestSvc", session);
        client.setPriority(400);
        client.setTtl(30000);
        client.setAssignationTtl(25000);
        session.saveOrUpdate(client);

        //Client for RoundRecompilation in TestServices
        client = createOrFindClient(prefix+"Compiler-ReRound", session);
        client.setPriority(400);
        client.setTtl(30000);
        client.setAssignationTtl(25000);
        session.saveOrUpdate(client);

        //Client for SRMCompiler on processor
        client = createOrFindClient(prefix+"SRMComp-Processor", session);
        client.setPriority(400);
        client.setTtl(60000);
        client.setAssignationTtl(25000);
        session.saveOrUpdate(client);

        //FIXME VERIFY THIS
        //Client for SRMCompiler admin tools
        client = createOrFindClient(prefix+"SRMComp-admin", session);
        client.setPriority(350);
        client.setTtl(60*60*1000);
        client.setAssignationTtl(25000);
        session.saveOrUpdate(client);

        //SRM Tester
        //Test enqueued by CoreServices are: user, challenge, and system (system with HIGH priority)
        client = createOrFindClient(prefix+"SRMTest-Core", session);
        client.setPriority(400);
        client.setTtl(2*24*3600*1000);
        client.setAssignationTtl(15000);
        session.saveOrUpdate(client);

        //MPSQAS Services client used for SRM testing
        client = createOrFindClient(prefix+"MPSQASTest-MPSQAS", session);
        client.setPriority(200);
        client.setTtl(60*60*1000);
        client.setAssignationTtl(15000);
        session.saveOrUpdate(client);
    }


    private void addGenericProcessors(Session session) {
        //Compiles everything, non MM testing
        ProcessorProperties lxPr = buildRealProperties("Linux Generic Processor", true, 2048, new int[]{1,3,6}, new int[]{13,14}, true, false, Boolean.valueOf(false), true, session);
        session.saveOrUpdate(lxPr);

        //Generics processor for linux
        ProcessorData pr;
        for (int i=0; i < 15; i++) {
            pr = createOrFindProcessor("LX-GEN-"+(i+1), session);
            pr.setActive(true);
            pr.setProperties(lxPr);
            pr.setMaxRunnableTasks(2);
            session.saveOrUpdate(pr);
        }

        //Compiles everything, non MM testing
        ProcessorProperties wnPr = buildRealProperties("Windows Generic Processor", false, 2048, new int[]{4,5}, new int[]{13,14,15,16}, true, false, null, true, session);
        session.saveOrUpdate(wnPr);

        for (int i=0; i < 5; i++) {
            pr = createOrFindProcessor("WN-GEN-"+(i+1), session);
            pr.setActive(true);
            pr.setProperties(wnPr);
            pr.setMaxRunnableTasks(2);
            session.saveOrUpdate(pr);
        }
    }


    private void addMMTesterProcessors(Session session) {
//        ProcessorProperties lxMMC = buildRealProperties("Linux MM Compiler", true, 2048, new int[]{1,3,6}, new int[]{13,14}, true, false, Boolean.valueOf(false), false, session);
//        session.saveOrUpdate(lxMMC);

        ProcessorProperties lxMMT = buildRealProperties("Linux MM Tester", true, 2048, new int[]{1,3,6}, new int[]{13,14}, false, true, Boolean.valueOf(false), false, session);
        session.saveOrUpdate(lxMMT);

        ProcessorData pr;

        //3 tester for Java/C/Python
        pr = createOrFindProcessor("LX-MM-TEST-CJP-1", session);
        pr.setActive(true);
        pr.setProperties(lxMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("LX-MM-TEST-CJP-2", session);
        pr.setActive(true);
        pr.setProperties(lxMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("LX-MM-TEST-CJP-3", session);
        pr.setActive(true);
        pr.setProperties(lxMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        //Processors: Windows MM compilers and tester
        ProcessorProperties wnMMT = buildRealProperties("Windows MM Tester", false, 2048, new int[]{4,5}, new int[]{13,14}, false, true, Boolean.valueOf(false), false, session);
        session.saveOrUpdate(wnMMT);

        //3 NET tester
        pr = createOrFindProcessor("WN-MM-TEST-NET-1", session);
        pr.setActive(true);
        pr.setProperties(wnMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("WN-MM-TEST-NET-2", session);
        pr.setActive(true);
        pr.setProperties(wnMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("WN-MM-TEST-NET-3", session);
        pr.setActive(true);
        pr.setProperties(wnMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);
    }


    private void addIMMTesterProcessors(Session session) {
        ProcessorProperties lxMMC = buildRealProperties("Linux IMM Compiler", true, 8192, new int[]{1,3,6}, new int[]{15,16}, true, false, Boolean.valueOf(true), false, session);
        session.saveOrUpdate(lxMMC);

        ProcessorProperties lxMMT = buildRealProperties("Linux IMM Tester", true, 8192, new int[]{1,3,6}, new int[]{15,16}, false, true, Boolean.valueOf(true), false, session);
        session.saveOrUpdate(lxMMT);

        //2 Java/C/Python
        ProcessorData pr = createOrFindProcessor("LX-IMM-COMP-CJP-1", session);
        pr.setActive(true);
        pr.setProperties(lxMMC);
        pr.setMaxRunnableTasks(2);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("LX-IMM-COMP-CJP-2", session);
        pr.setActive(true);
        pr.setProperties(lxMMC);
        pr.setMaxRunnableTasks(2);
        session.saveOrUpdate(pr);

        //3 tester for Java/C/Python
        pr = createOrFindProcessor("LX-IMM-TEST-CJP-1", session);
        pr.setActive(true);
        pr.setProperties(lxMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("LX-IMM-TEST-CJP-2", session);
        pr.setActive(true);
        pr.setProperties(lxMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("LX-IMM-TEST-CJP-3", session);
        pr.setActive(true);
        pr.setProperties(lxMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        //Processors: Windows MM compilers and tester
//        ProcessorProperties wnMMC = buildRealProperties("Windows IMM Compiler", false, 8192, new int[]{4,5}, new int[]{15,16}, true, false);
//        session.saveOrUpdate(wnMMC);
//
        ProcessorProperties wnMMT = buildRealProperties("Windows IMM Tester", false, 8192, new int[]{4,5}, new int[]{15,16}, false, true, Boolean.valueOf(true), false, session);
        session.saveOrUpdate(wnMMT);

        //2 NET compiler
//        pr = new ProcessorData();
//        pr.setActive(true);
//        pr.setName("WN-IMM-COMP-NET-1");
//        pr.setProperties(wnMMC);
//        pr.setMaxRunnableTasks(2);
//        session.saveOrUpdate(pr);
//
//        pr = new ProcessorData();
//        pr.setActive(true);
//        pr.setName("WN-IMM-COMP-NET-2");
//        pr.setProperties(wnMMC);
//        pr.setMaxRunnableTasks(2);
//        session.saveOrUpdate(pr);

        //3 net tester
        pr = createOrFindProcessor("WN-IMM-TEST-NET-1", session);
        pr.setActive(true);
        pr.setProperties(wnMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("WN-IMM-TEST-NET-2", session);
        pr.setActive(true);
        pr.setProperties(wnMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);

        pr = createOrFindProcessor("WN-IMM-TEST-NET-3", session);
        pr.setActive(true);
        pr.setProperties(wnMMT);
        pr.setMaxRunnableTasks(1);
        session.saveOrUpdate(pr);
    }
    
    private void addDevData(Session session) {
        ProcessorProperties lxTestMMC = buildRealProperties("Linux Dev Generic", true, 1024, new int[]{1,3,6}, new int[]{13,14,15,16}, true, true, Boolean.valueOf(false), true, session);
        session.saveOrUpdate(lxTestMMC);

        ProcessorData pr = createOrFindProcessor("PR-LX-1", session);
        pr.setActive(true);
        pr.setProperties(lxTestMMC);
        pr.setMaxRunnableTasks(2);
        session.saveOrUpdate(pr);

        ProcessorProperties wnTestMMC = buildRealProperties("Windows Dev Generic", true, 1024, new int[]{4,5}, new int[]{13,14,15,16}, true, true, Boolean.valueOf(false), true, session);
        session.saveOrUpdate(wnTestMMC);

        pr  = createOrFindProcessor("PR-WN-1", session);
        pr.setActive(true);
        pr.setProperties(wnTestMMC);
        pr.setMaxRunnableTasks(2);
        session.saveOrUpdate(pr);
    }


    private ProcessorProperties buildRealProperties(String description, boolean linuxOs, int maxMemory,
                int[] langs, int[] longTesterRoundTypes,
                boolean compiler, boolean longTester,
                Boolean threadingAllowed, boolean srmTester, Session session) {

        ProcessorProperties properties = createOrFindProcessorProperties(description, session);
        properties.clearAllProperties();
        PropertiesBuilder b = new PropertiesBuilder(properties);
        b.memoryAvailable(maxMemory);
        if (linuxOs) {
            b.osLinux();
        } else {
            b.osWindows();
        }

        if (longTester) {
            LongTestPropertiesBuilder testBuilder = new LongTestPropertiesBuilder(properties);
            testBuilder.languageIds(langs);
            testBuilder.roundTypes(longTesterRoundTypes);
        }
        if (compiler) {
            CompilerPropertiesBuilder compBuilder = new CompilerPropertiesBuilder(properties);
            compBuilder.languageIds(langs);
            compBuilder.compilerActionIds(ALL_COMPILE_ACTIONS);
            if (threadingAllowed != null) {
                compBuilder.threadingAllowed(threadingAllowed.booleanValue());
            }
        }
        if (srmTester) {
            TesterPropertiesBuilder builder = new TesterPropertiesBuilder(properties);
            builder.languageIds(langs);
            builder.testerActionIds(ALL_TESTER_ACTIONS);
        }
        return properties;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println("You must specify: [dropCreate|update|none] testData realData devData prefixes");
            System.out.println(HibernateUtil.PROD_HIBERNATE_CFG_XML+" must be in the classpath");
            return;
        }

        int action = "dropCreate".equals(args[0]) ? DROP_CREATE : "update".equals(args[0]) ? UPDATE : NODDL;
        boolean realData = "true".equals(args[2]);
        boolean devData = "true".equals(args[3]);
        String[] prefixes = args[4].split(" ");
        new ArenaTestDataGenerator(HibernateUtil.PROD_HIBERNATE_CFG_XML, realData, devData, prefixes).generate(action);
    }
}
