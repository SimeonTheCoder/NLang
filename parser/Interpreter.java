package parser;

import data.ReadableFile;
import data.WritableFile;
import nodes.Node;
import operations.Operation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Interpreter {
    public static HashMap<String, WritableFile> writableFiles = new HashMap<>();
    public static HashMap<String, ReadableFile> readableFiles = new HashMap<>();

    public static void interpret(Node node, HashMap<String, Float> memory) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (node.instruction != null) {
            executeInstruction(node.instruction, memory);
            if (node.childNodes.size() > 0) {
                for (Node childNode : node.childNodes) interpret(childNode, memory);
            }
        } else {
            for (int l = 0; l < node.repetitions; l++) {
                for (Node parallelNode : node.parallelNodes) interpret(parallelNode, memory);
                for (Node childNode : node.childNodes) interpret(childNode, memory);
            }
        }
    }

    public static float getValue(Object parsed, HashMap<String, Float> memory) {
        float val;

        try {
            val = (Float) parsed;
        } catch (Exception exception) {
            try {
                if (String.valueOf(parsed).startsWith("gg")) {
                    int index = Math.round(
                            memory.get(
                                    String.format("g%d", (String.valueOf(parsed).charAt(2) - '0'))
                            )
                    );

                    val = memory.get(String.format("g%d", index));
                } else {
                    val = memory.get((String) parsed);
                }
            } catch (Exception e) {
                throw new RuntimeException("Invalid address " + parsed);
            }
        }

        return val;
    }

    public static void executeInstruction(Object[] instruction, HashMap<String, Float> memory) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        int repetitions;

        try {
            repetitions = (Integer) instruction[7];
        } catch (Exception exception) {
            repetitions = Math.round(memory.get((String) instruction[7]));
        }

        for (int l = 0; l < repetitions; l++) {
            ((Operation) instruction[0]).execute(instruction, memory, writableFiles, readableFiles);
        }
    }
}
