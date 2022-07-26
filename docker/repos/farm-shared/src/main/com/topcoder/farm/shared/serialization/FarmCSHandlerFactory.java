/*
 * FarmCSHandlerFactory
 * 
 * Created 06/27/2006
 */
package com.topcoder.farm.shared.serialization;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.customserializer.SimpleCustomSerializerProvider;

/**
 * Base Class for Farm Serializer factory
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FarmCSHandlerFactory implements CSHandlerFactory {
    private static final byte SERIALIZABLE = 99;
    
    private final Map readMap = new HashMap(50);
    private final Map writeMap = new HashMap(50);
    private final SimpleCustomSerializerProvider customSerializers = new SimpleCustomSerializerProvider();

    private byte byteID = -127;
    
    public FarmCSHandlerFactory() {
        //No more than 126 clazzes can be registered due to the way they are being registered
        customSerializers.registerSerializer(InetSocketAddress.class, new InetSocketAddressSerializer());
        addClass("com.topcoder.farm.client.command.ReportInvocationResultCommand", byteID ++);
        addClass("com.topcoder.farm.controller.api.InvocationRequest", byteID++);
        addClass("com.topcoder.farm.controller.api.InvocationResponse", byteID++);
        addClass("com.topcoder.farm.controller.command.CancelPendingRequestsCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.CountPendingRequestsCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.GetEnqueuedRequestsSummaryCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.GetInitializationDataCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.GetProcessorInitializationDataCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.MarkInvocationAsNotifiedCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.RegisterClientCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.RegisterProcessorCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.RemoveSharedObjectsCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.ReportInvocationResultCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.RequestPendingResponsesCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.ScheduleInvocationRequestCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.SetAsAvailableCommand", byteID++);
        addClass("com.topcoder.farm.controller.command.StoreSharedObjectCommand", byteID++);
        addClass("com.topcoder.farm.controller.services.ProcessorInvocation", byteID++);
        addClass("com.topcoder.farm.processor.api.ProcessorInvocationRequest", byteID++);
        addClass("com.topcoder.farm.processor.api.ProcessorInvocationResponse", byteID++);
        addClass("com.topcoder.farm.processor.command.ProcessInvocationRequestCommand", byteID++);
        addClass("com.topcoder.farm.satellite.command.UnregisteredCommand", byteID++);
        addClass("com.topcoder.farm.shared.expression.AndExpression", byteID++);
        addClass("com.topcoder.farm.shared.expression.function.CompareFunction", byteID++);
        addClass("com.topcoder.farm.shared.expression.function.ContainsFunction", byteID++);
        addClass("com.topcoder.farm.shared.expression.function.EqualFunction", byteID++);
        addClass("com.topcoder.farm.shared.expression.NotExpression", byteID++);
        addClass("com.topcoder.farm.shared.expression.OrExpression", byteID++);
        addClass("com.topcoder.farm.shared.expression.PropertyExpression", byteID++);
        addClass("com.topcoder.farm.shared.invocation.InvocationRequirements", byteID++);
        addClass("com.topcoder.farm.shared.invocation.InvocationResult", byteID++);
        addClass("com.topcoder.farm.shared.net.connection.impl.ConnectionKeepAliveMessage", byteID++);
        addClass("com.topcoder.farm.shared.net.connection.remoting.InvocationRequestMessage", byteID++);
        addClass("com.topcoder.farm.shared.net.connection.remoting.InvocationResponseMessage", byteID++);
        addClass("com.topcoder.server.common.Location", byteID++);
        addClass("com.topcoder.server.common.LongSubmissionId", byteID++);
        addClass("com.topcoder.server.common.RemoteFile", byteID++);
        addClass("com.topcoder.server.common.RoundComponent", byteID++);
        addClass("com.topcoder.server.common.Submission", byteID++);
        addClass("com.topcoder.server.common.SystemTestAttributes", byteID++);
        addClass("com.topcoder.server.common.ChallengeAttributes", byteID++);
        addClass("com.topcoder.server.common.UserTestAttributes", byteID++);
        addClass("com.topcoder.server.farm.compiler.LongCompilationInvocation", byteID++);
        addClass("com.topcoder.server.farm.longtester.FarmLongTestRequest", byteID++);
        addClass("com.topcoder.server.farm.longtester.LongTestId", byteID++);
        addClass("com.topcoder.server.farm.longtester.LongTestInvocation", byteID++);
        addClass("com.topcoder.server.farm.tester.srm.SRMSystemTestInvocation", byteID++);
        addClass("com.topcoder.server.tester.DotNetComponentFiles", byteID++);
        addClass("com.topcoder.server.tester.JavaComponentFiles", byteID++);
        addClass("com.topcoder.server.tester.CPPComponentFiles", byteID++);
        addClass("com.topcoder.server.tester.PythonComponentFiles", byteID++);
        addClass("com.topcoder.server.tester.LongSubmission", byteID++);
        addClass("com.topcoder.server.tester.Solution", byteID++);
        addClass("com.topcoder.services.tester.common.LongTestResults", byteID++);
        addClass("com.topcoder.shared.common.LongRoundScores", byteID++);
        addClass("com.topcoder.shared.common.LongRoundScores$Record", byteID++);
        addClass("com.topcoder.shared.problem.ComponentCategory", byteID++);
        addClass("com.topcoder.shared.problem.DataType", byteID++);
        addClass("com.topcoder.shared.problem.NodeElement", byteID++);
        addClass("com.topcoder.shared.problem.ProblemComponent", byteID++);
        addClass("com.topcoder.shared.problem.SimpleComponent", byteID++);
        addClass("com.topcoder.shared.problem.TestCase", byteID++);
        addClass("com.topcoder.shared.problem.TextElement", byteID++);
    }
    
    private void addClass(String name, byte id) {
        try {
            Class clazz = Class.forName(name);
            if (clazz != null) {
                registerId(clazz, id);
            }
        } catch (ClassNotFoundException e) {
        }
    }

    public void registerCustomSerializableClassID(Class clazz, byte classID) {
        if (classID == SERIALIZABLE) {
            throw new IllegalArgumentException("ClassId="+SERIALIZABLE+" reserved for serializable type");
        }
    }

    private void registerId(Class clazz, byte classID) {
        Byte classId = new Byte(classID);
        writeMap.put(clazz, classId);
        readMap.put(classId, clazz);
    }
    
    public CSHandler newInstance() {
        return new FarmCSHandler(customSerializers, writeMap, readMap, SERIALIZABLE);
    }
    
}
