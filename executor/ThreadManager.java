package executor;

import nodes.Node;

public class ThreadManager {
    public ExecutorThread[] threads;
    private final boolean[] used;

    public ThreadManager(int threadCount, float[] memory) {
        this.threads = new ExecutorThread[threadCount];
        this.used = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) threads[i] = new ExecutorThread(memory);
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
            used[threadIndex] = true;
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

    public void startAll() {
        for (int i = 0; i < threads.length; i ++) {
            if(!used[i]) return;
            threads[i].start();
        }
    }
}
