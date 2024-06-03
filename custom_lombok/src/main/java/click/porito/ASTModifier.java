package click.porito;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Lombok 의 코드를 보려면 다음 링크를 참조하십시오. <a href="https://github.com/projectlombok/lombok/blob/master/src/core/lombok/javac/apt/LombokProcessor.java">LombokProcessor</a>) <br>
 * 16 이후에는 JavacProcessingEnvironment 가 작동하지 않습니다.
 * @see <a href="https://github.com/projectlombok/lombok/issues/2681">Issue </a>
 * @see <a href="https://openjdk.org/jeps/396">Damn JEP</a>
 * @see <a href="https://github.com/projectlombok/lombok/commit/9806e5cca4b449159ad0509dafde81951b8a8523">How Lombok Fix it</a>
 *
 */
public class ASTModifier {
    private Trees trees;
    private Context context;
    private TreeMaker treeMaker;
    private Names names;
    private TreePathScanner<Object, CompilationUnitTree> scanner;

    public ASTModifier(ProcessingEnvironment processingEnvironment) {
        final JavacProcessingEnvironment javacProcessingEnvironment = (JavacProcessingEnvironment) processingEnvironment;
        this.trees = Trees.instance(processingEnvironment);
        this.context = javacProcessingEnvironment.getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    /**
     * {@link JCTree} is the AST(abstract syntax tree) representation of the Java source code.
     * {@link JCTree} 는 Java 소스 코드의 AST(추상 구문 트리) 표현입니다.
     */
    public void setClassDefModifyStrategy(Consumer<JCTree.JCClassDecl> strategy) {
        this.scanner = new TreePathScanner<>() {
            @Override
            public Trees visitClass(ClassTree classTree, CompilationUnitTree compilationUnitTree) {
                /*
                하나의 소스 파일에 있는 모든 것은 JCTree.JCCompilationUnit 구조에 유지됩니다.
                 */
                JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) compilationUnitTree;
                if (compilationUnit.sourcefile.getKind() == JavaFileObject.Kind.SOURCE) {
                    /*
                    TreeTranslator 는 JCTree 를 변환하는 데 사용됩니다.
                     */
                    compilationUnit.accept(new TreeTranslator() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                            super.visitClassDef(jcClassDecl);
                            /*
                            사용자가 설정한 전략을 수행합니다.
                             */
                            strategy.accept(jcClassDecl);
                        }
                    });
                }
                return trees;
            }
        };
    }

    public void modifyTree(Element element) {
        if (Objects.nonNull(scanner)) {
            final TreePath path = trees.getPath(element);
            scanner.scan(path, path.getCompilationUnit());
        }
    }

    public TreeMaker getTreeMaker() {
        return treeMaker;
    }

    public Names getNames() {
        return names;
    }


}
