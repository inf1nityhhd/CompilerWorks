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
    private List<Node> abstractTreeNodeList;
    private boolean isStop;

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
        abstractTreeNodeList = new ArrayList<>();
        for (Token t : tokens) {
            for (char s : t.getValue().toCharArray()) {
                inputStr.add(String.valueOf(s));
            }
        }
        isStop = false;
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
            if (compareResult == 2) {
                System.out.printf("%s和%s之间不存在算符优先关系\n", topTerminal, topStr);
                return;
            }
            if (compareResult == 3) {
                return;
            }
            while (compareResult == -1 || compareResult == 0) {
                analyseStack.add(topStr);
                inputStrPointer++;
                System.out.printf("%s\t%s\t%s\t%s\n", step++, analyseStack.toString(), inputStr.subList(inputStrPointer, inputStr.size()).toString(), "移进");
                topStr = inputStr.get(inputStrPointer);
                topTerminalPointer = getFirstTerminalOfStack();
                topTerminal = analyseStack.get(topTerminalPointer);
                compareResult = compare(topTerminal, topStr);
                if (compareResult == 3) {
                    return;
                }
            }
            reduce();
            if (analyseStack.get(getFirstTerminalOfStack()).equals("#") && inputStr.get(inputStrPointer).equals("#")) {
                System.out.printf("%s%f", "计算结果所得结果为：", abstractTreeNodeList.get(0).value);
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
        if (isOperator(analyseStack.get(analyseStack.size() - 1)) && isOperator(inputStr.get(inputStrPointer))) {
            System.out.printf("算式错误：运算符%s和%s连续出现", analyseStack.get(analyseStack.size() - 1), inputStr.get(inputStrPointer));
            isStop = true;
            return;
        }
        StringBuffer sb = new StringBuffer();
        if (digitPattern.matcher(rightTerminal).matches()) {
            start = right;
            end = right + 1;
            for (String s : analyseStack.subList(start, end)) {
                sb.append(s);
            }
            abstractTreeNodeList.add(new Node(Integer.parseInt(sb.toString())));
            analyseStack = analyseStack.subList(0, start);
            analyseStack.add("D");
        } else if (leftTerminal.equals("(") && rightTerminal.equals(")")) {
            start = left;
            end = right + 1;
            for (String s : analyseStack.subList(start, end)) {
                sb.append(s);
            }
            analyseStack = analyseStack.subList(0, start);
            analyseStack.add("D");
        } else if (!isTerminal(analyseStack.get(right - 1)) && !isTerminal(analyseStack.get(right + 1))) {
            start = right - 1;
            end = right + 2;
            for (String s : analyseStack.subList(start, end)) {
                sb.append(s);
            }
            String reduceTarget = sb.toString();
            double op1 = 0;
            double op2 = 0;
            switch (reduceTarget.charAt(1)) {
                case '+':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("S");
                    op1 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 1).value;
                    op2 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value;
                    abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value = op2 + op1;
                    abstractTreeNodeList.remove(abstractTreeNodeList.size() - 1);
                    break;
                case '-':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("A");
                    op1 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 1).value;
                    op2 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value;
                    abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value = op2 - op1;
                    abstractTreeNodeList.remove(abstractTreeNodeList.size() - 1);
                    break;
                case '*':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("B");
                    op1 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 1).value;
                    op2 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value;
                    abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value = op2 * op1;
                    abstractTreeNodeList.remove(abstractTreeNodeList.size() - 1);
                    break;
                case '/':
                    analyseStack = analyseStack.subList(0, start);
                    analyseStack.add("C");
                    op1 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 1).value;
                    op2 = abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value;
                    abstractTreeNodeList.get(abstractTreeNodeList.size() - 2).value = op2 / op1;
                    abstractTreeNodeList.remove(abstractTreeNodeList.size() - 1);
                    break;
                default:
                    error();
                    isStop = true;
                    break;
            }
        } else {
            isStop = true;
            System.out.printf("待归约式 %s%s%s 无对应文法，无法归约！\n", analyseStack.get(analyseStack.size() - 3), analyseStack.get(analyseStack.size() - 2), analyseStack.get(analyseStack.size() - 1));
            return;
        }
        if (analyseStack.get(getFirstTerminalOfStack()).equals("#") && inputStr.get(inputStrPointer).equals("#")) {
            System.out.printf("%s\t%s\t%s\t%s\n", step++, analyseStack.toString(), inputStr.subList(inputStrPointer, inputStr.size()).toString(), "接受");
        } else {
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
        if (!OPERATOR_ORDER.containsKey(op1)) {
            System.out.printf("算式错误：算式中存在未知符号%s\n", op1);
            isStop = true;
        }
        if (!OPERATOR_ORDER.containsKey(op2)) {
            System.out.printf("算式错误：算式中存在未知符号%s\n", op2);
            isStop = true;
        }
        if (isStop) {
            return 3;
        }
        int order1 = OPERATOR_ORDER.get(op1);
        int order2 = OPERATOR_ORDER.get(op2);
        return OPERATER_PRIORITY_RELATION_TABLE[order1][order2];
    }

    private boolean isOperator(String op) {
        return op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("(") || op.equals(")");
    }

    class Node {
        private double value = 0;

        public Node(double value) {
            this.value = value;
        }
    }
}
