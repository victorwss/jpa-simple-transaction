package ninja.javahacker.test.jpasimpletransactions;

import ninja.javahacker.jpasimpletransactions.By;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ByTest {
    @Test
    public void testSimple1() {
        By b = By.asc("foo");
        Assertions.assertAll(
                () -> Assertions.assertFalse(b.isDescending()),
                () -> Assertions.assertEquals("foo", b.getField())
        );
    }

    @Test
    public void testSimple2() {
        By b = By.desc("xoo");
        Assertions.assertAll(
                () -> Assertions.assertTrue(b.isDescending()),
                () -> Assertions.assertEquals("xoo", b.getField())
        );
    }

    @Test
    public void testEqualsHashCode() {
        By a = By.desc("xoo");
        By b = By.desc("xoo");
        By c = By.asc("xoo");
        By d = By.asc("xoo");
        By e = By.desc("goo");
        By f = By.desc("goo");
        By g = By.asc("goo");
        By h = By.asc("goo");
        Assertions.assertAll(
                () -> Assertions.assertEquals(a, b),
                () -> Assertions.assertEquals(c, d),
                () -> Assertions.assertEquals(e, f),
                () -> Assertions.assertEquals(g, h),
                () -> Assertions.assertNotEquals(a, c),
                () -> Assertions.assertNotEquals(a, null),
                () -> Assertions.assertNotEquals(null, a),
                () -> Assertions.assertEquals(a.hashCode(), b.hashCode()),
                () -> Assertions.assertEquals(c.hashCode(), d.hashCode()),
                () -> Assertions.assertEquals(e.hashCode(), f.hashCode()),
                () -> Assertions.assertEquals(g.hashCode(), h.hashCode()),
                () -> Assertions.assertNotEquals(a.hashCode(), c.hashCode()),
                () -> Assertions.assertNotEquals(a.hashCode(), e.hashCode()),
                () -> Assertions.assertNotEquals(a.hashCode(), g.hashCode()),
                () -> Assertions.assertNotEquals(c.hashCode(), e.hashCode()),
                () -> Assertions.assertNotEquals(c.hashCode(), g.hashCode()),
                () -> Assertions.assertNotEquals(e.hashCode(), g.hashCode())
        );
    }

    @Test
    public void testToString() {
        By a = By.desc("xoo");
        By b = By.asc("xoo");
        By c = By.desc("goo");
        By d = By.asc("goo");
        Assertions.assertAll(
                () -> Assertions.assertEquals("By(field=xoo, descending=true)", a.toString()),
                () -> Assertions.assertEquals("By(field=xoo, descending=false)", b.toString()),
                () -> Assertions.assertEquals("By(field=goo, descending=true)", c.toString()),
                () -> Assertions.assertEquals("By(field=goo, descending=false)", d.toString())
        );
    }
}
