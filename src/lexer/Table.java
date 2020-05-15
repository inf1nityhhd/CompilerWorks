package lexer;

import sun.security.krb5.internal.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    // 种别和属性值对照表
    public static Map<String, Integer> NUMBER_TABLE = new HashMap<>();
    public static Map<String, Integer> IDENTIFIER_TABLE = new HashMap<>();
    public static Map<String, Integer> KEYWORD_TABLE = new HashMap<>();
    public static Map<String, Integer> OPERATOR_TABLE = new HashMap<>();
    public static Map<String, Integer> DELIMITER_TABLE = new HashMap<>();
    public static int NUMBER_TABLE_POINTER = 0;
    public static int IDENTIFIER_TABLE_POINTER = 0;

    static {
        KEYWORD_TABLE.put("begin", 1);
        KEYWORD_TABLE.put("end", 2);
        KEYWORD_TABLE.put("if", 3);
        KEYWORD_TABLE.put("then", 4);
        KEYWORD_TABLE.put("while", 5);
        KEYWORD_TABLE.put("do", 6);
        KEYWORD_TABLE.put("const", 7);
        KEYWORD_TABLE.put("var", 8);
        KEYWORD_TABLE.put("call", 9);
        KEYWORD_TABLE.put("procedure", 10);
        OPERATOR_TABLE.put("+", 12);
        OPERATOR_TABLE.put("-", 13);
        OPERATOR_TABLE.put("*", 14);
        OPERATOR_TABLE.put("/", 15);
        OPERATOR_TABLE.put("odd", 16);
        OPERATOR_TABLE.put("=", 17);
        OPERATOR_TABLE.put("<>", 18);
        OPERATOR_TABLE.put("<", 19);
        OPERATOR_TABLE.put(">", 20);
        OPERATOR_TABLE.put("<=", 21);
        OPERATOR_TABLE.put(">=", 22);
        OPERATOR_TABLE.put(":=", 23);
        DELIMITER_TABLE.put("(", 24);
        DELIMITER_TABLE.put(")", 25);
        DELIMITER_TABLE.put(",", 26);
        DELIMITER_TABLE.put(".", 27);
        DELIMITER_TABLE.put(";", 28);
    }

    public static Token addNumber(Token token) {
        if (!NUMBER_TABLE.containsKey(token.getValue())) {
            NUMBER_TABLE.put(token.getValue(), NUMBER_TABLE_POINTER++);
        }
        Token temp = new Token(token.getType() + token.getValue(), String.valueOf(NUMBER_TABLE.get(token.getValue())));
        return temp;
    }

    public static Token addIdentifier(Token token) {
        if (!IDENTIFIER_TABLE.containsKey(token.getValue())) {
            IDENTIFIER_TABLE.put(token.getValue(), IDENTIFIER_TABLE_POINTER++);
        }
        Token temp = new Token(token.getType() + token.getValue(), String.valueOf(IDENTIFIER_TABLE.get(token.getValue())));
        return temp;
    }
//
//    public static Token addNumber(String value) {
//        Token temp = new Token("常数 " + value, String.valueOf(NUMBER_TABLE.size()));
//        NUMBER_TABLE.add(temp);
//        return temp;
//    }
//
//    public static Token addIdentifier(String value) {
//        Token temp = new Token("标识符 " + value, String.valueOf(IDENTIFIER_TABLE.size()));
//        IDENTIFIER_TABLE.add(temp);
//        return temp;
//    }

    public static boolean isOperator(String str) {
        if (OPERATOR_TABLE.containsKey(str)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isKeyword(String str) {
        if (KEYWORD_TABLE.containsKey(str)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDelimiter(String str) {
        if (DELIMITER_TABLE.containsKey(str)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOperatprOrDelimiter(String str) {
        return isDelimiter(str) || isOperator(str);
    }
}
