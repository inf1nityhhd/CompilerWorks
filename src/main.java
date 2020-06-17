import lexer.LexicalAnalyzer;
import lexer.TextReader;
import lexer.Token;
import parser.SyntacticAnalyzer;

import java.io.File;
import java.util.List;

public class main {
    public static void main(String[] args) {
        TextReader r = new TextReader(new File(("src\\test2.txt")));
        String content = r.read();
        System.out.println(content);
        LexicalAnalyzer lex = LexicalAnalyzer.getInstance();
        lex.beginAnalysis(content);
        List<Token> tokens = lex.getTokenList();
//        for (Token t : tokens) {
//            System.out.println(t.getValue());
//        }
        SyntacticAnalyzer parser = new SyntacticAnalyzer(tokens);
        parser.parse();
    }
}
