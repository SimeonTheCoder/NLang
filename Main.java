import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        HashMap<String, Float> memory = new HashMap<>();

        Node mainNode = Parser.parse(new File(args[0] + ".nlp"), memory);

        Linker.linkArgs(
                Arrays.stream(args).skip(1).map(
                        Float::parseFloat
                ).collect(Collectors.toList()).toArray(new Float[]{}),
                memory
        );

        Interpreter.interpret(mainNode, memory);
    }
}
