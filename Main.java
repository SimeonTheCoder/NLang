import executor.ThreadManager;
import memory.MemoryManager;
import nodes.Node;
import operations.Operation;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;
import utils.EnumUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        MemoryManager.set(2048, 512, 4);

        try {
            if (args.length != 0 && !args[0].startsWith("--")) {
                EnumUtils.initClass();

                float[] memory = new float[MemoryManager.TOTAL_AMOUNT];

                int toSkip = 1;
                int toReadArg = 0;

                for (int i = 0; i < args.length; i ++) {
                    if (args[i].charAt(0) == '-') {
                        int val = Integer.parseInt(args[i].substring(2));

                        switch (args[i].charAt(1)) {
                            case 'G' -> MemoryManager.GLOBAL_AMOUNT = val;
                            case 'L' -> MemoryManager.LOCAL_AMOUNT = val;
                            case 'T' -> MemoryManager.TOTAL_AMOUNT = val;
                            case 'N' -> MemoryManager.NODE_SLOTS = val;
                        }

                        toSkip++;
                    } else {
                        toReadArg = i;
                    }
                }

                Parser parser = new Parser();
                Node mainNode = parser.parse(new File(args[toReadArg] + ".nlp"), memory);

                Linker linker = new Linker();

                linker.linkArgs(
                        Arrays.stream(args).skip(toSkip).map(
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

                long end = System.currentTimeMillis();
                System.out.println(end - start);
            } else {
                if(args.length != 0 && args[0].startsWith("--")) {
                    if (args[0].equals("--get")) {
                        File theDir = new File(System.getProperty("user.dir") + "/build");
                        if (!theDir.exists()){
                            theDir.mkdirs();
                        }

                        String repo = String.format(
                                "https://raw.githubusercontent.com/%s/%s/main/package.nlb",
                                args[1],
                                args[2]
                        );

                        System.out.println("Downloading package.nlb from: " + repo);

                        Runtime.getRuntime().exec("curl -o build/package.nlb " + repo);

                        Scanner scanner = new Scanner(new File("package.nlb"));

                        while(scanner.hasNextLine()) {
                            String line = scanner.nextLine();

                            String fileLocation = String.format(
                                    "https://raw.githubusercontent.com/%s/%s/main/%s.class",
                                    args[1],
                                    args[2],
                                    line
                            );

                            System.out.println("Downloading file from: " + fileLocation);

                            Runtime.getRuntime().exec(
                                    String.format("curl -o build/%s.class %s", line, fileLocation)
                            );
                        }
                    }
                } else {
                    EnumUtils.initClass();

                    float[] memory = new float[MemoryManager.TOTAL_AMOUNT];

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

                            memory[(nodeId - 1) * MemoryManager.NODE_SLOTS] = Float.parseFloat(line);
                        } else if (!line.startsWith("memdump")) {
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
                            boolean includeZero = line.endsWith("+0");

                            if(line.contains("local")) {
                                for (int i = 0; i < MemoryManager.LOCAL_AMOUNT; i ++) {
                                    if(memory[i] == 0f && !includeZero) continue;
                                    System.out.println("$" + i + " -> " + memory[i]);
                                }
                            } else if (line.contains("global")) {
                                for (int i = MemoryManager.LOCAL_AMOUNT; i < MemoryManager.TOTAL_AMOUNT; i ++) {
                                    if(memory[i] == 0f && !includeZero) continue;
                                    System.out.println("$" + i + " (%" + (i - MemoryManager.LOCAL_AMOUNT) + ") -> " + memory[i]);
                                }
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
