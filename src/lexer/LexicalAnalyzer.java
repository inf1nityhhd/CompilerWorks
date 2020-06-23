package lexer;


import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
    private static LexicalAnalyzer LEXER = new LexicalAnalyzer();
    private States curState;
    private String content;
    private int basePoint = 0;
    private int curPoint = 0;

    private LexicalAnalyzer() {

    }

    public static LexicalAnalyzer getInstance() {
        return LEXER;
    }

    public void beginAnalysis(String text) {
        this.content = text;
        this.curState = States.BEGIN;
        this.basePoint = 0;
        this.curPoint = 0;
    }

    public Token next() {
        Token temp = new Token("END", "-1");
        while (basePoint <= curPoint && curPoint < content.length()) {
            Character ch = content.charAt(curPoint);
            if (Character.isDigit(ch)) {
                if (isStateTansformed(States.DIGITS)) {
                    temp = getCurrentToken();
                    break;
                }
            } else if (Character.isLetter(ch)) {
                if (isStateTansformed(States.ALPHABET) || Table.isKeyword(content.substring(basePoint, curPoint))) {
                    temp = getCurrentToken();
                    break;
                }
            } else {
                if (Table.isDelimiter(String.valueOf(ch)) && curState == States.OPERATOR_OR_DELIMITER) {
                    temp = getCurrentToken();
                    break;
                }
                if (isStateTansformed(States.OPERATOR_OR_DELIMITER)) {
                    temp = getCurrentToken();
                    break;
                }
            }
            curPoint++;
        }
        if ((temp.getType().equals("END")) && basePoint <= curPoint && curPoint == content.length()) {
            temp = getCurrentToken();
        }
        curState = States.BEGIN;
        basePoint = curPoint;
        return temp;
    }

    public Token getCurrentToken() {
        Token temp = new Token("END", "-1");
        switch (curState) {
            case DIGITS:
                temp = Table.addNumber(new Token("常数", content.substring(basePoint, curPoint)));
                break;
            case ALPHABET:
                if (Table.isKeyword(content.substring(basePoint, curPoint))) {
                    temp = new Token("关键字", content.substring(basePoint, curPoint));
                } else {
                    temp = Table.addIdentifier(new Token("标识符", content.substring(basePoint, curPoint)));
                }
                break;
            case OPERATOR_OR_DELIMITER:
                temp = new Token("运算符或界符", content.substring(basePoint, curPoint));
                break;
            default:
                break;
        }
        return temp;
    }

    public boolean isStateTansformed(States newState) {
        if (curState == States.BEGIN) {
            curState = newState;
            return false;
        } else if (curState == newState) {
            return false;
        } else {
            return true;
        }
    }

    public List<Token> getTokenList() {
        List<Token> tokens = new ArrayList<>();
        while (true) {
            Token t = LEXER.next();
            if (t.getType() != "END") {
                tokens.add(t);
            } else {
                break;
            }
        }
        return tokens;
    }
}
