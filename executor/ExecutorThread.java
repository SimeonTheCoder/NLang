package executor;

import parser.Interpreter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExecutorThread extends Thread {
    public LinkedHashMap<Integer, Object[]> queue;
    public float[] memory;

    public ExecutorThread(float[] memory) {
        this.memory = memory;
        this.queue = new LinkedHashMap<>();
    }

    @Override
    public void run() {
        for (Map.Entry<Integer, Object[]> entry : queue.entrySet()) {
            try {
                Interpreter.executeInstruction(entry.getValue(), memory);
            } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
