/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 2:04:12 PM
 */
package com.topcoder.server.AdminListener.response;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ContestManagementSetupMessage extends ContestManagementAck {

    private Collection roundTypes,
    seasons,
    regions,
    problemStatusTypes,
    difficultyLevels,
    divisions,
    surveyStatusTypes,
    questionTypes,
    questionStyles,
    languages;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(seasons.toArray());
        writer.writeObjectArray(regions.toArray());
        writer.writeObjectArray(roundTypes.toArray());
        writer.writeObjectArray(problemStatusTypes.toArray());
        writer.writeObjectArray(difficultyLevels.toArray());
        writer.writeObjectArray(divisions.toArray());
        writer.writeObjectArray(surveyStatusTypes.toArray());
        writer.writeObjectArray(questionTypes.toArray());
        writer.writeObjectArray(questionStyles.toArray());
        writer.writeObjectArray(languages.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        seasons = Arrays.asList(reader.readObjectArray());
        regions = Arrays.asList(reader.readObjectArray());
        roundTypes = Arrays.asList(reader.readObjectArray());
        problemStatusTypes = Arrays.asList(reader.readObjectArray());
        difficultyLevels = Arrays.asList(reader.readObjectArray());
        divisions = Arrays.asList(reader.readObjectArray());
        surveyStatusTypes = Arrays.asList(reader.readObjectArray());
        questionTypes = Arrays.asList(reader.readObjectArray());
        questionStyles = Arrays.asList(reader.readObjectArray());
        languages = Arrays.asList(reader.readObjectArray());
    }


    public ContestManagementSetupMessage() {
    }

    public ContestManagementSetupMessage(Throwable exception) {
        super(exception);
    }

    public Collection getProblemStatusTypes() {
        return problemStatusTypes;
    }

    public void setProblemStatusTypes(Collection problemStatusTypes) {
        this.problemStatusTypes = problemStatusTypes;
    }

    public Collection getDifficultyLevels() {
        return difficultyLevels;
    }

    public void setDifficultyLevels(Collection difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    public Collection getDivisions() {
        return divisions;
    }

    public void setDivisions(Collection divisions) {
        this.divisions = divisions;
    }

    public Collection getSurveyStatusTypes() {
        return surveyStatusTypes;
    }

    public void setSurveyStatusTypes(Collection surveyStatusTypes) {
        this.surveyStatusTypes = surveyStatusTypes;
    }

    public Collection getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(Collection questionTypes) {
        this.questionTypes = questionTypes;
    }

    public Collection getQuestionStyles() {
        return questionStyles;
    }

    public void setQuestionStyles(Collection questionStyles) {
        this.questionStyles = questionStyles;
    }

    public Collection getRoundTypes() {
        return roundTypes;
    }
    
    public Collection getSeasons() {
        return seasons;
    }
    
    public Collection getRegions() {
        return regions;
    }

    public void setRoundTypes(Collection roundTypes) {
        this.roundTypes = roundTypes;
    }
    
    public void setSeasons(Collection seasons) {
        this.seasons = seasons;
    }
    
    public void setRegions(Collection regions) {
        this.regions = regions;
    }

    public Collection getLanguages() {
        return languages;
    }

    public void setLanguages(Collection languages) {
        this.languages = languages;
    }
}
