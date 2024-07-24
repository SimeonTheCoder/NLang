package executor;

import nodes.Node;

import java.util.HashMap;

public class ThreadManager {
    public ExecutorThread[] threads;
    private float[] memory;

    public ThreadManager(int threadCount, float[] memory) {
        this.threads = new ExecutorThread[threadCount];
        this.memory = memory;

        for (int i = 0; i < threadCount; i++) threads[i] = new ExecutorThread(this.memory);
    }

    public void enqueueNode(Node mainNode, int threadIndex, int operationIndex) {
        if (mainNode.parallelNodes.isEmpty()) {
            while (threads[threadIndex].queue.containsKey(operationIndex)) {
                if(++threadIndex >= threads.length) {
                    threadIndex = 0;
                    operationIndex++;
                }
            }

            threads[threadIndex].queue.put(operationIndex, mainNode.instruction);
        } else {
            int currThreadIndex = threadIndex;

            for (Node parallelNode : mainNode.parallelNodes) {
                enqueueNode(parallelNode, currThreadIndex++, operationIndex);
            }
        }

        for (Node childNode : mainNode.childNodes) {
            enqueueNode(childNode, threadIndex, operationIndex + 1);
        }
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
