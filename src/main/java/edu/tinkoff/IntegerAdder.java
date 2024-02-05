package edu.tinkoff;

public class IntegerAdder {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("You must pass exactly two integer arguments.");
            return;
        }

        int a;
        int b;
        try {
            a = Integer.parseInt(args[0]);
            b = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Both arguments must be integers.");
            return;
        }

        int sum = a + b;
        System.out.println(a + " + " + b + " = " + sum);
    }
}
