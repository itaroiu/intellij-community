// Items: arg, cast, for, not, par, var
public class Foo {
    void m() {
        int foo = 2 * 2;
        foo<caret> + 2 * 2; /* boo */
    }
}