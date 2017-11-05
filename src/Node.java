import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;

class Node {
    int row;
    int col;
    int box;
    ConcurrentSkipListSet<Integer> domain;

    Node(int row, int col) {
        this.row = row;
        this.col = col;
        box = calcBox(row, col);
        domain = new ConcurrentSkipListSet<>();
        for (int i = 1; i <= 9; i++) {
            domain.add(i);
        }
    }

    Node(int row, int col, int value) {
        this.row = row;
        this.col = col;
        box = calcBox(row, col);
        domain = new ConcurrentSkipListSet<>();
        domain.add(value);
    }

    static Node[][] getNodes(String filename) {
        Node[][] nodes = new Node[9][9];
        try {
            String dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "data";
            BufferedReader in = new BufferedReader(new FileReader(dir + File.separator + filename));
            String line;
            for (int i = 0; i < 9; i++) {
                if ((line = in.readLine()) != null) {
                    String[] numbers = line.trim().split(" ");
                    for (int j = 0; j < 9; j++) {
                        int number = Integer.parseInt(numbers[j]);
                        if (number > 0 && number < 10) {
                            nodes[i][j] = new Node(i, j, number);
                        } else {
                            nodes[i][j] = new Node(i, j);
                        }
                    }
                } else {
                    for (int j = 0; j < 9; j++) {
                        nodes[i][j] = new Node(i, j);
                    }
                }
            }
            printPuzzle(filename, nodes);
            return nodes;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    static void printNodes(Node[][] nodes) {
        System.out.print("\r\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(nodes[i][j].printDomain());
            }
            System.out.print("\r\n");
        }
    }

    private static void printPuzzle(String filename, Node[][] nodes) {
        System.out.println("loading puzzle from " + filename + "...\r\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (nodes[i][j].domain.size() > 1) {
                    System.out.print("[0]");
                } else {
                    System.out.print(nodes[i][j].printDomain());
                }
            }
            System.out.print("\r\n");
        }
    }

    // error, return -1
    // not end, return 1
    // find solution, return 0
    static int judgeState(Node[][] nodes) {
        boolean notEnd = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (nodes[i][j].domain.size() == 0) {
                    return -1;
                } else if (nodes[i][j].domain.size() > 1) {
                    notEnd = true;
                }
            }
        }
        if (notEnd) {
            return 1;
        }

        // check all rows & columns
        HashSet<Integer> rowSet;
        HashSet<Integer> colSet;
        for (int i = 0; i < 9; i++) {
            rowSet = new HashSet<>();
            colSet = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                rowSet.add(nodes[i][j].domain.first());
                colSet.add(nodes[j][i].domain.first());
            }
            if (rowSet.size() != 9 || colSet.size() != 9) {
                return -1;
            }
        }

        // check all boxes
        HashMap<Integer, Integer> boxesMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int box = nodes[i][j].box;
                int sum = 0;
                if (boxesMap.get(box) != null) {
                    sum = boxesMap.get(box);
                }
                sum += nodes[i][j].domain.first();
                boxesMap.put(box, sum);
            }
        }
        for (HashMap.Entry<Integer, Integer> entry : boxesMap.entrySet()) {
            if (entry.getValue() != 45) {
                return -1;
            }
        }

        return 0;
    }

    private int calcBox(int row, int col) {
        int box = 0;
        if (row < 3) {
            if (col < 3) {
                box = 0;
            } else if (col < 6) {
                box = 1;
            } else {
                box = 2;
            }
        } else if (row < 6) {
            if (col < 3) {
                box = 3;
            } else if (col < 6) {
                box = 4;
            } else {
                box = 5;
            }
        } else {
            if (col < 3) {
                box = 6;
            } else if (col < 6) {
                box = 7;
            } else {
                box = 8;
            }
        }
        return box;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        Node otherNode = (Node) other;
        if (otherNode.row == this.row && otherNode.col == this.col && otherNode.box == this.box) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + this.row + "," + this.col + ")";
    }

    String printDomain() {
        String str = "[";
        for (Integer i : domain) {
            str = str + i + "";
        }
        str += "]";
        return str;
    }
}