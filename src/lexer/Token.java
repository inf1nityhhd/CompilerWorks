package lexer;


public class Token {
    private String type;
    private String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        if (Table.isKeyword(value) || Table.isOperatprOrDelimiter(value)) {
            return "<" + value + "," + "-" + ">";
        } else {
            return "<" + type + "," + value + ">";
        }
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
