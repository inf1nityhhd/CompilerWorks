import lexer.LexicalAnalyzer;
import lexer.Table;
import lexer.TextReader;
import lexer.Token;

import java.io.File;

public class main {
    public static void main(String[] args) {
        TextReader r = new TextReader(new File(("src\\test.txt")));
        String content = r.read();
        System.out.println(content);
        LexicalAnalyzer lex = LexicalAnalyzer.getInstance();
        lex.beginAnalysis(content, 0, 0);
        while (true) {
            Token t = lex.next();
            if (t.getType() != "END") {
                System.out.println(t.toString());
            } else {
                break;
            }
        }
    }
}
