package sar.repair.utils;

import com.github.javaparser.ast.Node;
import program.MethodBuilder;
import variables.VariableBuilder;
import variables.VariableMatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Common {
    public VariableMatch variableMatch;
    public VariableBuilder buggyVariableBuilder;
    public VariableBuilder correctVariableBuilder;
    public MethodBuilder buggyMethodBuilder;
    public MethodBuilder correctMethodBuilder;

    public Map<Location, Node> order;
    public Stack<Integer> blockIndex;
    public Common(MethodBuilder buggyM, MethodBuilder correctM,VariableMatch variableMatch) {
        this.variableMatch = variableMatch;
        buggyVariableBuilder = buggyM.getVariableBuilder();
        correctVariableBuilder = correctM.getVariableBuilder();
        buggyMethodBuilder = buggyM;
        correctMethodBuilder = correctM;

        order=new HashMap<>();
        blockIndex=new Stack<>();
    }
}
