package click.porito;

import click.porito.annotation.Getter;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.function.Consumer;

@AutoService(Processor.class)
@SupportedAnnotationTypes("click.porito.annotation.Getter")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class GetterProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // super.processingEnv 를 통해 ProcessingEnvironment 를 사용할 수 있습니다.
        ASTModifier astModifier = new ASTModifier(processingEnv);

        // 전략 주입
        astModifier.setClassDefModifyStrategy(appendGetterForEveryFieldsStrategy(astModifier));

        // @Getter 어노테이션이 붙은 모든 타입들을 순회한다.
        for (Element element : roundEnv.getElementsAnnotatedWith(Getter.class)) {

            // @Getter 어노테이션이 클래스가 아닌 곳에 있는 경우 에러 메시지를 빌드 로그에 남긴다
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Getter can only be applied to class.");
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + element.getSimpleName());
                astModifier.modifyTree(element);
            }
        }
        //return true if the annotation has been processed
        //만약 어노테이션이 처리되었다면 true 를 반환한다.
        return true;
    }

    private Consumer<JCTree.JCClassDecl> appendGetterForEveryFieldsStrategy(ASTModifier astModifier) {
        TreeMaker treeMaker = astModifier.getTreeMaker();
        Names names = astModifier.getNames();

        return jcClassDecl -> {
            List<JCTree> members = jcClassDecl.getMembers();
            for (JCTree member : members) {
                /*
                모든 멤버 변수에 대해 Getter 메소드를 생성합니다.
                 */
                if (member instanceof JCTree.JCVariableDecl) {
                    /*
                    AST Tree 를 조작하는 핵심 부분
                     */

                    // Getter 메소드를 생성합니다.
                    // 메서드에 대한 정의
                    JCTree.JCMethodDecl getter = createGetter(treeMaker,names,(JCTree.JCVariableDecl) member);
                    // 생성된 Getter 메소드를 클래스에 추가합니다.
                    jcClassDecl.defs = jcClassDecl.defs.append(getter);
                }
            }
        };
    }

    private JCTree.JCMethodDecl createGetter(TreeMaker treeMaker, Names names, JCTree.JCVariableDecl member) {
        String memberName = member.getName().toString();
        // Getter 메소드의 이름을 생성합니다.
        String methodName = "get" + memberName.substring(0, 1).toUpperCase() + memberName.substring(1);

        // 메서드가 잘 생성되고 있는지 빌드로그로 확인
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Creating method: " + methodName);

        // 메서드에 대한 정의를 반환
        return treeMaker.MethodDef(
                treeMaker.Modifiers(1), // PUBLIC
                names.fromString(methodName), // 메서드 이름
                member.vartype, // 반환 타입
                List.nil(), // 타입 파라미터
                List.nil(), // 파라미터
                List.nil(), // 예외
                // 메서드의 본문
                treeMaker.Block(1, List.of( // 블록
                        treeMaker.Return( // return this.memberName;
                                treeMaker.Ident(member.getName())
                        )
                )),
                null // default value
        );
    }
}
