import executor.ThreadManager;
import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;
import utils.EnumUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length != 0 && !args[0].startsWith("--")) {
                EnumUtils.initClass();

                HashMap<String, Float> memory = new HashMap<>();

                Parser parser = new Parser();
                Node mainNode = parser.parse(new File(args[0] + ".nlp"), memory);

                Linker linker = new Linker();

                linker.linkArgs(
                        Arrays.stream(args).skip(1).map(
                                Float::parseFloat
                        ).collect(Collectors.toList()).toArray(new Float[]{}),
                        memory
                );

                ThreadManager threadManager = new ThreadManager(8, memory);
                threadManager.enqueueNode(mainNode, 0, 0);

                double start = System.currentTimeMillis();

                threadManager.startAll();

                boolean working = true;

                while (working) {
                    working = false;

                    for(int i = 0; i < threadManager.threads.length; i++) {
                        if(threadManager.threads[i].isAlive()) {
                            working = true;
                        }
                    }
                }

                double end = System.currentTimeMillis();

                System.out.println(end - start);
//                Interpreter.interpret(mainNode, memory);
            } else {
                if(args.length != 0 && args[0].startsWith("--")) {
                    switch(args[0]) {
                        case "--build": {
                            File dir = new File(System.getProperty("user.dir") + "\\" + "build");
                            dir.mkdir();

                            System.out.println("Directory created!");

                            Runtime.getRuntime().exec("git clone https://github.com/SimeonTheCoder/NLang build");
                            TimeUnit.SECONDS.sleep(2);

                            System.out.println("Repository cloned!");

                            String path = System.getenv("JAVA_HOME") + "\\bin" + "\\javac ";

                            Files.copy(
                                    new File("CustomOperation.java").toPath(),
                                    new File("./build/CustomOperation.java").toPath(),
                                    StandardCopyOption.REPLACE_EXISTING
                            );

                            System.out.println("File copied!");

                            Runtime.getRuntime().exec("cmd.exe /c \"start build.bat\"");

                            break;
                        }

                        case "--init": {
                            File file = new File("CustomOperation.java");
                            FileWriter fileWriter = new FileWriter(file);

                            fileWriter.write("package build;\nimport data.ObjType;\n" +
                                    "import data.ReadableFile;\n" +
                                    "import data.WritableFile;\n" +
                                    "import operations.Operation;\n" +
                                    "\n" +
                                    "import java.io.IOException;\n" +
                                    "import java.util.HashMap;\n" +
                                    "\n" +
                                    "public enum CustomOperation implements Operation {\n" +
                                    "    PING {\n" +
                                    "        @Override\n" +
                                    "        public ObjType[] getArguments() {\n" +
                                    "            return new ObjType[]{};\n" +
                                    "        }\n" +
                                    "\n" +
                                    "        @Override\n" +
                                    "        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) throws IOException {\n" +
                                    "            System.out.println(\"Pong!\");\n" +
                                    "        }\n" +
                                    "    };\n" +
                                    "\n" +
                                    "    public CustomOperation value(String str) {\n" +
                                    "        switch (str) {\n" +
                                    "            case \"PING\":\n" +
                                    "                return PING;\n" +
                                    "\n" +
                                    "            default:\n" +
                                    "                return null;\n" +
                                    "        }\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    CustomOperation() {\n" +
                                    "    }\n" +
                                    "}");

                            fileWriter.close();

                            break;
                        }

                        case "--get": {
                            String repo = String.format(
                                    "https://raw.githubusercontent.com/%s/%s/main/CustomOperation.java",
                                    args[1],
                                    args[2]
                            );

                            System.out.println(repo);

                            Runtime.getRuntime().exec("curl -o CustomOperation.java "+ repo);

                            break;
                        }
                    }
                } else {
                    EnumUtils.initClass();

                    HashMap<String, Float> memory = new HashMap<>();

                    Parser parser = new Parser();

                    List<Node> nodes = new ArrayList<>();
                    int nodeId = 0;

                    Scanner scanner = new Scanner(System.in);

                    System.out.print(">> ");
                    String line = scanner.nextLine();

                    while (!line.equals("exit")) {
                        if (line.chars().allMatch(Character::isDigit)) {
                            Node node = new Node();
                            node.id = nodeId++;

                            if (nodes.size() > 0) {
                                node.parentNode = nodes.get(nodes.size() - 1);
                            }

                            nodes.add(node);

                            memory.put(String.format("a%d", (nodeId - 1) * 10), Float.parseFloat(line));
                        } else if (!line.equals("memdump")) {
                            Node node = new Node();
                            node.id = nodeId++;

                            if (nodes.size() > 0) {
                                node.parentNode = nodes.get(nodes.size() - 1);
                            }

                            nodes.add(node);

                            Interpreter.executeInstruction(
                                    parser.parseInstruction(line, node, memory),
                                    memory
                            );
                        } else {
                            for (Map.Entry<String, Float> entry : memory.entrySet()) {
                                System.out.println(entry.getKey() + " -> " + entry.getValue());
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
