package click.porito;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class Main {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                // 특정 경로에 class 생성
                .annotateMethod()
                .make()
                .load(Main.class.getClassLoader())
                .getLoaded();
        System.out.println(dynamicType.newInstance().toString());
        System.out.println(dynamicType.getClass());


    }
}