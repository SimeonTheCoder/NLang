import data.ObjType;
import executor.ThreadManager;
import nodes.Node;
import operations.BasicOperation;
import operations.Operation;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;
import utils.EnumUtils;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length != 0 && !args[0].startsWith("--")) {
                EnumUtils.initClass();

                float[] memory = new float[2048];

                Parser parser = new Parser();
                Node mainNode = parser.parse(new File(args[0] + ".nlp"), memory);

                Linker linker = new Linker();

                linker.linkArgs(
                        Arrays.stream(args).skip(1).map(
                                Float::parseFloat
                        ).toList().toArray(new Float[]{}),
                        memory
                );

                ThreadManager threadManager = new ThreadManager(8, memory);
                threadManager.enqueueNode(mainNode, 0, 0);

                long start = System.currentTimeMillis();

                threadManager.startAll();

                for(int i = 0; i < threadManager.threads.length; i ++) {
                    threadManager.threads[i].join();
                }

//                boolean working = true;
//
//                while (working) {
//                    working = false;
//
//                    for(int i = 0; i < threadManager.threads.length; i++) {
//                        if (threadManager.threads[i].isAlive()) {
//                            working = true;
//                            break;
//                        }
//                    }
//                }

                long end = System.currentTimeMillis();

                System.out.println(end - start);
            } else {
                if(args.length != 0 && args[0].startsWith("--")) {
                    if (args[0].equals("--get")) {
                        String repo = String.format(
                                "https://raw.githubusercontent.com/%s/%s/main/CustomOperation.java",
                                args[1],
                                args[2]
                        );

                        System.out.println(repo);

                        Runtime.getRuntime().exec("curl -o CustomOperation.java " + repo);
                    }
                } else {
                    EnumUtils.initClass();

                    float[] memory = new float[2048];

                    Parser parser = new Parser();
                    parser.aliases = new HashMap<>();

                    List<Node> nodes = new ArrayList<>();
                    int nodeId = 0;

                    Scanner scanner = new Scanner(System.in);

                    System.out.print(">> ");
                    String line = scanner.nextLine();

                    while (!line.equals("exit")) {
                        if (line.startsWith("args")){
                            String[] tokens = line.split(" ");

                            Operation operation = EnumUtils.getOperation(tokens[1].toUpperCase());

                            Arrays.stream(operation.getArguments()).forEach(a -> {
                                System.out.print(a.toString() + " ");
                            });

                            System.out.println();
                        } else if (line.startsWith("help")) {
                            String[] tokens = line.split(" ");

                            System.out.print("( ");

                            Operation operation = EnumUtils.getOperation(tokens[1].toUpperCase());
                            Arrays.stream(operation.getArguments()).forEach(a -> {
                                System.out.print(a.toString() + " ");
                            });

                            System.out.println(")\n" + operation.help());
                        } else if (line.chars().allMatch(Character::isDigit)) {
                            Node node = new Node();
                            node.id = nodeId++;

                            if (!nodes.isEmpty()) {
                                node.parentNode = nodes.getLast();
                            }

                            nodes.add(node);

                            memory[(nodeId - 1) * 10] = Float.parseFloat(line);
                        } else if (!line.equals("memdump")) {
                            Node node = new Node();
                            node.id = nodeId++;

                            if (!nodes.isEmpty()) {
                                node.parentNode = nodes.getLast();
                            }

                            nodes.add(node);

                            Interpreter.executeInstruction(
                                    parser.parseInstruction(line, node, memory),
                                    memory
                            );
                        } else {
                            for (int i = 0; i < 2048; i ++) {
                                System.out.println(i + " -> " + memory[i]);
                            }
                        }

                        System.out.print(">> ");
                        line = scanner.nextLine();
                    }
                }
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
