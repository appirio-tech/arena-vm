/*
 * ComponentResultDisplayRenderer
 * 
 * Created 09/21/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellRenderer;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 * @author Diego Belfer (Mural)
 * @version $Id: ComponentResultDisplayRenderer.java 67962 2008-01-15 15:57:53Z mural $
 */
public class ComponentResultDisplayRenderer extends ValueTransformDecoratorCellRenderer {
    private final NumberFormat fmt = new DecimalFormat("####0.00");
    private Map specificSets = Collections.synchronizedMap(new HashMap());
    private ResultDisplayType displayType;
    private String notChallengedString;
    private boolean canShowPoints;

    public ComponentResultDisplayRenderer(TableCellRenderer renderer) {
        super(renderer);
        this.canShowPoints = true;
        this.notChallengedString = "Not Challenged";
        this.displayType = ResultDisplayType.STATUS;
    }

    public ComponentResultDisplayRenderer(RoundModel model, TableCellRenderer renderer) {
        super(renderer);
        setModel(model);
    }

    public void setModel(RoundModel model) {
        this.displayType = model.getRoundProperties().getAllowedScoreTypesToShow()[0];
        this.canShowPoints =  model.getRoundProperties().usesScore();
        this.notChallengedString = model.getRoundProperties().hasChallengePhase() ? "Not Challenged"  : "Submitted";
    }

    protected Object transform(Object value, int row, int column) {
        boolean mustShowStatus = false;
        boolean mustShowPoints = false;
        String status;
        String points = "0.00";
        if (! (value instanceof CoderComponent)) {
            if (value instanceof Integer) {
                return value.toString();
            }
            return Common.formatScore(((Double)value).doubleValue());
        }
        CoderComponent component = (CoderComponent) value;
        switch (component.getStatus().intValue()) {
        case ContestConstants.NOT_OPENED:
            status = "Unopened";
            mustShowStatus = true;
            break;
        case ContestConstants.LOOKED_AT:
            status = "Opened";
            mustShowStatus = true;
            break;
        case ContestConstants.COMPILED_UNSUBMITTED:
            status = "Compiled";
            mustShowStatus = true;
            break;
        case ContestConstants.NOT_CHALLENGED:
            points = fmt.format((component.getPoints().doubleValue() / 100.0));
            status = notChallengedString;
            mustShowPoints = canShowPoints;
            break;
        case ContestConstants.CHALLENGE_FAILED:
            points = fmt.format((component.getPoints().doubleValue() / 100.0));
            status = notChallengedString;
            mustShowPoints = canShowPoints;
            break;
        case ContestConstants.CHALLENGE_SUCCEEDED:
            status = "Challenge Succeeded";
            break;
        case ContestConstants.SYSTEM_TEST_SUCCEEDED:
            status = "Passed System Test";
            points = fmt.format((component.getPoints().doubleValue() / 100.0));
            break;
        case ContestConstants.SYSTEM_TEST_FAILED:
            status = "Failed System Test";
            break;
        default:
            throw new IllegalStateException("Invalid component state: " + component);
        }
        ResultDisplayType type = getTypeFor(component);
        if (type == null) {
            type = displayType;
        }
        if ((ResultDisplayType.POINTS.equals(type) && !mustShowStatus) || mustShowPoints) {
            return points;
        } else if (ResultDisplayType.STATUS.equals(type) || mustShowStatus || component.getStatus().intValue() < ContestConstants.SYSTEM_TEST_SUCCEEDED || component.getPassedSystemTests() == null) { 
            return status;
        } else if (ResultDisplayType.PASSED_TESTS.equals(type)) {
            return component.getPassedSystemTests().toString();
        }
        return this;
    }

    public ResultDisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(ResultDisplayType displayType) {
        this.displayType = displayType;
        specificSets.clear();
    }

    public void toggleDisplayTypeForComponent(CoderComponent component, ResultDisplayType[] resultDisplayTypes) {
        ResultDisplayType type = getTypeFor(component);
        if (type == null) {
            type = displayType;
        }
        type = getNexTo(type, resultDisplayTypes);
        setTypeFor(component, type);
    }

    private ResultDisplayType getNexTo(ResultDisplayType displayType, ResultDisplayType[] resultDisplayTypes) {
        for (int i = 0; i < resultDisplayTypes.length; i++) {
            if (displayType.equals(resultDisplayTypes[i])) {
                return resultDisplayTypes[(i + 1) % resultDisplayTypes.length];
            }
        }
        return resultDisplayTypes[0];
    }

    private void setTypeFor(CoderComponent component, ResultDisplayType type) {
        specificSets.put(component, type);
    }

    private ResultDisplayType getTypeFor(CoderComponent component) {
        return (ResultDisplayType) specificSets .get(component);
    }
}
