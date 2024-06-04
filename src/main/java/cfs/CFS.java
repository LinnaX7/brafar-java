package cfs;

import com.github.gumtreediff.tree.Tree;
import java.util.ArrayList;
import java.util.List;

public class CFS {

    private final List<String> structList;
    private final String struct;
    private final String simpleStruct;
    private final String methodName;

    public CFS(Tree method, String methodName) {
        this.methodName = methodName;
        structList = new ArrayList<>();
        Tree block = getBlockStmt(method);
        assert block != null;
        struct = getCFSFromBlock(block);
        this.simpleStruct = generateSimpleStrut();
    }

    public String getMethodName() {
        return methodName;
    }

    public String getStruct() {
        return struct;
    }

    public String getSimpleStruct() {
        return simpleStruct;
    }

    public void getStructList() {
        for (String ss : structList) {
            System.out.println(ss);
        }
    }

    private String generateSimpleStrut() {

        StringBuilder retStruct = new StringBuilder();
        for (String struct : structList) {
            switch (struct) {
                case "bb":
                    retStruct.append("b");
                    break;
                case "for":
                    retStruct.append("f");
                    break;
                case "while":
                    retStruct.append("w");
                    break;
                case "if":
                    retStruct.append("i");
                    break;
                case "elif":
                    retStruct.append("e");
                    break;
                case "else":
                    retStruct.append("l");
                    break;
            }
        }
        return retStruct.toString();
    }

    private Tree getBlockStmt(Tree method) {
        for (Tree node : method.getChildren()) {
            if (node.getType().toString().equals("BlockStmt")) {
                return node;
            }
        }
        return null;
    }

    private String getCFSFromIf(Tree ifStmt) {

        StringBuilder retCFS = new StringBuilder("If_start");
        if (structList.size() != 0 && structList.get(structList.size() - 1).equals("el")) {
            structList.set(structList.size() - 1, "elif");
        } else {
            structList.add("if");
        }
        boolean flag = false;
        for (Tree node : ifStmt.getChildren()) {
//            System.out.println(flag);

            if (!flag && node.getType().toString().endsWith("Stmt")) {
                String temp = getCFSFromStmt(node);
                if (!temp.equals(""))
                    retCFS.append(",").append(temp);
                flag = true;
                retCFS.append(",").append("If_end");
            } else if (flag) {
                structList.add("el");
                retCFS.append(",").append("Else_start").append(",");
                if (node.getType().toString().equals("IfStmt")) {
                    String temp = getCFSFromStmt(node);
                    if (!temp.equals("")) {
                        retCFS.append(getCFSFromStmt(node)).append(",");
                    }
                } else {
                    structList.set(structList.size() - 1, "else");
                    String temp = getCFSFromStmt(node);
                    if (!temp.equals("")) {
                        retCFS.append(getCFSFromStmt(node)).append(",");
                    }
                }
                retCFS.append("Else_end");
            }
        }
        return retCFS.toString();
    }

    private String getCFSFromWhile(Tree whileStmt) {
        StringBuilder retCFS = new StringBuilder("While_start");
        structList.add("while");
        for (Tree node : whileStmt.getChildren()) {
            String temp = getCFSFromStmt(node);
            if (!temp.equals(""))
                retCFS.append(",").append(temp);
        }
        return retCFS + "," + "While_end";
    }

    private String getCFSFromSwitch(Tree switchStmt) {
        StringBuilder retCFS = new StringBuilder("Switch_start");
        structList.add("switch");
        for (Tree node : switchStmt.getChildren()) {
            String temp = getCFSFromStmt(node);
            if (!temp.equals(""))
                retCFS.append(",").append(temp);
        }
        return retCFS + "," + "Switch_end";
    }

    private String getCFSFromFor(Tree forStmt) {
        StringBuilder retCFS = new StringBuilder("For_start");
        structList.add("for");
        for (Tree node : forStmt.getChildren()) {
            String temp = getCFSFromStmt(node);
            if (!temp.equals(""))
                retCFS.append(",").append(temp);
        }
        return retCFS + "," + "For_end";
    }

//    private String getCFSFromContinue(Tree continueStmt) {
//        return "Continue";
//    }
//
//    private String getCFSFromBreak(Tree breakStmt) {
//        return "Break";
//    }

//    private String getCFSFromReturn(Tree retStmt) {
//        //structList.add("ret");
//        return "Return";
//    }

    private String getCFSFromStmt(Tree stmt) {
        String retCFS = "";
        switch (stmt.getType().toString()) {
            case "BlockStmt":
                retCFS = getCFSFromBlock(stmt);
                break;
            case "IfStmt":
                retCFS = getCFSFromIf(stmt);

                break;
            case "ForStmt":
                retCFS = getCFSFromFor(stmt);
                break;
            case "ForEachStmt":
                retCFS = getCFSFromForEach(stmt);
                break;
            case "WhileStmt":
                retCFS = getCFSFromWhile(stmt);
                break;
            case "SwitchStmt":
                retCFS = getCFSFromSwitch(stmt);
                break;
            case "BreakStmt":
            case "ContinueStmt":
            case "ReturnStmt":
            case "ExpressionStmt":
                //                retCFS = getCFSFromReturn(stmt);
                //                retCFS = getCFSFromContinue(stmt);
                //                retCFS = getCFSFromBreak(stmt);
                if (structList.size() == 0 || !structList.get(structList.size() - 1).equals("bb")) {
                    this.structList.add("bb");
                }
                break;

        }
        return retCFS;
    }

    private String getCFSFromForEach(Tree stmt) {
        StringBuilder retCFS = new StringBuilder("ForEach_start");
        structList.add("forEach");
        for (Tree node : stmt.getChildren()) {
            String temp = getCFSFromStmt(node);
            if (!temp.equals(""))
                retCFS.append(",").append(temp);
        }
        return retCFS + "," + "ForEach_end";
    }

    private String getCFSFromBlock(Tree block) {

        StringBuilder retCFS = new StringBuilder();
        for (Tree node : block.getChildren()) {
            String temp = getCFSFromStmt(node);
            if (!temp.equals("")) {
                if (retCFS.toString().equals("")) {
                    retCFS.append(temp);
                } else {
                    retCFS.append(",").append(temp);
                }
            }
        }
        return retCFS.toString();
    }

    public boolean isEqual(CFS cfs){
        return struct.equals(cfs.getStruct());
    }

    public static void main(String[] args) {

    }
}
