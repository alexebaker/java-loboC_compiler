package Compiler;

public class Value<T> {
    private T value;

    public Value() {
        this(null);
    }

    public Value(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
