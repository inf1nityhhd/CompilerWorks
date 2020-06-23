package calculator;

import lexer.Token;

import java.util.*;
import java.util.regex.Pattern;

public class Calculator {
    /**
     * 算符优先关系表 OPERATER_PRIORITY_RELATION_TABLE
     * >=<和无定义分别用1,0,-1,2表示
     * 行列为 + - * ÷ ( ) number #
     */
    private final int[][] OPERATER_PRIORITY_RELATION_TABLE = new int[][]{
            {1, -1, -1, -1, -1, 1, -1, 1},
            {1, 1, -1, -1, -1, 1, -1, 1},
            {1, 1, 1, -1, -1, 1, -1, 1},
            {1, 1, 1, 1, -1, 1, -1, 1},
            {-1, -1, -1, -1, -1, 0, -1, 1},
            {1, 1, 1, 1, 2, 1, 2, 1},
            {1, 1, 1, 1, 2, 1, 2, 1},
            {-1, -1, -1, -1, -1, -1, -1, 0}
    };
    private final Pattern digitPattern = Pattern.compile("[0-9]*");
    private HashMap<String, Integer> OPERATOR_ORDER = new HashMap<>();
    List<String> analyseStack;

    public Calculator() {
        initOperatorOrder();
    }

    private boolean isTerminal(String t) {
        if (t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/") || t.equals("(") || t.equals(")") || t.equals("#")
                || digitPattern.matcher(t).matches()) {
            return true;
        } else {
            return false;
        }
    }

    public void analyse(List<Token> tokens) {
        ArrayList<String> inputStr = new ArrayList<>();
        analyseStack = new ArrayList<>();
        for (Token t : tokens) {
            inputStr.add(t.getValue());
        }
        boolean isStop = false;
        inputStr.add("#");
        analyseStack.add("#");
        int topTerminalPointer;
        int inputStrPointer = 0;

        while (!isStop) {
            topTerminalPointer = getFirstTerminalOfStack();
            if (topTerminalPointer == -1) {
                error();
                break;
            }
            String topTerminal = analyseStack.get(topTerminalPointer);
            String topStr = inputStr.get(inputStrPointer);
            int compareResult = compare(topTerminal, topStr);
            System.out.println(topTerminal+"和"+topStr+"的比较结果"+compareResult);
            while (compareResult == -1 || compareResult == 0) {
                analyseStack.add(topStr);
                inputStrPointer++;
                topStr = inputStr.get(inputStrPointer);
                topTerminalPointer = getFirstTerminalOfStack();
                topTerminal = analyseStack.get(topTerminalPointer);
                compareResult = compare(topTerminal, topStr);
            }
            System.out.println(analyseStack.toString());
            topTerminalPointer = getFirstTerminalOfStack();
            int start = getFirstTerminalOfStack(topTerminalPointer - 1);
            reduce(start + 1);
        }
    }

    private void reduce(int start) {
        StringBuffer sb = new StringBuffer();
        for (String s : analyseStack.subList(start, analyseStack.size())) {
            sb.append(s);
        }
        String reduceTarget = sb.toString();
        System.out.println("归约目标：" + reduceTarget);
        analyseStack = analyseStack.subList(0, start);
        analyseStack.add("D");
        System.out.println("规约后：" + analyseStack.toString());
    }


    private int getFirstTerminalOfStack() {
        int result = -1;
        for (int i = analyseStack.size() - 1; i >= 0; i--) {
            if (isTerminal(analyseStack.get(i))) {
                result = i;
                break;
            }
        }
        return result;
    }

    private int getFirstTerminalOfStack(int end) {
        int result = -1;
        for (int i = end; i >= 0; i--) {
            if (isTerminal(analyseStack.get(i))) {
                result = i;
                break;
            }
        }
        return result;
    }


    private void error() {
        System.out.println("算符优先分析过程中出现错误！");
    }

    private void initOperatorOrder() {
        OPERATOR_ORDER.put("+", 0);
        OPERATOR_ORDER.put("-", 1);
        OPERATOR_ORDER.put("*", 2);
        OPERATOR_ORDER.put("/", 3);
        OPERATOR_ORDER.put("(", 4);
        OPERATOR_ORDER.put(")", 5);
        OPERATOR_ORDER.put("number", 6);
        OPERATOR_ORDER.put("#", 7);
    }

    private int compare(String op1, String op2) {
        if (digitPattern.matcher(op1).matches()) {
            op1 = "number";
        }
        if (digitPattern.matcher(op2).matches()) {
            op2 = "number";
        }
        int order1 = OPERATOR_ORDER.get(op1);
        int order2 = OPERATOR_ORDER.get(op2);
        return OPERATER_PRIORITY_RELATION_TABLE[order1][order2];
    }
}
