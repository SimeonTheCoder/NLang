package parser;

import data.ReadableFile;
import data.WritableFile;
import executor.ThreadManager;
import nodes.Node;
import operations.Operation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Interpreter {
    public static HashMap<String, WritableFile> writableFiles = new HashMap<>();
    public static HashMap<String, ReadableFile> readableFiles = new HashMap<>();

    public static ThreadManager threadManager;

    public static void interpretMain(Node node, float[] memory, int operationIndex, int threadIndex) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (operationIndex == 0) threadManager = new ThreadManager(8, memory);

        if (node.instruction != null) {
            threadManager.enqueue(node.instruction, threadIndex, operationIndex);
            if (!node.childNodes.isEmpty()) {
                for (Node childNode : node.childNodes) interpretMain(childNode, memory, operationIndex++, threadIndex);
            }
        } else {
            for (int l = 0; l < node.repetitions; l++) {
                for (Node parallelNode : node.parallelNodes) interpretMain(parallelNode, memory, operationIndex, threadIndex + 1);
                for (Node childNode : node.childNodes) interpretMain(childNode, memory, operationIndex, threadIndex + 1);
            }
        }

        if (operationIndex == 0) {
            threadManager.startAll();
        }
    }

    public static void interpret(Node node, float[] memory) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (node.instruction != null) {
            executeInstruction(node.instruction, memory);
            if (!node.childNodes.isEmpty()) {
                for (Node childNode : node.childNodes) interpret(childNode, memory);
            }
        } else {
            for (int l = 0; l < node.repetitions; l++) {
                for (Node parallelNode : node.parallelNodes) interpret(parallelNode, memory);
                for (Node childNode : node.childNodes) interpret(childNode, memory);
            }
        }
    }

    public static float getValue(Object parsed, float[] memory) {
        float val;

        if (parsed instanceof Float) {
            val = (Float) parsed;
        } else {
            int address = (Integer) parsed;

            if(address > 2048) {
                address -= 2048;
                float value = memory[address];
                address = (int) Math.floor(value) + 1536;
            }

            val = memory[address];
        }

        return val;
    }

    public static void executeInstruction(Object[] instruction, float[] memory) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        int repetitions = 1;

        if (instruction[7] != null) {
            Object value = instruction[7];
            repetitions = (value instanceof String) ? (int) getValue(value, memory) : (Integer) value;
        }

        for (int l = 0; l < repetitions; l++) {
            ((Operation) instruction[0]).execute(instruction, memory, writableFiles, readableFiles);
        }
    }
}
