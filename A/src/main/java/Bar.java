public class Bar implements Foo {
    public Bar() {
        Foo.$init$(this);
    }

    public void print(String str) {
        Foo.print$(this, str);
    }
}
