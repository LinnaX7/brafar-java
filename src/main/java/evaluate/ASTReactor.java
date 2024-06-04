package evaluate;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ASTReactor extends VoidVisitorAdapter<ASTEditDistanceCalculator.TreeNode> {

    @Override
    public void visit(AnnotationDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(AnnotationMemberDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ArrayAccessExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ArrayCreationExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ArrayCreationLevel n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ArrayInitializerExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ArrayType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<ArrayType.Origin> newNode = new ASTEditDistanceCalculator.TreeNode<>(n.getOrigin(),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(AssertStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(AssignExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<AssignExpr.Operator> newNode = new ASTEditDistanceCalculator.TreeNode<>(n.getOperator(),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(BinaryExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<BinaryExpr.Operator> newNode = new ASTEditDistanceCalculator.TreeNode<>(n.getOperator(), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(BlockComment n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(BlockStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(BooleanLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isValue:%s", n.isValue()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(BreakStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(CastExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(CatchClause n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(CharLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-value:%s", n.getClass(), n.getValue()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ClassExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isInterface:%s", n.isInterface()), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ClassOrInterfaceType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(CompilationUnit n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ConditionalExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ConstructorDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ContinueStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(DoStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(DoubleLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-value:%s", n.getClass(), n.getValue()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    //
    @Override
    public void visit(EmptyStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(EnclosedExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(EnumConstantDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(EnumDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isThis:%s", n.isThis()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ExpressionStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(FieldAccessExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(FieldDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ForStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ForEachStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(IfStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ImportDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(
                String.format("isStatic:%s;isAsterisk:%s", n.isStatic(), n.isAsterisk()), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(InitializerDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isStatic:%s", n.isStatic()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(InstanceOfExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(IntegerLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-value:%s", n.getClass(), n.getValue()), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(IntersectionType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(JavadocComment n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(LabeledStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(LambdaExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isEnclosingParameters:%s", n.isEnclosingParameters()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(LineComment n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(LocalClassDeclarationStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    // @Override
    // public void visit(LocalRecordDeclarationStmt n, ASTEditDistanceCalculator.TreeNode arg) {
    //     ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
    //     arg.add(newNode);
    //     super.visit(n, newNode);
    // }

    //getValue 待定
    @Override
    public void visit(LongLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-value:%s", n.getClass(), n.getValue()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(MarkerAnnotationExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(MemberValuePair n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(MethodCallExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(MethodDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(MethodReferenceExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-identifier:%s", n.getClass(), n.getIdentifier()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(NameExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(Name n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-identifier:%s", n.getClass(), n.getIdentifier()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(NormalAnnotationExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(NullLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ObjectCreationExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(PackageDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(Parameter n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isVarArgs:%s", n.isVarArgs()) , true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(PrimitiveType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<PrimitiveType.Primitive> newNode = new ASTEditDistanceCalculator.TreeNode<>(n.getType(), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    // @Override
    // public void visit(RecordDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
    //     ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
    //     arg.add(newNode);
    //     super.visit(n, newNode);
    // }

    // @Override
    // public void visit(CompactConstructorDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
    //     ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
    //     arg.add(newNode);
    //     super.visit(n, newNode);
    // }

    @Override
    public void visit(ReturnStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SimpleName n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-identifier:%s", n.getClass(), n.getIdentifier()), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(StringLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-value:%s", n.getClass(), n.getValue()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SuperExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SwitchEntry n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SwitchStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SynchronizedStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ThisExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ThrowStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(TryStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(TypeExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(TypeParameter n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(UnaryExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<UnaryExpr.Operator> newNode = new ASTEditDistanceCalculator.TreeNode<>(n.getOperator(), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(UnionType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(UnknownType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(VariableDeclarationExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(VariableDeclarator n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(VoidType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n, false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(WhileStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(WildcardType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ModuleDeclaration n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("isOpen:%s", n.isOpen()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ModuleRequiresDirective n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ModuleExportsDirective n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ModuleProvidesDirective n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ModuleUsesDirective n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ModuleOpensDirective n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(UnparsableStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(ReceiverParameter n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(VarType n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(Modifier n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Modifier.Keyword> newNode = new ASTEditDistanceCalculator.TreeNode<>(n.getKeyword(), true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(SwitchExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    //有String value
    @Override
    public void visit(TextBlockLiteralExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<String> newNode = new ASTEditDistanceCalculator.TreeNode<>(String.format("%s-value:%s", n.getClass(), n.getValue()),true);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(YieldStmt n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

    @Override
    public void visit(PatternExpr n, ASTEditDistanceCalculator.TreeNode arg) {
        ASTEditDistanceCalculator.TreeNode<Node> newNode = new ASTEditDistanceCalculator.TreeNode<>(n,false);
        arg.add(newNode);
        super.visit(n, newNode);
    }

}
