/*
 * LongCoderItem Created 07/02/2007
 */
package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a user in a user list of a marathon round. It has extra information about the final score of system tests in
 * the marathon round.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongCoderItem.java 72424 2008-08-20 08:06:01Z qliu $
 */
public class LongCoderItem extends CoderItem implements CustomSerializable {
    /** Represents the final score of the user. */
    private Double finalPoints;

    /**
     * Creates a new instance of <code>LongCoderItem</code>. It is required by custom serialization.
     */
    public LongCoderItem() {
    }

    /**
     * Creates a new instance of <code>LongCoderItem</code>. The team member list is initialized as <code>null</code>.
     * There is no copy.
     * 
     * @param name the handle of the user.
     * @param rating the rating of the user.
     * @param points the current total score of the user.
     * @param components the status of problem components attempted by the user.
     * @param userType the type of the user.
     * @param finalPoints the final score of the user.
     * @see #getUserType()
     */
    public LongCoderItem(String name, int rating, double points, CoderComponentItem[] components, int userType,
        double finalPoints) {
        super(name, rating, points, components, userType);
        this.finalPoints = new Double(finalPoints);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        this.finalPoints = new Double(reader.readDouble());
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeDouble(finalPoints.doubleValue());
    }

    /**
     * Gets the final score of the user.
     * 
     * @return the final score.
     */
    public Double getFinalPoints() {
        return finalPoints;
    }
}
