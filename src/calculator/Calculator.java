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
    private List<String> analyseStack;
    private ArrayList<String> inputStr;
    private int topTerminalPointer;
    private int inputStrPointer;
    private int step;
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
        inputStr = new ArrayList<>();
        analyseStack = new ArrayList<>();
        for (Token t : tokens) {
            inputStr.add(t.getValue());
        }
        boolean isStop = false;
        inputStr.add("#");
        analyseStack.add("#");
        topTerminalPointer = 0;
        inputStrPointer = 0;
        step = 1;
        System.out.printf("%s\t%s\t%s\t%s\n", "步骤", "分析栈", "剩余输入串", "动作");
        System.out.printf("%s\t%s\t%s\t%s\n", step++, analyseStack.toString(), inputStr.subList(inputStrPointer, inputStr.size()).toString(), "");
        while (!isStop) {
            topTerminalPointer = getFirstTerminalOfStack();
            if (topTerminalPointer == -1) {
                error();
                break;
            }
            String topTerminal = analyseStack.get(topTerminalPointer);
            String topStr = inputStr.get(inputStrPointer);
            int compareResult = compare(topTerminal, topStr);
            while (compareResult == -1 || compareResult == 0) {
                analyseStack.add(topStr);
                inputStrPointer++;
                System.out.printf("%s\t%s\t%s\t%s\n", step++, analyseStack.toString(), inputStr.subList(inputStrPointer, inputStr.size()).toString(), "移进");
                topStr = inputStr.get(inputStrPointer);
                topTerminalPointer = getFirstTerminalOfStack();
                topTerminal = analyseStack.get(topTerminalPointer);
                compareResult = compare(topTerminal, topStr);
            }
            reduce();
            if (analyseStack.get(getFirstTerminalOfStack()).equals("#") && inputStr.get(inputStrPointer).equals("#")) {
                isStop = true;
            }
        }
    }

    private void reduce() {
        int right = getFirstTerminalOfStack();
        int left = getFirstTerminalOfStack(right - 1);
        String rightTerminal = analyseStack.get(right);
        String leftTerminal = analyseStack.get(left);
        int start = -1;
        int end = -1;
        StringBuffer sb = new StringBuffer();
        if (digitPattern.matcher(rightTerminal).matches()) {
            start = right;
            end = right + 1;
            for (String s : analyseStack.subList(start, end)) {
                sb.append(s);
            }
            String reduceTarget = sb.toString();
            analyseStack = analyseStack.subList(0, start);
            analyseStack.add("D");
        } else if (leftTerminal.equals("(") && rightTerminal.equals(")")) {
            start = left;
            end = right + 1;
            for (String s : analyseStack.subList(start, end)) {
                sb.append(s);
            }
            String reduceTarget = sb.toString();
            analyseStack = analyseStack.subList(0, start);
            analyseStack.add("D");
        } else if (!isTerminal(analyseStack.get(right - 1)) && !isTerminal(analyseStack.get(right + 1))) {
            start = right - 1;
            end = right + 2;
            for (String s : analyseStack.subList(start, end)) {
                sb.append(s);
            }
            String reduceTarget = sb.toString();
            switch (reduceTarget.charAt(1)) {
                case '+':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("S");
                    break;
                case '-':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("A");
                    break;
                case '*':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("B");
                    break;
                case '/':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("C");
                    break;
                default:
                    error();
                    break;
            }
        }
        if (analyseStack.get(getFirstTerminalOfStack()).equals("#") && inputStr.get(inputStrPointer).equals("#")) {
            System.out.printf("%s\t%s\t%s\t%s\n", step++, analyseStack.toString(), inputStr.subList(inputStrPointer, inputStr.size()).toString(), "接受");
        }else{
            System.out.printf("%s\t%s\t%s\t%s\n", step++, analyseStack.toString(), inputStr.subList(inputStrPointer, inputStr.size()).toString(), "归约");
        }

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
