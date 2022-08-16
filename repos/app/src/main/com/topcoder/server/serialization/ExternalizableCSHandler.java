/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.Submission;
import com.topcoder.server.ejb.TestServices.to.ComponentAndDependencyFiles;
import com.topcoder.server.ejb.TestServices.to.SystemTestResult;
import com.topcoder.server.tester.CPPComponentFiles;
import com.topcoder.server.tester.DotNetComponentFiles;
import com.topcoder.server.tester.JavaComponentFiles;
import com.topcoder.server.tester.Python3ComponentFiles;
import com.topcoder.server.tester.PythonComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.problem.TestCase;

/**
 * CSHandler used for Externalizable types.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Register {@link Python3ComponentFiles} class.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), liuliquan
 * @version 1.1
 */
public class ExternalizableCSHandler extends CSHandler {
    private static final Map writeMap;
    private static final Map readMap;

    /*
     * Just a little implementation change to avoid problems with inheritance.
     * And to avoid so many ifs and instanceof.
     */
    static {
        writeMap = new HashMap(150);
        readMap = new HashMap(150);
        registerClassID(ComponentAndDependencyFiles.class , (byte)-128);
        registerClassID(DataType.class                    , (byte)-127);
        registerClassID(Solution.class                    , (byte)-126);
        registerClassID(Submission.class                  , (byte)-125);
        registerClassID(ChallengeAttributes.class         , (byte)-124);
        registerClassID(JavaComponentFiles.class          , (byte)-123);
        registerClassID(CPPComponentFiles.class           , (byte)-122);
        registerClassID(DotNetComponentFiles.class        , (byte)-121);
        registerClassID(PythonComponentFiles.class        , (byte)-120);
        registerClassID(SystemTestResult.class            , (byte)-119);
        registerClassID(RemoteFile.class                  , (byte)-118);
        registerClassID(RoundComponent.class              , (byte)-117);
        registerClassID(SimpleComponent.class             , (byte)-116);
        registerClassID(Location.class                    , (byte)-115);

        registerClassID(Problem.class                     , (byte)-114);
        registerClassID(ProblemComponent.class            , (byte)-113);
        registerClassID(ProblemInformation.class          , (byte)-112);
        registerClassID(ComponentInformation.class        , (byte)-111);

        registerClassID(TestCase.class                    , (byte)-110);
        registerClassID(Python3ComponentFiles.class        , (byte)-109);
    }

    private static void registerClassID(Class clazz, byte classID) {
        Byte classId = new Byte(classID);
        writeMap.put(clazz, classId);
        readMap.put(classId, clazz);
    }

    protected boolean writeObjectOverride2(Object object) throws IOException {
        return false;
    }

    protected final boolean writeObjectOverride(Object object) throws IOException {
        if (writeObjectOverride2(object)) {
            return true;
        }
        Byte classId = (Byte) writeMap.get(object.getClass());
        if (classId != null) {
            writeByte(classId.byteValue());
            customWriteObject(object);
            return true;
        }
        return false;
    }

    protected final Object readObjectOverride(byte type) throws IOException {
        Class clazz = (Class) readMap.get(new Byte(type));
        if (clazz != null) {
            CustomSerializable object;
            try {
                object = (CustomSerializable) clazz.newInstance();
                object.customReadObject(this);
                return object;
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate class: " + clazz.getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access default constructor for class: " + clazz.getName());
            }
        } else {
            return readObjectOverride2(type);
        }
    }

    protected Object readObjectOverride2(byte type) throws IOException {
        return super.readObjectOverride(type);
    }
}
