package sar;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import monitor.JDIDebuggerExecutor;
import program.ProgramBuilder;
import program.TesterBuilder;
import variables.Variable;
import variables.VariableBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sar.Executor.*;
import static sar.Fixer.EXECUTE_LOG_PATH;

public class Cluster {

    public static List<List<ProgramBuilder>> classifyByCFS(List<ProgramBuilder>programBuilders){
        Map<String,List<ProgramBuilder>> cfsMap=new HashMap<>();
        for(ProgramBuilder pb:programBuilders){
            String str_cfs=pb.getMethodToFixCFS().getStrCFS();
            if(cfsMap.containsKey(str_cfs)){
                cfsMap.get(str_cfs).add(pb);
            }else{
                List<ProgramBuilder> list=new ArrayList<>();
                list.add(pb);
                cfsMap.put(str_cfs,list);
            }
        }
        List<List<ProgramBuilder>>list=new ArrayList<>();
        for(Map.Entry<String,List<ProgramBuilder>>entry: cfsMap.entrySet()){
           list.add(entry.getValue());
        }
        return list;
    }
    public static boolean isDEA(ProgramBuilder pb1,ProgramBuilder pb2,TesterBuilder testerBuilder,String methodToFix) throws IOException {
        for(String test: testerBuilder.getTesterNames()){
            JDIDebuggerExecutor.compileTest(pb1, testerBuilder);
            JDIDebuggerExecutor.executeTest(pb1, testerBuilder, methodToFix, test);
            pb1.initMethodBuilder(methodToFix);
            pb1.getMethodByName(methodToFix).setVariableValues(EXECUTE_LOG_PATH, pb1.getClassName());

            JDIDebuggerExecutor.compileTest(pb2, testerBuilder);
            JDIDebuggerExecutor.executeTest(pb2, testerBuilder, methodToFix, test);
            pb2.initMethodBuilder(methodToFix);
            pb2.getMethodByName(methodToFix).setVariableValues(EXECUTE_LOG_PATH, pb2.getClassName());

            VariableBuilder vb1=pb1.getMethodByName(methodToFix).getVariableBuilder();
            VariableBuilder vb2=pb2.getMethodByName(methodToFix).getVariableBuilder();

             List<Variable> variables1=new ArrayList<>();
             for(Variable v:vb1.getVariableList()){
                 if(!v.specialType.equals(Variable.SpecialType.RETURN)){
                     variables1.add(v);
                 }
             }
             List<Variable> variables2=new ArrayList<>();
             for(Variable v:vb2.getVariableList()){
                 if(!v.specialType.equals(Variable.SpecialType.RETURN)){
                     variables2.add(v);
                 }
             }

             if(variables1.size()!= variables2.size()){
                 return false;
             }

             List<Variable> remove1 = new ArrayList<>();
             for(Variable v1:variables1){
                 List<Variable> remove2 = new ArrayList<>();
                 for(Variable v2 : variables2){
                     if(v1.isDEA(v2)){
                         remove1.add(v1);
                         remove2.add(v2);
                         break;
                     }
                 }
                 variables2.removeAll(remove2);
             }
             variables1.removeAll(remove1);
             if(!(variables1.isEmpty()&& variables2.isEmpty())){
                 return false;
             }
        }
        return true;
    }
    public static List<List<ProgramBuilder>> classifyByDEA(List<ProgramBuilder>programBuilders,String testSourceDir,String testClass,String methodToFix) throws IOException {
        List<List<ProgramBuilder>>list=new ArrayList<>();
        TesterBuilder testerBuilder=new TesterBuilder(testSourceDir,testClass);

        while(programBuilders.size() != 0){
            if(programBuilders.size()==1){
                list.add(programBuilders);
                break;
            }
            List<ProgramBuilder> remove=new ArrayList<>();
            ProgramBuilder pb=programBuilders.get(0);
            remove.add(pb);
            for(int i=1;i<programBuilders.size(); i++){
                if(isDEA(pb,programBuilders.get(i),testerBuilder,methodToFix)){
                    remove.add(programBuilders.get(i));
                }
            }
            list.add(remove);
            programBuilders.removeAll(remove);
        }
        return list;
    }

    public static void write2Excel(List<List<ProgramBuilder>>programBuilders) throws IOException, WriteException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format("Cluster"+"Assignment%02dProblem%02d.xls", ASSIGNMENT_NO, PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        int index=0;
        for(List<ProgramBuilder>pbs:programBuilders){
            WritableSheet sheet = workbook.createSheet(Integer.toString(index), index);
            String[] titles;
            titles = new String[]{"Program","Size"};
            Label label;
            label = new Label(0, 0, titles[0]);
            sheet.addCell(label);

            label=new Label(1,0,titles[1]);
            sheet.addCell(label);

            label=new Label(1,1,Integer.toString(pbs.size()));
            sheet.addCell(label);
            for (int i = 0; i < pbs.size(); i++) {
                label = new Label(0, i+1,pbs.get(i).getCompilationUnit().toString());
                sheet.addCell(label);
            }
            index++;
        }
        workbook.write();
        workbook.close();
    }
    public static void cluster() throws IOException, WriteException {
        String methodToFix = TARGET_METHOD_LIST[ASSIGNMENT_NO -1][PROBLEM_NO -1];
        String folder=CORRECT_FOLDER.toString();
        String testSourceDir =TESTER_FOLDER.toString();
        String testClass = PACKAGE_N + "." + TESTER_CLASS_LIST[ASSIGNMENT_NO -1][PROBLEM_NO -1];
        List<ProgramBuilder>programBuilders= Executor.getProgramBuilders(folder,ASSIGNMENT_NO,PROBLEM_NO);
        if(programBuilders==null||programBuilders.size() == 0){
            return;
        }
        List<List<ProgramBuilder>>cfs=classifyByCFS(programBuilders);

        List<List<ProgramBuilder>>dea=new ArrayList<>();
        for(List<ProgramBuilder>cfsList:cfs){
            List<List<ProgramBuilder>>deaList=classifyByDEA(cfsList,testSourceDir,testClass,methodToFix);
            dea.addAll(deaList);
        }

        write2Excel(dea);
    }
}
