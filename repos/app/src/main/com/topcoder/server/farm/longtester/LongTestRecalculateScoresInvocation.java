/*
 * LongTestRecalculateScoresInvocation
 * 
 * Created 10/03/2006
 */
package com.topcoder.server.farm.longtester;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.type.longtest.FarmLongTester;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTestRecalculateScoresInvocation.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestRecalculateScoresInvocation implements Invocation {
    private Solution solution;
    private LongRoundScores results;

    public LongTestRecalculateScoresInvocation() {
    }
    
    public LongTestRecalculateScoresInvocation(Solution solution, LongRoundScores results) {
       this.solution = solution;
       this.results = results;
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new FarmLongTester(context.getRootFolder(), context.getWorkFolder()).recalculateFinalScores(solution, results);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    public LongRoundScores getResults() {
        return results;
    }

    public void setResults(LongRoundScores results) {
        this.results = results;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.solution = (Solution) reader.readObject();
        this.results = (LongRoundScores) reader.readObject();
        
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(solution);
        writer.writeObject(results);
        
    }
}
