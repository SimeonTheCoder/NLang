import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length != 0) {
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
            HashMap<String, Float> memory = new HashMap<>();

            Parser parser = new Parser();

            List<Node> nodes = new ArrayList<>();
            int nodeId = 0;

            Scanner scanner = new Scanner(System.in);

            System.out.print(">> ");
            String line = scanner.nextLine();

            while(!line.equals("exit")) {
                if(line.chars().allMatch( Character::isDigit )) {
                    Node node = new Node();
                    node.id = nodeId++;

                    if(nodes.size() > 0) {
                        node.parentNode = nodes.get(nodes.size() - 1);
                    }

                    nodes.add(node);

                    memory.put(String.format("a%d", (nodeId - 1) * 10), Float.parseFloat(line));
                } else if(!line.equals("memdump")) {
                    Node node = new Node();
                    node.id = nodeId++;

                    if(nodes.size() > 0) {
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
}
