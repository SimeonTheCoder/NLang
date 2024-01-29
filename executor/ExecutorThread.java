package executor;

import parser.Interpreter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ExecutorThread extends Thread {
    public HashMap<Integer, Object[]> queue;
    public HashMap<String, Float> memory;

    public ExecutorThread(HashMap<String, Float> memory) {
        this.memory = memory;
    }

    @Override
    public void run() {
        for(int i = 0; i < queue.size(); i++) {
            try {
                Interpreter.executeInstruction(queue.get(i), memory);
            } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
