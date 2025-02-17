import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LexicalErrorHandler {
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "int", "float", "double", "char", "if", "bool", "string", "else", "for", "while", "return", "void", "main"
    ));

    public static void checkErrors(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty() || line.trim().equals("{") || line.trim().equals("}")) {
                    continue;
                }

                //1. Illegal or Unrecognized Characters
                Pattern invalidCharPattern = Pattern.compile("[@#$%^&<>]");
                Matcher matcher = invalidCharPattern.matcher(line);
                while (matcher.find()) {
                    System.out.println("Lexical Error: Invalid character '" + matcher.group() + "' at line " + lineNumber);
                }

                //2. Misspelled Keywords 
                String[] words = line.split("\\s+|\\(|\\)|\\{|\\}|,");
                if (words.length > 1) { 
                    String firstWord = words[0];
                    if (!KEYWORDS.contains(firstWord) && firstWord.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                        System.out.println("Lexical Error: misspelled keyword '" + firstWord + "' at line " + lineNumber);
                    }
                }

                // 3. Invalid Number Formats
                Pattern invalidNumberPattern = Pattern.compile("\\d+\\.\\d+\\.\\d+|\\d+[a-zA-Z]+");
                Matcher numberMatcher = invalidNumberPattern.matcher(line);
                if (numberMatcher.find()) {
                    System.out.println("Lexical Error: Invalid number format at line " + lineNumber);
                }

                // 4. Unterminated Strings
                int quoteCount = line.length() - line.replace("\"", "").length();
                if (quoteCount % 2 != 0) {
                    System.out.println("Lexical Error: Unclosed string literal at line " + lineNumber);
                }

                // 4b. Unterminated Comments
                if (line.contains("/*") && !line.contains("*/")) {
                    System.out.println("Lexical Error: Unclosed comment at line " + lineNumber);
                }

                // 5. Excess or Missing Operators
                if (line.contains("===")) {
                    System.out.println("Lexical Error: Excess operator '===' at line " + lineNumber);
                }

                // 6. Missing Semicolon (for statements that require it)
                if (line.matches(".*(int|float|double|char)\\s+\\w+\\s*=.*[^;]\\s*$") ||  
                    line.matches(".*(\\w+)\\s*=.*[^;]\\s*$")) {  
                    System.out.println("Lexical Error: Missing semicolon at line " + lineNumber);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}