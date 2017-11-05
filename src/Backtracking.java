import java.util.*;

public class Backtracking {
    private int stepCount = 0;
    private Node[][] nodes;
    private Stack<Record> stack = new Stack<>();

    class Record {
        Node node;
        int removedValue;

        Record(Node node, int value) {
            this.node = node;
            removedValue = value;
        }
    }

    Backtracking(Node[][] nodes) {
        System.out.println("\r\nfirst time of forward checking...");

        this.nodes = nodes;
        if (!forwardCheck()) {
            System.out.println("no solution");
            return;
        }
        Node.printNodes(nodes);

        System.out.println("\r\nbacktracking...");
        if (search(getHeuristicNode()) == -1) {
            System.out.println("no solution");
        } else {
            Node.printNodes(nodes);
        }
    }

    Backtracking(Node[][] nodes, boolean notUsingForwardCheck) {
        System.out.println("\r\nbacktracking...");

        this.nodes = nodes;
        if (search(getHeuristicNode()) == -1) {
            System.out.println("no solution");
        } else {
            Node.printNodes(nodes);
        }
    }

    // use recursion to search value for all the nodes
    //
    // error, return -1
    // not end, return 1
    // find solution, return 0
    int search(Node node) {
        int state = stack.size();

        // 0 or -1
        int result = Node.judgeState(nodes);
        if (result != 1) {
            return result;
        }
        if (node == null) {
            return -1;
        }

        // not end, iterate each value of the node
        int value = getHeuristicValue(node);
        while (value != 0) {
            if (Test.VERBOSE) {
                stepCount++;
                System.out.println("step " + stepCount + "  try " + value + " for " + node.toString());
            }
            setNodeValue(node, value);
            if (!forwardCheck()) {
                // if this value is invalid
                backtrackState(state);
                if (!removeNodeValue(node, value)) {
                    return -1;
                }
                state = stack.size();
                value = getHeuristicValue(node);
            } else {
                // if this value is valid
                // search the next node
                result = search(getHeuristicNode());
                if (result == 0) {
                    return 0;
                } else if (result == -1) {
                    backtrackState(state);
                    if (!removeNodeValue(node, value)) {
                        return -1;
                    }
                    state = stack.size();
                }
                value = getHeuristicValue(node);
            }
        }
        return -1;
    }

    // establish one node's neighbors arc consistency with it: arc(Y,X)
    // when this node has one value in its domain
    boolean forwardCheck() {
        boolean isContinue = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (nodes[i][j].domain.size() == 1) {
                    ArrayList<Node> neighbors = getNeighbors(nodes[i][j]);
                    for (Node n : neighbors) {
                        int value = nodes[i][j].domain.first();
                        if (!removeNodeValue(n, value)) {
                            return false;
                        } else if (n.domain.size() == 1) {
                            isContinue = true;
                        }
                    }
                }
            }
        }
        if (isContinue) {
            return forwardCheck();
        }
        return true;
    }

    // choose one unassigned node with the minimum remaining value
    Node getHeuristicNode() {
        ArrayList<Node> nodeList = new ArrayList<>();
        // minimum size start from 2 because all nodes with 1 value in domain are assigned
        for (int size = 2; size <= 9; size++) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (nodes[i][j].domain.size() == size) {
                        nodeList.add(nodes[i][j]);
                    }
                }
            }
            if (!nodeList.isEmpty()) {
                break;
            }
        }
        if (nodeList.isEmpty()) {
            return null;
        }
        return nodeList.get(0);
    }

    // assign the selected node with the least constraining value
    int getHeuristicValue(Node node) {
        ArrayList<Node> neighbours = getNeighbors(node);
        HashMap<Integer, Integer> countMap = new HashMap();
        for (int value : node.domain) {
            int count = 0;
            for (Node n : neighbours) {
                count += n.domain.size();
                if (n.domain.contains(value)) {
                    count--;
                }
            }
            countMap.put(value, count);
        }

        int key = 0;
        int maxCount = -1; // maxCount may be 0
        for (HashMap.Entry<Integer, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                key = entry.getKey();
            }
        }
        return key;
    }

    // remove one value from one node's domain and return true
    // return false when the domain is empty
    boolean removeNodeValue(Node node, int value) {
        if (node.domain.contains(value)) {
            node.domain.remove(value);
            stack.push(new Record(node, value));
            if (node.domain.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    void setNodeValue(Node node, int value) {
        for (int i : node.domain) {
            if (i != value) {
                node.domain.remove(i);
                stack.push(new Record(node, i));
            }
        }
    }

    void backtrackState(int state) {
        while (stack.size() > state) {
            Record record = stack.pop();
            record.node.domain.add(record.removedValue);
        }
    }

    // get neighbors whose domains have more than 1 node
    ArrayList<Node> getNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<>();

        // same row
        try {
            for (int i = 0; i < 9; i++) {
                if (i != node.col && nodes[node.row][i].domain.size() > 1) {
                    neighbors.add(nodes[node.row][i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // same col
        for (int i = 0; i < 9; i++) {
            if (i != node.row && nodes[i][node.col].domain.size() > 1) {
                neighbors.add(nodes[i][node.col]);
            }
        }

        // same box
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i != node.row && j != node.col && nodes[i][j].box == node.box
                        && nodes[i][j].domain.size() > 1) {
                    neighbors.add(nodes[i][j]);
                }
            }
        }
        return neighbors;
    }
}
