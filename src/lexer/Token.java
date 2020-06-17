package lexer;


public class Token {
    private String type;
    private String value;
    private int rank;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(String type, String value, int rank) {
        this.type = type;
        this.value = value;
        this.rank = rank;
    }

    @Override
    public String toString() {
        if (Table.isKeyword(value) || Table.isOperatprOrDelimiter(value)) {
            return "<" + value + ", " + "-" + ">";
        } else {
            return "<" + type + ", " + rank + ">";
        }
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
