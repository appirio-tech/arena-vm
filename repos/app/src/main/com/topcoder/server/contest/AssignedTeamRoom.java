package com.topcoder.server.contest;

import com.topcoder.server.common.*;

import java.util.*;

public class AssignedTeamRoom extends AssignedRoom {

    public AssignedTeamRoom(String name, int divisionID) {
        super(name, divisionID, true, false);
    }

    protected ArrayList m_teams = new ArrayList();

    public void addTeam(Team team) {
        m_teams.add(team);
    }

    public ArrayList getTeams() {
        return m_teams;
    }
}
