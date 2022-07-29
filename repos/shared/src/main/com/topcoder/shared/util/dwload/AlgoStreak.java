package com.topcoder.shared.util.dwload;

abstract class AlgoStreak {
    public static final int MARATHON_RATING_INCREASE = 8;
    public static final int MARATHON_RATING_INCREASE_ALL = 9;
    public static final int MARATHON_CONSECUTIVE_TOP_5 = 10;
    public static final int MARATHON_CONSECUTIVE_TOP_10 = 11;

    private int streakType;
    protected int coderId;
    private int startRoundId;
    private int endRoundId;
    private int length = 0;
    
    public AlgoStreak(int streakType) {
        this.streakType = streakType;
    }
    
    private StreakRow flush(boolean isCurrent) {
        return length > 1 ? new StreakRow(coderId, streakType, startRoundId, endRoundId, length, isCurrent) : null;
    }

    public StreakRow flush() {
        return flush(true);
    }
    public StreakRow add(int coderId, int roundId, int placed, int rating, int roundTypeId) {
        StreakRow sr = null;

        if (this.coderId != coderId) {
            sr = flush();
            length = 0;
            this.coderId = coderId;
            reset();
        }

        if (skipRound(roundTypeId)) {
            return sr;
        }

        if (addToStreak(placed, rating)) {
            if (length == 0) {
                startRoundId = roundId;
            }
            endRoundId = roundId;
            length++;
        } else {
            if (length > 1) {
                sr = flush(false);
            }
            length = 0;
        }

        return sr;
    }


    protected boolean skipRound(int roundTypeId) {
        return false;
    }

    protected abstract boolean addToStreak(int placed, int rating);

    protected void reset() {
    }
    
    
    public static class StreakRow {
        private int coderId;
        private int streakType;
        private int startRoundId;
        private int endRoundId;
        private int length;
        private boolean isCurrent;
        
        
        public StreakRow(int coderId, int streakType, int startRoundId, int endRoundId, int length, boolean isCurrent) {
            this.coderId = coderId;
            this.streakType = streakType;
            this.startRoundId = startRoundId;
            this.endRoundId = endRoundId;
            this.length = length;
            this.isCurrent = isCurrent;
        }
        
        public int getCoderId() {
            return coderId;
        }
        public int getEndRoundId() {
            return endRoundId;
        }
        public boolean isCurrent() {
            return isCurrent;
        }
        public int getLength() {
            return length;
        }
        public int getStartRoundId() {
            return startRoundId;
        }
        public int getStreakType() {
            return streakType;
        }
    }
}
