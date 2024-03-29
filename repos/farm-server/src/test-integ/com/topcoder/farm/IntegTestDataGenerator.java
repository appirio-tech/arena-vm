/*
 * IntegTestDataGenerator
 *
 * Created 08/03/2006
 */
package com.topcoder.farm;

import com.topcoder.farm.test.common.TestDataGenerator;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class IntegTestDataGenerator extends TestDataGenerator {

    public IntegTestDataGenerator() {
        super("hibernate-integ.cfg.xml");
    }

    public void generate() throws Exception {
        generate(TestDataGenerator.DROP_CREATE);
    }
}
