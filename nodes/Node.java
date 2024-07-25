package nodes;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<Node> parallelNodes;
    public List<Node> childNodes;
    public Node parentNode;

    public Object[] instruction;

    public int level;
    public int id;
    public int repetitions;

    public Node() {
        this.parallelNodes = new ArrayList<>();
        this.childNodes = new ArrayList<>();

        this.instruction = new Object[9];

        this.repetitions = 1;
    }

    public Node(Node copy) {
        this.parallelNodes = new ArrayList<>(copy.parallelNodes);
        this.childNodes = new ArrayList<>(copy.childNodes);

        this.id = copy.id;
        this.level = copy.level;

        this.parentNode = copy.parentNode;
        this.repetitions = copy.repetitions;
    }
}
