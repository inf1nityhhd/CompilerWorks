import calculator.Calculator;
import lexer.LexicalAnalyzer;
import lexer.TextReader;
import lexer.Token;
import parser.SyntacticAnalyzer;

import java.io.File;
import java.util.List;

public class main {
    public static void main(String[] args) {
        TextReader r = new TextReader(new File("src\\test2.txt"));
        String content = r.read();
        LexicalAnalyzer lex = LexicalAnalyzer.getInstance();
        lex.beginAnalysis(content);
        List<Token> tokens = lex.getTokenList();
//        for (Token t : tokens) {
//            System.out.println(t.getValue());
//        }
//        SyntacticAnalyzer parser = new SyntacticAnalyzer(tokens);
//        parser.parse();
        r = new TextReader(new File("src\\test3.txt"));
        content = r.read();
        lex.beginAnalysis(content);
        tokens = lex.getTokenList();
//        for (Token t : tokens) {
//            System.out.println(t.getValue() + t.getType());
//        }
        Calculator c = new Calculator();
        c.analyse(tokens);
    }
}
