package com.github.ksouthwood.possystem;

public class InventoryAndPOSSystem {
    public static void main(String[] args) {
        if (args.length != 0) {
            for (int index = 0; index < args.length; index++) {
                if (args[index].equals("-fileName")) {
                    new RootWindow(args[index + 1]);
                    break;
                }
            }
        } else {
            new RootWindow();
        }
    }
}
