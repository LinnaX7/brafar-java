package evaluate;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class ASTEditDistanceCalculator {
    private static final int delCost = 1; //the cost of delete node
    private static final int insCost = 1; //
    private static final int relCost = 1; //

    Map<Pair<Forest, Forest>, Integer> distances = new HashMap<>();
    Forest forest1;
    Forest forest2;

    int buggyASTSize=0;
    //
    /**
     * @param list1
     * @param list2
     */
    public ASTEditDistanceCalculator(ArrayList list1, ArrayList list2) {
        this.forest1 = new Forest(list1);
        this.forest2 = new Forest(list2);
    }

    // 用路径初始化
    public ASTEditDistanceCalculator(String code1, String code2,
                                     String methodname1,String methodname2) throws FileNotFoundException {
        ASTEditDistanceCalculator.TreeNode<Node> tree1 = new ASTEditDistanceCalculator.TreeNode<>();
        ASTEditDistanceCalculator.TreeNode<Node> tree2 = new ASTEditDistanceCalculator.TreeNode<>();
        ASTReactor astReactor = new ASTReactor();
        ASTGenerator.getCU(code1).ifPresent((l) -> {
            l.findAll(MethodDeclaration.class).forEach(m->{
                if(m.getName().getIdentifier().equals(methodname1)){
                    astReactor.visit(m,tree1);
                }
            });
            //astReactor.visit(l, tree1);
        });
        buggyASTSize=tree1.size();
        ASTGenerator.getCU(code2).ifPresent((l) -> {
            l.findAll(MethodDeclaration.class).forEach(m->{
                if(m.getName().getIdentifier().equals(methodname2)){
                    astReactor.visit(m,tree2);
                }
            });
            //astReactor.visit(l, tree2);
        });
        this.setForests(tree1.getChildren(), tree2.getChildren());
    }

    private void setForests(ArrayList list1, ArrayList list2) {
        this.forest1 = new Forest(list1);
        this.forest2 = new Forest(list2);
    }

    public int getDistance() {
        return this.getDistance(this.forest1, this.forest2);
    }

    // zhang-shasha
    private int getDistance0(Forest forest1, Forest forest2) {
        int distance = -1;
        // base case
        if (distances.get(new Pair<>(forest1, forest2)) != null) {
            // read from save
            distance = distances.get(new Pair<>(forest1, forest2));
        } else if (forest1.isEmpty() && forest2.isEmpty()) {
            distance = 0;
        } else if (!forest1.isEmpty() && forest2.isEmpty()) {
            distance = getDistance(forest1.deleteTheMostRightRoot(), forest2) + delCost;
        } else if (forest1.isEmpty()) {
            distance = getDistance(forest1, forest2.deleteTheMostRightRoot()) + insCost;
        } else if (Objects.equals(forest1, forest2)) {
            distance = 0;
        } else {
            // recursive case
            Forest R1 = forest1.getTheMostRightTree();
            Forest R2 = forest2.getTheMostRightTree();
            int delTED = getDistance(forest1.deleteTheMostRightRoot(), forest2) + delCost;
            int insTED = getDistance(forest1, forest2.deleteTheMostRightRoot()) + insCost;
            int relTED = getDistance(forest1.deleteTheMostRightTree(), forest2.deleteTheMostRightTree())
                    + (R1.getTheMostRightRoot().equal(R2.getTheMostRightRoot()) ? 0 : relCost)
                    + getDistance(R1.deleteTheMostRightRoot(), R2.deleteTheMostRightRoot());
            distance = Math.min(delTED, Math.min(insTED, relTED));
        }
        // save
        distances.put(new Pair<>(forest1, forest2), distance);
        return distance;
    }

    private static class RecursiveParameter {
        //
        RecursiveParameter parent;
        //
        ArrayList<Integer> childrenHash = new ArrayList<>();
        Pair<Forest, Forest> pair; //
        int caseNum; //
        boolean ifEnd; //
        int data; //
        double percent; //

        public RecursiveParameter(Forest f1, Forest f2, RecursiveParameter parent, int caseNum, boolean ifEnd, int data,
                double percent) {
            this.pair = new Pair<>(f1, f2);
            this.parent = parent;
            if (parent != null)
                parent.childrenHash.add(this.pair.hashCode());
            this.caseNum = caseNum;
            this.ifEnd = ifEnd;
            this.data = data;
            this.percent = percent;
        }

        public int hashCode() {
            return pair.hashCode();
        }
    }

    private void reverse(RecursiveParameter reverse, Map<Integer, Integer> forestDistanceMap) {
        boolean flag = true;
        this.addProcess(reverse.percent);
        while (reverse != null && flag) {
            flag = reverse.ifEnd;
            int res = 0;
            switch (reverse.caseNum) {
                case 0:
                    res = forestDistanceMap.get(reverse.hashCode());
                    break;
                case 1:
                    break;
                case 2:
                    res = reverse.data;
                    break;
                case 3:
                    int resDel = forestDistanceMap.get(reverse.childrenHash.get(0));
                    int resIns = forestDistanceMap.get(reverse.childrenHash.get(1));
                    int resRel1 = forestDistanceMap.get(reverse.childrenHash.get(2));
                    int resRel2 = forestDistanceMap.get(reverse.childrenHash.get(3));
                    res = Math.min(resDel + delCost, Math.min(resIns + insCost, resRel1 + resRel2 + reverse.data));
                    break;
                default:
                    ;
            }
            forestDistanceMap.put(reverse.hashCode(), res);
            reverse = reverse.parent;
        }
    }

    private double process = 0;

    public synchronized double getProcess() {
        return this.process;
    }

    public synchronized void initProcess() {
        this.process = 0;
    }

    private synchronized void addProcess(double num) {
        this.process += num;
    }
    private int getDistance(Forest forest1, Forest forest2) {
        //
        this.initProcess();

        Stack<RecursiveParameter> forestStack = new Stack<>();
        RecursiveParameter root = new RecursiveParameter(forest1, forest2, null, 0, true, 0, 1);
        forestStack.push(root);
        //
        Map<Integer, Integer> forestDistanceMap = new ConcurrentHashMap<>();

        while (!forestStack.empty()) {
            RecursiveParameter parameter = forestStack.pop();
            Pair<Forest, Forest> pair = parameter.pair;
            Forest f1 = pair.first;
            Forest f2 = pair.second;
            // base case
            if (forestDistanceMap.get(pair.hashCode()) != null) { // case 0, res = read()
                // read from storage
                parameter.caseNum = 0;
                reverse(parameter, forestDistanceMap);
            } else if (f1.isEmpty() && f2.isEmpty()) { // case 1, res = 0
                parameter.caseNum = 1;
                reverse(parameter, forestDistanceMap);
            } else if (!f1.isEmpty() && f2.isEmpty()) { // case 2, res = size * delCost
                parameter.caseNum = 2;
                parameter.data = f1.size * delCost;
                reverse(parameter, forestDistanceMap);
            } else if (f1.isEmpty()) { // case 2, res = size * insCost
                parameter.caseNum = 2;
                parameter.data = f2.size * insCost;
                reverse(parameter, forestDistanceMap);
            } else if (Objects.equals(f1, f2)) { // case 1, res = 0
                parameter.caseNum = 1;
                reverse(parameter, forestDistanceMap);
            } else { // case 3, res = Math.min(delTED, Math.min(insTED, relTED))
                // recursive case
                Forest R1 = f1.getTheMostRightTree();
                Forest R2 = f2.getTheMostRightTree();
                parameter.caseNum = 3;
                parameter.data = R1.getTheMostRightRoot().equal(R2.getTheMostRightRoot()) ? 0 : relCost;
                double percent = parameter.percent / 4;
                forestStack
                        .push(new RecursiveParameter(f1.deleteTheMostRightRoot(), f2, parameter, 0, true, 0, percent));
                forestStack
                        .push(new RecursiveParameter(f1, f2.deleteTheMostRightRoot(), parameter, 0, false, 0, percent));
                forestStack.push(new RecursiveParameter(f1.deleteTheMostRightTree(), f2.deleteTheMostRightTree(),
                        parameter, 0, false, 0, percent));
                forestStack.push(new RecursiveParameter(R1.deleteTheMostRightRoot(), R2.deleteTheMostRightRoot(),
                        parameter, 0, false, 0, percent));
            }
        }
        return forestDistanceMap.get(root.hashCode());
    }

    public static class TreeNode<T> {
        ArrayList<TreeNode> children = new ArrayList<>();
        // @Setter
        TreeNode parent;

        public void setParent(TreeNode p) {
            parent = p;
        }

        T data;
        boolean isLeaf;
        int size = 1;

        public TreeNode() {
            this.data = null;
            this.isLeaf = true;
        }

        TreeNode(T t) {
            this.data = t;
            this.isLeaf = true;
        }

        TreeNode(T t, boolean isLeaf) {
            this.data = t;
            this.isLeaf = isLeaf;
        }

        public int size() {
            return this.size;
        }

        public void add(TreeNode node) {
            this.children.add(node);
            node.setParent(this);
            this.addSize(node.size());
        }

        public void addSize(int s) {
            this.size += s;
            if (this.parent != null)
                this.parent.addSize(s);
        }

        //
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || this.getClass() != o.getClass())
                return false;
            TreeNode node = (TreeNode) o;
            if (!this.equal(o)) {
                return false;
            } else if (this.children.size() != node.children.size()) {
                return false;
            } else if (this.size != node.size) {
                return false;
            } else {
                for (int i = 0; i < this.children.size(); i++) {
                    if (!this.children.get(i).equals(node.children.get(i)))
                        return false;
                }
                return true;
            }
        }

        //
        public boolean equal(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            TreeNode node = (TreeNode) o;
            if (this.isLeaf && node.isLeaf) {
                if (this.data == node.data) {
                    return true;
                }
                if (this.data == null || this.data.getClass() != node.data.getClass()) {
                    return false;
                }
                return Objects.equals(this.data, node.data);
            } else {
                return this.data.getClass() == node.data.getClass();
            }
        }

        public ArrayList<TreeNode> getChildren() {
            return this.children;
        }
    }

    static class Forest {
        ArrayList<TreeNode> roots = new ArrayList<>();
        int size = 0;

        Forest() {
        }

        Forest(ArrayList<TreeNode> list) {
            this.roots.addAll(list);
            for (TreeNode treeNode : list) {
                this.size += treeNode.size();
            }
        }

        public int size() {
            return this.size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || this.getClass() != o.getClass())
                return false;
            Forest forest = (Forest) o;
            if (this.roots.size() != forest.roots.size()) {
                return false;
            } else if (this.size != forest.size) {
                return false;
            }
            for (int i = 0; i < this.roots.size(); i++) {
                if (!Objects.equals(this.roots.get(i), (forest.roots.get(i)))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.roots);
        }

        public boolean isEmpty() {
            return this.roots.isEmpty();
        }

        public Forest getTheMostRightTree() {
            Forest forest = new Forest();
            TreeNode treeNode = this.roots.get(this.roots.size() - 1);
            forest.size += treeNode.size;
            forest.roots.add(treeNode);
            return forest;
        }

        public TreeNode getTheMostRightRoot() {
            return this.roots.get(this.roots.size() - 1);
        }

        public Forest deleteTheMostRightTree() {
            Forest forest = new Forest(this.roots);
            if (!forest.isEmpty()) {
                forest.size -= forest.roots.get(forest.roots.size() - 1).size;
                forest.roots.remove(forest.roots.size() - 1);
            }
            return forest;
        }

        public Forest deleteTheMostRightRoot() {
            Forest forest = new Forest(this.roots);
            if (!forest.isEmpty()) {
                int lastIndex = forest.roots.size() - 1;
                TreeNode root = forest.roots.get(lastIndex);
                forest.size -= root.size;
                forest.roots.remove(lastIndex);
                ArrayList<TreeNode> children = root.getChildren();
                for (TreeNode t : children) {
                    forest.size += t.size;
                }
                forest.roots.addAll(children);
            }
            return forest;
        }
    }

}
