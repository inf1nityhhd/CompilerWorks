package lexer;


public class Token {
    private String type;
    private String value;
    private String rank;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(String type, String value, String rank) {
        this.type = type;
        this.value = value;
        this.rank = rank;
    }

    @Override
    public String toString() {
        if (Table.isKeyword(value) || Table.isOperatprOrDelimiter(value)) {
            return "<" + value + ", " + "-" + ">";
        } else {
            return "<" + type + value + ", " + rank + ">";
        }
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
