package parser;

import lexer.LexicalAnalyzer;
import lexer.Token;
import sun.awt.windows.WPrinterJob;

import java.util.*;

/**
 * 文法：
 * E → TE'
 * E' → +TE'|-TE'|ε
 * T → FT'
 * T' → *FT'|/FT'|ε
 * F → (E)|id|num
 */


public class SyntacticAnalyzer {
    private Deque<String> S = new LinkedList<>();
    private int curIndex = 0;
    private static HashSet<String> TERMINAL = new HashSet<String>();
    private static HashSet<String> NON_TERMINAL = new HashSet<String>();
    private HashMap<String, HashMap<String, List<String>>> ANALYZE_TABLE = new HashMap<>();
    private List<Token> tokens;
    private String curTop = "#";

    static {
        TERMINAL.add("+");
        TERMINAL.add("-");
        TERMINAL.add("*");
        TERMINAL.add("/");
        TERMINAL.add("(");
        TERMINAL.add(")");
        TERMINAL.add("id");
        TERMINAL.add("num");
        NON_TERMINAL.add("E");
        NON_TERMINAL.add("E'");
        NON_TERMINAL.add("T");
        NON_TERMINAL.add("T'");
        NON_TERMINAL.add("F");
    }

    private void initTable() {
        List<String> temp = new ArrayList<>();
        HashMap<String, List<String>> tempMap = new HashMap<>();
        temp.add("E'");
        temp.add("T");
        tempMap.put("(", temp);

        temp = new ArrayList<>();
        temp.add("E'");
        temp.add("T");
        tempMap.put("id", temp);

        temp = new ArrayList<>();
        temp.add("E'");
        temp.add("T");
        tempMap.put("num", temp);
        ANALYZE_TABLE.put("E", tempMap);


        tempMap = new HashMap<>();
        temp = new ArrayList<>();
        temp.add("E'");
        temp.add("T");
        temp.add("+");
        tempMap.put("+", temp);

        temp = new ArrayList<>();
        temp.add("E'");
        temp.add("T");
        temp.add("-");
        tempMap.put("-", temp);

        temp = new ArrayList<>();
        tempMap.put(")", temp);

        temp = new ArrayList<>();
        tempMap.put("#", temp);
        ANALYZE_TABLE.put("E'", tempMap);


        temp = new ArrayList<>();
        tempMap = new HashMap<>();
        temp.add("T'");
        temp.add("F");
        tempMap.put("(", temp);

        temp = new ArrayList<>();
        temp.add("T'");
        temp.add("F");
        tempMap.put("id", temp);

        temp = new ArrayList<>();
        temp.add("T'");
        temp.add("F");
        tempMap.put("num", temp);
        ANALYZE_TABLE.put("T", tempMap);


        temp = new ArrayList<>();
        tempMap = new HashMap<>();
        temp.add("T'");
        temp.add("F");
        temp.add("*");
        tempMap.put("*", temp);

        temp = new ArrayList<>();
        temp.add("T'");
        temp.add("F");
        temp.add("/");
        tempMap.put("/", temp);


        temp = new ArrayList<>();
        tempMap.put("+", temp);

        temp = new ArrayList<>();
        tempMap.put("-", temp);

        temp = new ArrayList<>();
        tempMap.put(")", temp);
        ANALYZE_TABLE.put("T'", tempMap);

        temp = new ArrayList<>();
        tempMap.put("#", temp);
        ANALYZE_TABLE.put("T'", tempMap);


        temp = new ArrayList<>();
        tempMap = new HashMap<>();
        temp.add(")");
        temp.add("E");
        temp.add("(");
        tempMap.put("(", temp);

        temp = new ArrayList<>();
        temp.add("id");
        tempMap.put("id", temp);

        temp = new ArrayList<>();
        temp.add("num");
        tempMap.put("num", temp);
        ANALYZE_TABLE.put("F", tempMap);
    }

    public SyntacticAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        S.push("#");
        S.push("E");
        initTable();
    }

    private String getCurrentToken() {
        return tokens.get(curIndex).getValue();
    }

    public void parse() {
        tokens.add(new Token("在输入串后附加的结束标记#", "#"));
        boolean isStop = false;
        System.out.println("步骤\t 分析栈\t 输入串\t");
        int step = 1;
        while (!isStop) {

            String inputString = "";
            for (Token t : tokens.subList(curIndex, tokens.size())) {
                inputString += t.getValue();
            }
            StringBuffer sb = new StringBuffer();
            String[] temp = S.toString().substring(1, S.toString().length() - 1).split(",");
            for (int i = temp.length - 1; i != -1; i--) {
                sb.append(temp[i]);
            }
            System.out.printf("%d\t %s\t %s\t \n", step, sb.toString(), inputString);


            curTop = S.pop();
            String curToken = getCurrentToken();
            step++;
            if (TERMINAL.contains(curTop)) {
                if (curTop.equals(curToken)) {
                    advance();
                } else {
                    error();
                    break;
                }
            } else if (curTop.equals("#")) {
                if (curTop.equals(curToken)) {
                    isStop = true;
                } else {
                    error();
                    break;
                }
            } else if (isInAnalyzeTable(curTop, curToken)) {
                List<String> l = ANALYZE_TABLE.get(curTop).get(curToken);
                for (String t : l) {
                    S.push(t);
                }
            } else {
                error();
                break;
            }
        }
        System.out.println("语法分析结束");
    }

    private boolean isInAnalyzeTable(String t1, String t2) {
        return ANALYZE_TABLE.containsKey(t1) && ANALYZE_TABLE.get(t1).containsKey(t2);
    }

    private void advance() {
        this.curIndex++;
    }

    private void error() {
        System.out.println("出现错误，语法分析终止！");
    }

}
