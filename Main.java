import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;
import utils.EnumUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        EnumUtils.initClass();

        try {
            if (args.length != 0 && !args[0].startsWith("--")) {
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

                Interpreter.interpret(mainNode, memory);
            } else {
                if(args.length != 0 && args[0].startsWith("--")) {
                    switch(args[0]) {
                        case "--build":
                            File dir = new File("build");
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

                            ProcessBuilder builder = new ProcessBuilder((path + "CustomOperation.java").split("\\s+"));
                            builder.directory(dir);

                            builder.start();

                            System.out.println(path + "CustomOperation.java");

                            TimeUnit.SECONDS.sleep(2);

                            System.out.println("Class compiled!");

                            for (File file : Objects.requireNonNull(dir.listFiles())) {
                                if(!file.getName().endsWith(".class") && file.getName().contains(".")) {
                                    file.delete();
                                } else if(!file.getName().contains(".")) {
                                    String[] entries = file.list();

                                    for(String currPath: entries){
                                        File currentFile = new File(file.getPath(), currPath);
                                        currentFile.delete();
                                    }

                                    file.delete();
                                }
                            }

                            System.out.println("Junk deleted!");

                            break;
                    }
                } else {
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
