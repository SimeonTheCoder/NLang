package transformer;

import java.util.List;

public class ProgramTransformer {
    public static void transform(List<String> lines) {
        lines.replaceAll(String::trim);

        int indentation = 0;
        int currGlobal = 0;
        boolean inFunction = false;
        int nonFunctionIndent = 0;

        for(int i = 0; i < lines.size(); i ++) {
            String line = lines.get(i);

            if(line.trim().isEmpty() || line.startsWith("//")) {
                lines.remove(i--);
                continue;
            }

            if(line.startsWith("def")) {
                String varName = lines.get(i).split(" ")[1];
                lines.set(i, String.format("alias %%%d as %s", currGlobal++, varName));
            }

            if(line.contains("+") || line.contains("-") ||line.contains("*") || line.contains("/")) {
                String[] tokens = lines.get(i).split(" ");
                StringBuilder builder = new StringBuilder();

                String temp = tokens[1];
                tokens[1] = tokens[0];
                tokens[0] = temp;

                for (String token : tokens) {
                    builder.append(token);
                    builder.append(" ");
                }

                lines.set(i, builder.toString().trim());
            }

            if(line.contains("()")) {
                if(line.endsWith("{")) {
                    lines.set(i, "func " + lines.get(i));
                } else {
                    lines.set(i, "call " + lines.get(i));
                }
            }

            if(line.startsWith("func") || line.startsWith("}") || line.startsWith("{")) {
                inFunction = inFunction || lines.get(i).startsWith("func");

                if(line.startsWith("{")) {
                    nonFunctionIndent = indentation;
                }

                indentation = (line.startsWith("{") || !inFunction) ? indentation : 0;

                if(line.startsWith("}") && !inFunction) indentation = nonFunctionIndent;

                lines.set(i, "\t".repeat(indentation) + lines.get(i));

                if(lines.get(i).startsWith("func") || (lines.get(i).contains("{") && !inFunction)) indentation++;
                if(lines.get(i).startsWith("}") && inFunction) inFunction = false;
            } else {
                if (line.startsWith("alias")) continue;

                lines.set(i, "\t".repeat(indentation) + lines.get(i));

                if (!line.endsWith("|")) {
                    indentation ++;
                } else {
                    lines.set(i, lines.get(i).replace("|", ""));
                }
            }

            lines.set(i, lines.get(i).replace("(", ""));
            lines.set(i, lines.get(i).replace(")", ""));
            lines.set(i, lines.get(i).replace(">", "inp"));
            lines.set(i, lines.get(i).replace("<", "print"));

            lines.set(i, lines.get(i).replace("+", "add"));
            lines.set(i, lines.get(i).replace("-", "sub"));
            lines.set(i, lines.get(i).replace("*", "mul"));
            lines.set(i, lines.get(i).replace("/", "div"));
        }
    }
}
