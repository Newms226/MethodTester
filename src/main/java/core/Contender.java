package core;


import java.util.function.Consumer;

// TODO Abstract out Consumer and create ConsumerContender and FunctionContender
public class Contender<T> {

    final String name;
    
    final Consumer<T> method;

    public Contender(String name, Consumer<T> method) {
        this.name = name;
        this.method = method;
    }

    void compete(T t) { this.method.accept(t); }
}