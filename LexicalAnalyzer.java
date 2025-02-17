import java.util.*;

class LexicalAnalyzer {
    private Set<String> keywords;
    private Set<Character> operators;
    private List<Token> tokens;
    private int currentLine;
    private int currentColumn;
    private String source;
    private int pos;

    public LexicalAnalyzer() {
        keywords = new HashSet<>(Arrays.asList("int","if", "else", "while", "for", "return", "true", "false","string","char","float","double","bool","const","switch","case","break","continue","default","void","printf"));
        operators = new HashSet<>(Arrays.asList('+', '-', '*', '/', '%', '^', '=','>','<','!','&','|'));
        tokens = new ArrayList<>();
        currentLine = 1;
        currentColumn = 1;
    }

    public List<Token> tokenize(String source) throws SyntaxError {
        this.source = source;
        this.pos = 0;
        tokens.clear();

        while (pos < source.length()) {
            char ch = source.charAt(pos);

            // Skip whitespace
            if (Character.isWhitespace(ch)) {
                if (ch == '\n') {
                    currentLine++;
                    currentColumn = 1;
                } else {
                    currentColumn++;
                }
                pos++;
                continue;
            }

            // Handle comments
            if (ch == '/' && pos + 1 < source.length()) {
                char nextChar = source.charAt(pos + 1);
                if (nextChar == '/') {
                    handleSingleLineComment();
                    continue;
                } else if (nextChar == '*') {
                    handleMultiLineComment();
                    continue;
                }
            }

            // Handle identifiers
            if (Character.isLowerCase(ch)) {
                handleIdentifier();
                continue;
            }

            // Handle numbers
            if (Character.isDigit(ch) || ch == '.') {
                handleNumber();
                continue;
            }

            // Handle operators
            if (operators.contains(ch)) {
                tokens.add(new Token(TokenType.OPERATOR, 
                    String.valueOf(ch), currentLine, currentColumn));
                currentColumn++;
                pos++;
                continue;
            }
            // Handle strings
            if (ch == '"') {
                StringBuilder literal = new StringBuilder();
                int startColumn = currentColumn;
                literal.append(ch);
                currentColumn++;
                pos++;
                while (pos < source.length() && source.charAt(pos) != '"') {
                    ch = source.charAt(pos);
                    literal.append(ch);
                    currentColumn++;
                    pos++;
                }
                if (pos < source.length()) {
                    literal.append('"');
                    currentColumn++;
                    pos++;
                } else {
                    throw new SyntaxError("Unterminated string literal at line " + currentLine + ", column " + startColumn);
                }
                tokens.add(new Token(TokenType.LITERAL, literal.toString(), currentLine, startColumn));
                continue;
            }

            if(ch == '\'' && pos + 2 < source.length()){
                StringBuilder literal = new StringBuilder();
                int startColumn = currentColumn;
                literal.append(ch);
                currentColumn++;
                pos++;
                while (pos < source.length() && source.charAt(pos) != '\'') {
                    ch = source.charAt(pos);
                    literal.append(ch);
                    currentColumn++;
                    pos++;
                }
                if (pos < source.length()) {
                    literal.append('\'');
                    currentColumn++;
                    pos++;
                } else {
                    throw new SyntaxError("Unterminated character literal at line " + currentLine + ", column " + startColumn);
                }
                tokens.add(new Token(TokenType.CHARACTER, literal.toString(), currentLine, startColumn));
                continue;
            }

            // Handle semicolons
            if (ch == ';'||ch == '{'||ch == '}'||ch == '('||ch == ')'||ch == ',' || ch == '[' || ch == ']') { 
                tokens.add(new Token(TokenType.OPERATOR, 
                    String.valueOf(ch), currentLine, currentColumn));
                currentColumn++;
                pos++;
                continue;
            }

            

            throw new SyntaxError("Unrecognized character '" + ch + 
                "' at line " + currentLine + ", column " + currentColumn);
                
        }

        tokens.add(new Token(TokenType.EOF, "", currentLine, currentColumn));
        return tokens;
    }

    private void handleIdentifier() {
        StringBuilder identifier = new StringBuilder();
        int startColumn = currentColumn;

        while (pos < source.length() && 
               Character.isLowerCase(source.charAt(pos))||Character.isDigit(source.charAt(pos))||source.charAt(pos)=='_') {
            identifier.append(source.charAt(pos));
            currentColumn++;
            pos++;
        }

        String identifierStr = identifier.toString();
        TokenType type = keywords.contains(identifierStr) ? 
            TokenType.KEYWORD : TokenType.IDENTIFIER;
        tokens.add(new Token(type, identifierStr, currentLine, startColumn));
    }

    private void handleNumber() {
        StringBuilder number = new StringBuilder();
        int startColumn = currentColumn;
        boolean isDecimal = false;
        int decimalPlaces = 0;

        while (pos < source.length()) {
            char ch = source.charAt(pos);
            if (Character.isDigit(ch)) {
                number.append(ch);
                if (isDecimal) {
                    decimalPlaces++;
                }
            } else if (ch == '.' && !isDecimal) {
                number.append(ch);
                isDecimal = true;
            } else {
                break;
            }
            currentColumn++;
            pos++;
        }

        if (isDecimal) {
            if (decimalPlaces > 5) {
                double value = Double.parseDouble(number.toString());
                value = Math.round(value * 100000.0) / 100000.0;
                number = new StringBuilder(String.valueOf(value));
            }
            tokens.add(new Token(TokenType.DECIMAL, number.toString(), 
                currentLine, startColumn));
        } else {
            tokens.add(new Token(TokenType.INTEGER, number.toString(), 
                currentLine, startColumn));
        }
    }

    private void handleSingleLineComment() {
        int startColumn = currentColumn;
        StringBuilder comment = new StringBuilder("//");
        pos += 2; // Skip //
        
        while (pos < source.length() && source.charAt(pos) != '\n') {
            comment.append(source.charAt(pos));
            pos++;
            currentColumn++;
        }
        
        tokens.add(new Token(TokenType.COMMENT, comment.toString(), currentLine, startColumn));
        
        currentLine++;
        currentColumn = 1;
        if (pos < source.length()) pos++;
    }

    private void handleMultiLineComment() {
        int startColumn = currentColumn;
        int startLine = currentLine;
        StringBuilder comment = new StringBuilder("/*");
        pos += 2; // Skip /*
        
        while (pos < source.length() - 1) {
            comment.append(source.charAt(pos));
            if (source.charAt(pos) == '*' && 
                source.charAt(pos + 1) == '/') {
                comment.append('/');
                pos += 2;
                currentColumn += 2;
                break;
            }
            if (source.charAt(pos) == '\n') {
                currentLine++;
                currentColumn = 1;
            } else {
                currentColumn++;
            }
            pos++;
        }
        
        tokens.add(new Token(TokenType.COMMENT, comment.toString(), startLine, startColumn));
    }
}

class SyntaxError extends Exception {
    public SyntaxError(String message) {
        super(message);
    }
}