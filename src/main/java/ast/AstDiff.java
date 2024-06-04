package ast;

import cfs.CFS;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.gen.javaparser.JavaParserGenerator;
import com.github.gumtreediff.matchers.GumtreeProperties;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.javaparser.ParseProblemException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AstDiff extends Diff{
    private final double sim;

    public double getSim() {
        return sim;
    }

    public AstDiff(TreeContext src, TreeContext dst, MappingStore mappings, EditScript editScript, double sim) {
        super(src,dst,mappings,editScript);
        this.sim = sim;
    }

//    @Override

    public static AstDiff myCompute(TreeContext src,TreeContext dst){
        Matcher m = Matchers.getInstance().getMatcherWithFallback((String)null);
        return getAstDiff(src, dst, m);
    }

    public static AstDiff myCompute(String srcFile, String dstFile, String treeGenerator, String matcher) throws IOException {
        //TreeContext src = TreeGenerators.getInstance().getTree(srcFile, treeGenerator);
        //TreeContext dst = TreeGenerators.getInstance().getTree(dstFile, treeGenerator);
        try {
            TreeContext src = new JavaParserGenerator().generateFrom().file(srcFile);
            TreeContext dst = new JavaParserGenerator().generateFrom().file(dstFile);
            Matcher m = Matchers.getInstance().getMatcherWithFallback(matcher);
            return getAstDiff(src, dst, m);
        }catch (ParseProblemException e){
            return null;
        }
    }

    private static AstDiff getAstDiff(TreeContext src, TreeContext dst, Matcher m) {
        m.configure(new GumtreeProperties());
        MappingStore mappings = m.match(src.getRoot(), dst.getRoot());
        EditScript editScript;
        editScript = (new SimplifiedChawatheScriptGenerator()).computeActions(mappings);
        double editDist;
        editDist = 0.0;
        editDist = getEditDist(editScript, editDist);
        double sim = 1.0 - (double) editDist / (Math.max(src.getRoot().getLength(), dst.getRoot().getLength()));
        return new AstDiff(src, dst, mappings, editScript, sim);
    }

    private static double getEditDist(EditScript editScript, double editDist) {
        for (Action action : editScript.asList()) {
            if (action instanceof Delete || action instanceof Update || action instanceof Insert) {
                editDist += 1.0;
            } else if (action instanceof TreeDelete) {
                editDist += 1.0 * action.getNode().getLength();
            } else if (action instanceof TreeInsert) {
                editDist += 1.0 * action.getNode().getLength();
            }
        }
        return editDist;
    }

    public static List<CFS> getMethodCFS(TreeContext tc){
        List<CFS> retMethodCFSList = new ArrayList<>();
        for (Tree t: tc.getRoot().getChildren()) {
            Boolean flag = false;
            if (t.getType().toString().equals("ClassOrInterfaceDeclaration")) {
                for (Tree t1 : t.getChildren()) {
                    if (t1.getType().toString().equals("MethodDeclaration")) {
                        for (Tree t2 : t1.getChildren())
                            if (t2.getType().toString().equals("SimpleName")) {
                                retMethodCFSList.add(new CFS(t1,t2.getLabel().toString()));
                            }
                    }
                }
            }
        }
        return retMethodCFSList;
    }

    public static Tree getMethodTree(String methodName, TreeContext tc){
        for (Tree t: tc.getRoot().getChildren()) {
            Boolean flag = false;
            if(t.getType().toString().equals("ClassOrInterfaceDeclaration")) {
                for (Tree t1:t.getChildren()){
                    if(t1.getType().toString().equals("MethodDeclaration")) {
                        for (Tree t2 : t1.getChildren())
                            if (t2.getType().toString().equals("SimpleName") && t2.getLabel().equals(methodName)) {
                                return t1.deepCopy();
                            }
                    }
                }
            }
        }
        return null;
    }

    public double astSimilarity(String methodName){
        Tree srcMethodTree = getMethodTree(methodName,src);
//        srcMethodTree.setParent(null);
//        System.out.println(srcMethodTree.toTreeString());
        Tree dstMethodTree = getMethodTree(methodName,dst);
//        dstMethodTree.setParent(null);
//        System.out.println(dstMethodTree.toTreeString());

        Matcher m = Matchers.getInstance().getMatcherWithFallback((String)null);
        m.configure(new GumtreeProperties());
        MappingStore mappings = m.match(srcMethodTree, dstMethodTree);

        EditScript editScript = (new SimplifiedChawatheScriptGenerator()).computeActions(mappings);

        double editDist = 0.0;
        editDist = getEditDist(editScript, editDist);
//        System.out.println(editDist);
        return editDist;
    }

}
