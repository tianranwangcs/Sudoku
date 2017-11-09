import java.util.ArrayList;

public class AC3 {
    private int stepCount = 0;
    private Node[][] nodes;
    private ArrayList<Arc> arcs = new ArrayList<>();

    class Arc {
        Node node1;
        Node node2;

        Arc(Node n1, Node n2) {
            node1 = n1;
            node2 = n2;
        }
    }

    AC3(Node[][] nodes) {
        System.out.println("\r\nAC3...");

        this.nodes = nodes;

        for (int i = 0; i < 9; i++) {
            addRowArcs(i);
            addColArcs(i);
            addBoxArcs(i);
        }

        while (!arcs.isEmpty()) {
            Arc arch = arcs.remove(0);
            if (!establishAC(arch)) {
                System.out.println("no solution.");
                break;
            }
        }

        int result = Node.judgeState(nodes);
        if (result != -1) {
            Node.printNodes(nodes);
        }
        if (result == 1) {
            new Backtracking(nodes, false);
        }
    }

    void addRowArcs(int row) {
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 9; j++) {
                arcs.add(new Arc(nodes[row][i], nodes[row][j]));
                arcs.add(new Arc(nodes[row][j], nodes[row][i]));
            }
        }
    }

    void addColArcs(int col) {
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 9; j++) {
                arcs.add(new Arc(nodes[i][col], nodes[j][col]));
                arcs.add(new Arc(nodes[j][col], nodes[i][col]));
            }
        }
    }

    void addBoxArcs(int box) {
        // add nodes which are in the same box together
        ArrayList<Node> nodesBox = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (nodes[i][j].box == box) {
                    nodesBox.add(nodes[i][j]);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 9; j++) {
                arcs.add(new Arc(nodesBox.get(i), nodesBox.get(j)));
                arcs.add(new Arc(nodesBox.get(j), nodesBox.get(i)));
            }
        }
    }

    boolean establishAC(Arc arc) {
        Node node1 = arc.node1;
        Node node2 = arc.node2;
        for (Integer i : node1.domain) {
            boolean isDiff = false;
            for (Integer j : node2.domain) {
                if (i != j) {
                    isDiff = true;
                    break;
                }
            }

            if (!isDiff) {
                node1.domain.remove(i);
                if (Test.VERBOSE) {
                    stepCount++;
                    System.out.print("step " + stepCount +
                            "  set.size():" + arcs.size() +
                            "  AC:" + node1.toString() + node2.toString() +
                            "  " + node1.toString() + ".remove(" + i + ")" +
                            "  domain:" + node1.printDomain() + "\r\n");
                }
                if (node1.domain.isEmpty()) {
                    return false;
                }
                addNeighborArcsExceptB(node1, node2);
            }
        }
        return true;
    }

    void addNeighborArcsExceptB(Node A, Node B) {
        // add arcs with nodes in the same row
        for (int i = 0; i < 9; i++) {
            if (i != A.col && nodes[A.row][i] != B) {
                arcs.add(new Arc(nodes[A.row][i], A));
            }
        }

        // add arcs with nodes in the same col
        for (int i = 0; i < 9; i++) {
            if (i != A.row && nodes[i][A.col] != B) {
                arcs.add(new Arc(nodes[i][A.col], A));
            }
        }

        // add arcs with nodes in the same box
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (nodes[i][j].box == A.box && i != A.row && j != A.col && nodes[i][j] != B) {
                    arcs.add(new Arc(nodes[i][j], A));
                }
            }
        }
    }
}
