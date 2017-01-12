package com.egnyte.androidsdk.picker;

class NullSafeEquals {

    static boolean check(Object a, Object b) {
        if (a != null && b != null) {
            return a.equals(b);
        } else {
            return a == b;
        }
    }
}
