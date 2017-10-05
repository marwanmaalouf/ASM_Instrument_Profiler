package com.asmproj;

public class Testing {

    public static void main(String args[]) {
        int x = 8;
        int y = 7;
        int z, v, w;
        foo();

        int t[] = new int[3];
        t[2] = 2;

        if (x > 2) { // 9

            if (y > x) { // 10

                z = 10;
            } else {
                z = -10; // 13
            }
            w = 8; // 15
        }

        v = 6;


    }


    public static void foo() {

        int x = 5;
        bar();
    }

    public static void bar() {

        int y = 9;
        if (y > 1) {
            y = 7;
        }

    }

}
