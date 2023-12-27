//Emil Davlityarov 
//e.davlityarov@innopolis.university
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main {
    public static FileReader file;

    static {
        try {
            file = new FileReader("fsa.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));
        String s;
        Scanner scanner = new Scanner(file);
        ArrayList<String> states;
        ArrayList<String> alpha;
        String initst;
        ArrayList<String> fin;
        ArrayList<String> trans;
        s = scanner.nextLine();
        int index1 = s.indexOf("[");
        int index2 = s.indexOf("]");
        s = s.substring(index1 + 1, index2); // split 1st string
        String[] sSplit =  s.split(","); // split 1st string
        states  = new ArrayList<>(Arrays.asList(sSplit)); // states is array list that contains all stages in FSA
        for (String state : states) {
            if (!checkState(state)) { // check states on error5
                writer.write("Error:" + '\n' + "E5: Input file is malformed");
                file.close();
                writer.close();
                System.exit(0);
            }
        }
        s = scanner.nextLine();
        index1 = s.indexOf("[");
        index2 = s.indexOf("]");
        s = s.substring(index1 + 1, index2); // split 2nd string
        String[] sSplit1 =  s.split(","); // split 2nd string
        alpha  = new ArrayList<>(Arrays.asList(sSplit1)); // alpha is array list that consists all alphabets in FSA
        for (String value : alpha) { // check alphabet on error 5
            if (!checkAlpha(value)) {
                writer.write("Error:" + '\n' + "E5: Input file is malformed");
                file.close();
                writer.close();
                System.exit(0);
            }
        }
        s = scanner.nextLine();
        index1 = s.indexOf("[");
        index2 = s.indexOf("]");
        s = s.substring(index1 + 1, index2); // split 3rd string
        for (int i = 0; i < s.length(); i++){ // check initial state on error 5
            if (s.charAt(i) == ','){ // if initial state has more 2 elements (comma)
                writer.write("Error:" + '\n' + "E5: Input file is malformed");
                file.close();
                writer.close();
                System.exit(0);
            }
        }
        if (s == ""){ // check on error 5 if initial state is empty
            writer.write("Error:" + '\n' + "E4: Initial state is not defined");
            file.close();
            writer.close();
            System.exit(0);
        }
        initst  = s;
        boolean flagInist = false; // flagInit needs to check exist initial state in states
        for (String state : states) {
            if (initst.equals(state)) { // if initial state equals to some state, it's good, otherwise if
                // we don't find some state which equals to initial state, so we get error 4
                flagInist = true;
                break;
            }
        }
        if (!flagInist){ // print error 5 if flagInit is false
            writer.write("Error:" + '\n' +"E4: Initial state is not defined");
            file.close();
            writer.close();
            System.exit(0);
        }
        s = scanner.nextLine();
        index1 = s.indexOf("[");
        index2 = s.indexOf("]");
        s = s.substring(index1 + 1, index2); // split 4th string
        String[] sSplit2 =  s.split(","); // split 4th string
        fin  = new ArrayList<>(Arrays.asList(sSplit2)); // fin is Array list that consists all finite states
        s = scanner.nextLine();
        index1 = s.indexOf("[");
        index2 = s.indexOf("]");
        s = s.substring(index1 + 1, index2); // split 5th string
        String[] sSplit3 =  s.split(","); // split 5th string
        trans = new ArrayList<>(Arrays.asList(sSplit3)); // trans is array list that consists all transitions on FSA
        checkTrans(trans, states, alpha); // checkTrans is function that check all transition on error 1 or error 3
        int m = trans.size(); // count of transitions
        int n = states.size(); // count of states
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>(n); // initialize graph as adjacency list
        for(int i = 0; i < n; i++){  // initialize graph as adjacency list
            graph.add(new ArrayList<>());
        }
        for (int i = 0; i < m; i++) {
            String[] ss = trans.get(i).split(">"); // split trans array list into ">"
            int first = states.indexOf(ss[0]) ; // take index of 1st state from state array list
            int second = states.indexOf(ss[2]);// take index of 2nd state from state array list
            graph.get(first).add(second); // connect 1st and transition states
            graph.get(second).add(first); // connect transition and 1st states
        }
        int[] busy = new int[n]; // busy is array for visiting states
        for (int i = 0; i < n; i++){
            busy[i] = 0;
        }
        dfs(busy, 0, graph); // launch dfs for check error 2
        for (int i = 0; i < n ; i++){
            if (busy[i] == 0){ // if we didn't visit some state therefore we get error 2
                writer.write("Error:" + '\n' + "E2: Some states are disjoint");
                file.close();
                writer.close();
                System.exit(0);
            }
        }
        boolean complete = true; // boolean complete needs to check fsa complete or not
        ArrayList<ArrayList<Integer>> graph2 = new ArrayList<>(n); // initialize graph as adjacency list
        for(int i = 0; i < n; i++){  // initialize graph as adjacency list
            graph2.add(new ArrayList<>());
        }
        for (int i = 0; i < m; i++){
            String[] ss = trans.get(i).split(">"); // split trans array list into ">"
            int first = states.indexOf(ss[0]); // take index of 1st state from state array list
            int second = alpha.indexOf(ss[1]);// take index of alphabet from alphabet array list
            graph2.get(first).add(second); // connect 1st state and alphabet
        }
        for (int i = 0; i < graph2.size(); i++){
            Set<Integer> set = new HashSet<>(); // initialize set
            for (int j = 0; j < graph2.get(i).size(); j++){
                set.add(graph2.get(i).get(j)); // add in set all alphabets which get out of state
            }
            if (set.size() != alpha.size()){ // if size of set all alphabets which get out of state doesn't equal to
                // alphabet size, therefore our FSA is not complete
                complete = false;
                break;
            }
        }
        if (complete){ // if complete is true so fsa is complete
            writer.write("FSA is complete" + '\n');
        } else { // otherwise is not complete
            writer.write("FSA is incomplete" + '\n');
        }
        ArrayList<ArrayList<Integer>> graph4 = new ArrayList<>(n); // initialize graph
        for(int i = 0; i < n; i++){
            graph4.add(new ArrayList<>());
        }
        for (int i = 0; i < m; i++) {
            String[] ss = trans.get(i).split(">"); // split elements from trans array list
            int first = states.indexOf(ss[0]) ; // take index of 1st state from state array list
            int second = states.indexOf(ss[2]); // take index of 2nd state from state array list
            graph4.get(first).add(second); // connect 1st state and 2nd state
        }
        for (int i = 0; i < n; i++){ // all elements in busy are 0
            busy[i] = 0;
        }
        dfs(busy, states.indexOf(initst), graph4); // launch dfs from initial state for check warning 2
        boolean w2 = true;
        for (int i = 0; i < busy.length; i++){
            if (busy[i] == 0){ // if we don't visit(it means that some element from busy equals to 0) some state,
                // so we get warning 2
                w2 = false;
                break;
            }
        }

        boolean nondeterministic = false; // boolean nondeterministic needs to check deterministic or not fsa
        for (int i = 0; i < graph2.size(); i++){
            Map<Integer, Integer> map = new HashMap<>(); // initialize map to count outgoing alphabets
            for (int j = 0; j < graph2.get(i).size(); j++){
                if (map.get(graph2.get(i).get(j)) == null){ // if our value on key is null, therefore we put 1
                    map.put(graph2.get(i).get(j), 1);
                } else {
                    nondeterministic = true; // if we get value > 1, it means that the same alphabet outgoing
                    // more than once hence our fsa is nondeterministic
                    break;
                }
            }
        }
        if (!w2 || (fin.size() == 1 && fin.get(0).equals("")) || nondeterministic){
            writer.write("Warning:" + '\n');
        }
        if (fin.size() == 1 && fin.get(0).equals("")){ // if we did not get finite states(empty), so print warning 1
            writer.write("W1: Accepting state is not defined" + '\n');
        }
        if (!w2){ // print warning 2, if w2 equals to false
            writer.write("W2: Some states are not reachable from the initial state" + '\n');
        }
        if (nondeterministic){ // if nondeterministic equals to true, so print warning 3
            writer.write( "W3: FSA is nondeterministic" + '\n');
        }
        writer.close();
        file.close();
    }

    /**
     * checkTrans is method which check correct input otherwise we print error1 or error3
     * @param trans
     * @param states
     * @param alpha
     * @throws IOException
     */
    static void checkTrans(ArrayList<String> trans, ArrayList<String> states, ArrayList<String> alpha) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));
        for (int i = 0; i < trans.size(); i++){
            boolean flag1 = false;// flag1 for check correct 1st state
            boolean flag2 = false;// flag2 for check correct alphabet
            boolean flag3 = false;// flag3 for check correct 2nd state
            String[] ss = trans.get(i).split(">"); //split elements from trans array list
            for (int j = 0; j < states.size(); j++) {
                if (ss[0].equals(states.get(j))) { //if 1st state does not equal some states from states array list
                    flag1 = true;
                }
            }
            for (int j = 0; j < states.size(); j++){
                if (ss[2].equals(states.get(j))){ //if alphabet does not equal some alphabet from alpha array list
                    flag3 = true;
                }
            }
            for (int k = 0; k < alpha.size(); k++){
                if (ss[1].equals(alpha.get(k))){ //if 2nd state does not equal some states from states array list
                    flag2 = true;
                    break;
                }
            }
            if (!flag1){ // if flag1 equal to false, so print error 1
                writer.write("Error:" + '\n' +"E1: A state '" + ss[0] + "' is not in the set of states" );
                writer.close();
                file.close();
                System.exit(0);
            }
            if (!flag3){ // if flag3 equal to false, so print error 1
                writer.write("Error:" + '\n' +"E1: A state '" + ss[2] + "' is not in the set of states" );
                writer.close();
                file.close();
                System.exit(0);
            }
            if (!flag2){ // if flag2 equal to false, so print error 3
                writer.write("Error:" + '\n' + "E3: A transition '" + ss[1] + "' is not represented in the alphabet");
                writer.close();
                file.close();
                System.exit(0);
            }
        }
    }

    /**
     * check states, that they contain latin letters, words and numbers
     * @param s
     * @return true or false
     */
    static boolean checkState(String s){
        for (int i = 0; i < s.length(); i++){
            if ((s.charAt(i) >= 65 && s.charAt(i) <= 90) || (s.charAt(i) >= 48 && s.charAt(i) <= 57)
                    || (s.charAt(i) >= 97 && s.charAt(i) <= 122)){

            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * check alphabets, that they contain latin letters, words and numbers
     * @param s
     * @return true or false
     */
    static boolean checkAlpha(String s){
        // and character '_’(underscore)
        for (int i = 0; i < s.length(); i++){
            if ((s.charAt(i) >= 65 && s.charAt(i) <= 90) || (s.charAt(i) >= 48 && s.charAt(i) <= 57)
                    || (s.charAt(i) >= 97 && s.charAt(i) <= 122) || (s.charAt(i) == 95) ) {

            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * dfs needs to traversal graph
     * @param busy
     * @param vert
     * @param graph
     */
    static void dfs(int[] busy, int vert, ArrayList<ArrayList<Integer>> graph){
        busy[vert] = 1; // set to 1 if vertex v has been visited
        int size_graph = graph.get(vert).size(); // number of vertices adjacent to v
        for (int i = 0; i < size_graph; i++){ // go through the vertices adjacent to v
            int next = graph.get(vert).get(i); // // next - vertex adjacent to v
            if (busy[next] == 0 ){ // // check whether we visited this vertex or not
                dfs(busy, next, graph);
            }
        }
    }

}
