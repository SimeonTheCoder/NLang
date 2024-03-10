package executor;

import nodes.Node;

import java.util.HashMap;

public class ThreadManager {
    public ExecutorThread[] threads;
    HashMap<String, Float> memory;

    public ThreadManager(int threadCount, HashMap<String, Float> memory) {
        this.threads = new ExecutorThread[threadCount];
        this.memory = memory;

        for (int i = 0; i < threadCount; i++) threads[i] = new ExecutorThread(this.memory);
    }

    public void enqueueNode(Node mainNode, int threadIndex, int operationIndex) {
        if (mainNode.parallelNodes.size() == 0) {
            while (threads[threadIndex].queue.containsKey(operationIndex)) {
                if(++threadIndex >= threads.length) {
                    threadIndex = 0;
                    operationIndex++;
                }
            }

            threads[threadIndex].queue.put(operationIndex, mainNode.instruction);

//            if(mainNode.instruction[0] == BasicOperation.CALL) {
//                enqueueNode((Node) mainNode.instruction[1], threadIndex, operationIndex + 1);
//            }
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
