/*
 * RoundModifiedEvent
 * 
 * Created Oct 2, 2007
 */
package com.topcoder.shared.round.events;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Event indicating that a round has been modified.<p>
 * 
 * Since a round could have different kinds of changes, 
 * this event contains a set of {@link RoundModification}
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundModifiedEvent extends RoundEvent {
    private Set<RoundModification> modifications;
    
    public RoundModifiedEvent() {
    }
    
    /**
     * Creates the event.
     * 
     * @param roundId The round Id referred by this event
     * @param roundTypeId The round type, this value can be null
     */
    public RoundModifiedEvent(int roundId, Integer roundTypeId) {
        super(roundId, roundTypeId);
        this.modifications = new HashSet<RoundModification>();
    }

    /**
     * Creates the event.
     * 
     * @param roundId The round Id referred by this event
     */
    public RoundModifiedEvent(int roundId) {
        super(roundId);
        this.modifications = new HashSet<RoundModification>();
    }
    
    /**
     * @return The modification set
     */
    public Set<RoundModification> getModifications() {
        return modifications;
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        this.modifications = (Set<RoundModification>) reader.readCollection(new HashSet<RoundModification>());
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeCollection(modifications);
    }
    
    /**
     * Add a modification to the set of modifications.
     * 
     * @param mod The modification to add
     * @return true if the modification was added, false if was not because 
     *              that kind of modification already existed.
     * 
     */
    public boolean addModification(RoundModification mod) {
        if (modifications.contains(mod)) {
            return false;
        }
        modifications.add(mod);
        return true;
    }
    
    /**
     * Base Modification class 
     */
    public static class RoundModification implements Serializable, CustomSerializable {
        private int type;

        public RoundModification() {
        }
        
                
        protected RoundModification(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public int hashCode() {
            return type;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final RoundModification other = (RoundModification) obj;
            if (type != other.type)
                return false;
            return true;
        }
        
        public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
            this.type = reader.readInt();
        }
        
        public void customWriteObject(CSWriter writer) throws IOException {
            writer.writeInt(this.type);
        }
    }
    
    /**
     * Represents a modification in the schedule of the round.
     */
    public static class ScheduleModification extends RoundModification {
        public static final int ID = 1;

        public ScheduleModification() {
            super(ID);
        }
        
    }
    /**
     * Represents a modification in the Problem set of the round 
     */
    public static class ProblemSetModification extends RoundModification {
        public static final int ID = 2;
        
        public ProblemSetModification() {
            super(ID);
        }
    }
    
    /**
     * Represents a modification in the registration of the round.
      */
    public static class RegistrationModification extends RoundModification {
        public static final int ID = 3;
        private int[] addedCoders;
        private int[] removedCoders;
 
        /**
         * This constructor is provided for serialzation purposes.
         * It should not be used.
         */
        public RegistrationModification() {
        }
        
        /**
         * Creates a RegistrationModification indicating
         * the Ids of the coders added to the round and the ids of the
         * coders removed from the round
         * 
         * @param addedCoders an int[] containing ids of the added coders
         * @param removedCoders an int[] containing ids of the removed coders
         */
        public RegistrationModification(int[] addedCoders, int[] removedCoders) {
            super(ID);
            this.addedCoders = addedCoders;
            this.removedCoders = removedCoders;
        }

        /**
         * @return The ids of the added coders
         */
        public int[] getAddedCoders() {
            return addedCoders;
        }

        /**
         * @return The ids of the removed coders
         */
        public int[] getRemovedCoders() {
            return removedCoders;
        }
        
        public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
            super.customReadObject(reader);
            this.addedCoders = (int[]) reader.readObject();
            this.removedCoders = (int[]) reader.readObject();
        }
        
        public void customWriteObject(CSWriter writer) throws IOException {
            super.customWriteObject(writer);
            writer.writeObject(this.addedCoders);
            writer.writeObject(this.removedCoders);
        }
    }
}
