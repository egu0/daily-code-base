package im.engure.util;


import java.util.List;

public class MyAssertions {

    public static void assertEqual(List<Object> o1, List<Object> o2) {
        if (o1 == o2) {
            return;
        }
        if (o1 == null || o2 == null) {
            throw new NullPointerException();
        }
        if (o1.size() != o2.size()) {
            throw new RuntimeException("unmatched length");
        }
        int n = o1.size();
        for (int i = 0; i < n; i++) {
            assertEqual(o1.get(i), o2.get(i));
        }
    }


    public static void assertFalse(Boolean bool) {
        if (bool == null) {
            throw new NullPointerException();
        }

        if (bool) {
            throw new RuntimeException("condition is true");
        }
    }

    public static void assertTrue(Boolean bool) {
        if (bool == null) {
            throw new NullPointerException();
        }

        if (!bool) {
            throw new RuntimeException("condition is false");
        }
    }

    public static void assertEqual(Object i1, Object i2) {

        try {
            if (i1 == i2) {
                return;
            }

            if (i1 == null || i2 == null) {
                throw new NullPointerException();
            }

            if (i1.getClass() != i2.getClass()) {
                throwTypeUnmatchedException(i1.getClass(), i2.getClass());
            }

            if (!i1.equals(i2)) {
                throwNotEqualException(i1, i2);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void throwTypeUnmatchedException(Class<?> c1, Class<?> c2) {
        throw new RuntimeException("found unmatched type: " + c1 + " , " + c2);
    }

    private static void throwNotEqualException(Object o1, Object o2) {
        throw new RuntimeException("not equal: " + o1 + " != " + o2);
    }

}
