package executor;

import java.util.HashMap;

public class ThreadManager {
    ExecutorThread[] threads;
    HashMap<String, Float> memory;

    public ThreadManager(int threadCount, HashMap<String, Float> memory) {
        this.threads = new ExecutorThread[threadCount];
        this.memory = memory;

        for (int i = 0; i < threadCount; i++) threads[i] = new ExecutorThread(this.memory);
    }

    public void enqueue(Object[] operation, int threadIndex, int operationIndex) {
        while (threads[threadIndex].queue.containsKey(operationIndex)) {
            if(++threadIndex >= threads.length) {
                threadIndex = 0;
                operationIndex++;
            }
        }

        threads[threadIndex].queue.put(operationIndex, operation);
    }

    public void startAll() {
        for (ExecutorThread thread : threads) thread.start();
    }
}
