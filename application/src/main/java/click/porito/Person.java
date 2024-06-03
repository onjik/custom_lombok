package click.porito;

import click.porito.annotation.Getter;

@Getter
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
