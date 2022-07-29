/*
 * TestDataGenerator
 *
 * Created 08/03/2006
 */
package com.topcoder.farm.test.common;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TestDataGenerator { // extends DataGenerator {
//    private static final int MAX_CLIENTS = 50;
//
//    public TestDataGenerator(String hibernateConfig) {
//        super(hibernateConfig);
//    }
//
//    protected void generateData(Session session) {
//        //PROPS 1: OS=Windows Mem=512MB ProcessorType=1
//        ProcessorProperties p1 = buildProperties(1, 1024, TestDataPropertiesHelper.OS_WINDOWS, 1, session);
//        //PROPS 2: OS=Windows Mem=512MB ProcessorType=2
//        ProcessorProperties p2 = buildProperties(2, 512, TestDataPropertiesHelper.OS_WINDOWS, 2, session);
//        //PROPS 3: OS=Linux Mem=512MB ProcessorType=1
//        ProcessorProperties p3 = buildProperties(1, 512, TestDataPropertiesHelper.OS_LINUX, 3, session);
//        //PROPS 4: OS=Linux Mem=1024MB ProcessorType=2
//        ProcessorProperties p4 = buildProperties(2, 1024, TestDataPropertiesHelper.OS_LINUX, 4, session);
//
//        session.saveOrUpdate(p1);
//        session.saveOrUpdate(p2);
//        session.saveOrUpdate(p3);
//        session.saveOrUpdate(p4);
//        ProcessorProperties[] props = new ProcessorProperties[]{p1, p2, p3, p4};
//
//        for (int i = 0; i < props.length; i++) {
//            for (int procNum = 0; procNum < 2; procNum++) {
//                ProcessorData pr = createOrFindProcessor("PR"+(i+1)+"-"+(procNum+1), session);
//                pr.setActive(true);
//                pr.setProperties(props[i]);
//                pr.setMaxRunnableTasks(TestDataPropertiesHelper.MAX_RUNNABLE_TASKS[i]);
//                session.saveOrUpdate(pr);
//            }
//        }
//
//        for (int i = 0; i <= MAX_CLIENTS; i++) {
//            ClientData cl = createOrFindClient("CL"+(i+1), session);
//            cl.setPriority(i / 5 + 1);
//            cl.setTtl(120000);
//            cl.setAssignationTtl(120000);
//            session.saveOrUpdate(cl);
//        }
//    }
//
//    
//    private ProcessorProperties buildProperties(int processorType, int maxMemory, String osName, int procPropId, Session session) {
//        ProcessorProperties properties = createOrFindProcessorProperties("Test Properties ["+processorType+", "+maxMemory+","+osName+"]", session);
//        properties.clearAllProperties();
//        PropertiesBuilder b = new PropertiesBuilder(properties);
//
//        if (osName.equals(TestDataPropertiesHelper.OS_WINDOWS)) {
//            b.osWindows();
//        } else {
//            b.osLinux();
//        }
//        b.memoryAvailable(maxMemory);
//        properties.addProperty(TestDataPropertiesHelper.PROCESSOR_TYPE, new Integer(processorType));
//        properties.addProperty(TestDataPropertiesHelper.PROC_PROP_ID, new Integer(procPropId));
//
//        return properties;
//    }
//    
//    public static void main(String[] args) throws Exception {
//        if (args.length != 5) {
//            System.out.println("You must specify: [dropCreate|update|none]");
//            System.out.println(HibernateUtil.PROD_HIBERNATE_CFG_XML+" must be in the classpath");
//            return;
//        }
//        int action = "dropCreate".equals(args[0]) ? DROP_CREATE : "update".equals(args[0]) ? UPDATE : NODDL;
//        new TestDataGenerator(HibernateUtil.PROD_HIBERNATE_CFG_XML).generate(action);
//    }
}
