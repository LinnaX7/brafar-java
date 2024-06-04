package sar.config;

import program.ProgramBuilder;
import program.TesterBuilder;

import java.util.ArrayList;

public class Config {
    private ProgramBuilder buggyProgram;
    private ArrayList<ProgramBuilder> correctPrograms;
    private ProgramBuilder referenceProgram;
    private String methodToFix;
    private TesterBuilder testerProgram;

    public void setBuggyProgram(ProgramBuilder buggyProgram) {
        this.buggyProgram = buggyProgram;
    }

    public ProgramBuilder getBuggyProgram() {
        return buggyProgram;
    }

    public void setMethodToFix(String methodToFix) {
        this.methodToFix = methodToFix;
    }

    public String getMethodToFix() {
        return methodToFix;
    }

    public void setCorrectPrograms(ArrayList<ProgramBuilder> correctPrograms) {
        this.correctPrograms = correctPrograms;
    }

    public void setReferenceProgram(ProgramBuilder referenceProgram) {
        this.referenceProgram = referenceProgram;
    }

    public ProgramBuilder getReferenceProgram() {
        return referenceProgram;
    }

    public ArrayList<ProgramBuilder> getCorrectPrograms() {
        return correctPrograms;
    }

    public void setTesterProgram(TesterBuilder testerProgram) {
        this.testerProgram = testerProgram;
    }

    public TesterBuilder getTesterProgram() {
        return testerProgram;
    }

    public String getFaultyMethodSignature(){
        return getMethodToFix().substring(0, getMethodToFix().indexOf('@'));
    }

    public String getClassName(){
        return getMethodToFix().substring(getMethodToFix().indexOf('@')+1);
    }
}
