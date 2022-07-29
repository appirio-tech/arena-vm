package com.topcoder.netCommon.contest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import junit.framework.TestCase;

import com.topcoder.io.serialization.basictype.BasicTypeDataInput;
import com.topcoder.io.serialization.basictype.BasicTypeDataOutput;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.SimpleCSHandler;

public final class Matrix2DTest extends TestCase {

    public void testCustomSerialization() {
        Random rand = new Random();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BasicTypeDataOutput output = new BasicTypeDataOutputImpl(baos);
        CSHandler csHandler = new SimpleCSHandler();
        csHandler.setDataOutput(output);

        ArrayList data = new ArrayList();
        data.add(new Integer(rand.nextInt()));
        Matrix2D matrix = new Matrix2D(2, 2);

        try {
            matrix.customWriteObject(csHandler);
        } catch (IOException e) {
            fail();
        }
        BasicTypeDataInput input = new BasicTypeDataInputImpl(new ByteArrayInputStream(baos.toByteArray()));
        csHandler.setDataInput(input);
        Matrix2D matrix2 = new Matrix2D();
        try {
            matrix2.customReadObject(csHandler);
        } catch (IOException e) {
            fail();
        }

        assertEquals(matrix.numRows(), matrix2.numRows());
        assertEquals(matrix.numCols(), matrix2.numCols());
    }

}
