/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.mpsqas;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * <p>
 * This class holds lookup values.
 * Users should get instance via {@link com.topcoder.client.mpsqasApplet.object.MainObjectFactory#getLookupValues()}.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is mutable and not thread-safe. It's expected to be populated once (from a single thread),
 * so later it can be used concurrently only for reading (calling getters).
 * </p>
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 * <li>
 * Added property for custom build settings. Specifically, added {@link #customBuildSettings},
 * {@link #getCustomBuildSettings()}, {@link #setCustomBuildSettings(HashMap)} and updated
 * {@link #customReadObject(CSReader)} and {@link #customWriteObject(CSWriter)}.
 * </li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class LookupValues implements CustomSerializable, Cloneable, Serializable {

    /**
     * Serial version UID constant.
     */
    private static final long serialVersionUID = -8175396413532590319L;

    /**
     * <p>
     * Problem round types.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     */
    private ArrayList<ProblemRoundType> problemRoundTypes;

    /**
     * <p>
     * Custom build settings, grouped by type. Key represents a type (see constants defined
     * in {@link com.topcoder.netCommon.mpsqas.CustomBuildSetting}), value represents
     * a list of settings of the corresponding type.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     *
     * @since 1.1
     */
    private HashMap<Integer, ArrayList<CustomBuildSetting>> customBuildSettings;

    /**
     * Creates instance.
     */
    public LookupValues() {
    }

    /**
     * Gets problem round types.
     *
     * @return Problem round types.
     */
    public ArrayList<ProblemRoundType> getProblemRoundTypes() {
        return problemRoundTypes;
    }

    /**
     * Sets problem round types.
     *
     * @param problemRoundTypes Problem round types.
     */
    public void setProblemRoundTypes(ArrayList<ProblemRoundType> problemRoundTypes) {
        this.problemRoundTypes = problemRoundTypes;
    }

    /**
     * Gets custom build settings
     *
     * @return Custom build settings, grouped by type. Key represents a type (see constants defined
     * in {@link com.topcoder.netCommon.mpsqas.CustomBuildSetting}), value represents
     * a list of settings of the corresponding type.
     *
     * @since 1.1
     */
    public HashMap<Integer, ArrayList<CustomBuildSetting>> getCustomBuildSettings() {
        return customBuildSettings;
    }

    /**
     * Sets custom build settings.
     *
     * @param customBuildSettings Custom build settings, grouped by type. Key represents a type (see constants defined
     * in {@link com.topcoder.netCommon.mpsqas.CustomBuildSetting}), value represents
     * a list of settings of the corresponding type.
     *
     * @since 1.1
     */
    public void setCustomBuildSettings(HashMap<Integer, ArrayList<CustomBuildSetting>> customBuildSettings) {
        this.customBuildSettings = customBuildSettings;
    }

    /**
     * Performs de-serialization.
     *
     * @param reader Reader.
     *
     * @throws IOException If any I/O error occurs.
     * @throws ObjectStreamException If any stream error occurs.
     **/
    @SuppressWarnings("unchecked")
    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        problemRoundTypes = reader.readArrayList();
        customBuildSettings = reader.readHashMap();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeArrayList(problemRoundTypes);
        writer.writeHashMap(customBuildSettings);
    }
}
