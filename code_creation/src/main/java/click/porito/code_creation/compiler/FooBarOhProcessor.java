package click.porito.code_creation.compiler;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@AutoService(Processor.class)
@SupportedAnnotationTypes("click.porito.code_creation.compiler.Panda")
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_11)
public class FooBarOhProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 만약 fooBar 라는 이름의 메서드만 있으면, 직접 class 파일을 컴파일해서 생성한다.
        // 그리고 그 클래스에 @Component 를 붙인다.

        for (Element element : roundEnv.getElementsAnnotatedWith(Panda.class)) {
            // @Panda 어노테이션이 클래스가 아닌 곳에 있는 경우 에러 메시지를 빌드 로그에 남긴다
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Panda can only be applied to class.");
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + element.getSimpleName());
                // 오직 하나의 메서드만 있는지 확인한다.
                List<ExecutableElement> methodList = element.getEnclosedElements().stream()
                        .filter(e -> e.getKind() == ElementKind.METHOD)
                        .map(e -> (ExecutableElement) e)
                        .collect(Collectors.toList());
                if (methodList.size() != 1) {
                    sendError("There should be only one method.", element);
                    return false;
                }
                // 메서드 이름이 fooBar 인지 확인한다.
                ExecutableElement method = methodList.get(0);
                if (!method.getSimpleName().toString().equals("fooBar")) {
                    sendError("Method name should be fooBar.", element);
                    return false;
                }

            }
        }



        return false;
    }

    protected void sendError(String msg, Element e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
