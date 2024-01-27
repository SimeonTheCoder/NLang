import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
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
    }
}
