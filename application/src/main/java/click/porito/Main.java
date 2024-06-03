package click.porito;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

public class Main {
    public static void main(String[] args) {
        Person john = new Person("John", 30);
        String name = john.getName();
        int age = john.getAge();
        System.out.println("Name: " + name + ", Age: " + age);

//        Class<?> dynamicType = new ByteBuddy()
//                .subclass(TestInterface.class)
//                //bean 어노테이션 붙이기
//                .annotateType(AnnotationDescription.Builder.ofType(Component.class).build())
//                .make()
//                .load(Main.class.getClassLoader())
//                .getLoaded();
//
//        // spring context
//
//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("click.porito");
//        boolean empty = applicationContext.getBeansOfType(TestBean.class).isEmpty();
//        System.out.println("TestBean is empty: " + empty);
    }
}