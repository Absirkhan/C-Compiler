import java.io.*;
import java.util.*;
import java.util.regex.*;

class SymbolTableExtractor {
    static class Symbol {
        String name;
        String type;
        String scope;
        String value; // Stores assigned value (if any)

        Symbol(String name, String type, String scope, String value) {
            this.name = name;
            this.type = type;
            this.scope = scope;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%-15s %-10s %-10s %-10s", name, type, scope, value == null ? "N/A" : value);
        }
    }

    static List<Symbol> symbolTable = new ArrayList<>();

    // Regular expressions
    static final String VARIABLE_PATTERN = "\\b(int|float|double|char|string)\\s+([^;]+);";
    static final String FUNCTION_PATTERN = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^)]*\\)\\s*\\{";
    static final String DEFINE_PATTERN = "#define\\s+([A-Z_]+)\\s+(.+)";

    public static void main(String[] args) throws IOException {
        String filename = "sample.c"; // Replace with your C file
        extractSymbols(filename);
        printSymbolTable();
    }

    static void extractSymbols(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        boolean insideFunction = false;
        
        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Skip comments
            if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) continue;

            // Detect function definitions
            Matcher funcMatcher = Pattern.compile(FUNCTION_PATTERN).matcher(line);
            if (funcMatcher.find()) {
                String functionName = funcMatcher.group(1);
                symbolTable.add(new Symbol(functionName, "Function", "Global", "N/A"));
                insideFunction = true;
                continue;
            }

            // Detect variable declarations (handles multiple variables in one line)
            Matcher varMatcher = Pattern.compile(VARIABLE_PATTERN).matcher(line);
            if (varMatcher.find()) {
                String type = varMatcher.group(1);
                String declaration = varMatcher.group(2); // Full declaration part after data type

                // Split multiple variables (comma-separated)
                String[] variables = declaration.split(",");

                for (String var : variables) {
                    var = var.trim();
                    String name, value = null;

                    // Check if variable has an assignment
                    if (var.contains("=")) {
                        String[] parts = var.split("=");
                        name = parts[0].trim();
                        value = parts[1].trim();
                    } else {
                        name = var.trim();
                    }

                    String scope = insideFunction ? "Local" : "Global";
                    symbolTable.add(new Symbol(name, type, scope, value));
                }
            }

            // Detect constants using #define
            Matcher defineMatcher = Pattern.compile(DEFINE_PATTERN).matcher(line);
            if (defineMatcher.find()) {
                String constantName = defineMatcher.group(1);
                String constantValue = defineMatcher.group(2);
                symbolTable.add(new Symbol(constantName, "Constant", "Global", constantValue));
            }

            // Detect end of function scope
            if (line.equals("}")) {
                insideFunction = false;
            }
        }
        br.close();
    }

    static void printSymbolTable() {
        System.out.println("\nSymbol Table:");
        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-15s %-10s %-10s %-10s\n", "Name", "Type", "Scope", "Value");
        System.out.println("-------------------------------------------------------------");
        for (Symbol symbol : symbolTable) {
            System.out.println(symbol);
        }
    }
}
