package executor;

import parser.Interpreter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExecutorThread extends Thread {
    public LinkedHashMap<Integer, Object[]> queue;
    public HashMap<String, Float> memory;

    public ExecutorThread(HashMap<String, Float> memory) {
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

//        for(int i = 0; i < queue.size(); i++) {
//            try {
//                if(queue.containsKey(i)) {
//                    Interpreter.executeInstruction(queue.get(i), memory);
//                }
//            } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
//                     InstantiationException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }
}
