import java.io.*;
import java.util.*;

public class App {
    public static void main(String[] args) {
        try {
            try (Scanner in = new Scanner(System.in)) {
                System.out.println("Enter a regular expression");
                String re = in.nextLine();
                System.out.println("re:" + re);
                NFA nfa = new NFA(re);
                nfa.add_join_symbol();
                nfa.postfix();
                nfa.re2nfa();
                nfa.print();
                
                DFA dfa = new DFA(nfa.getPair(),nfa.getLetter());
                dfa.createDFA();
                dfa.printDFA();
                
                
                System.out.println();
                System.out.println("re:" + re);
            }
            File file = new File("src/input.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder code = new StringBuilder();
            String line;
            
            while ((line = br.readLine()) != null) {
                code.append(line).append("\n");
            }
            br.close();
            
            LexicalAnalyzer lexer = new LexicalAnalyzer();

            // Lexical Analysis
            List<Token> tokens = null;
            try {
                tokens = lexer.tokenize(code.toString());
                System.out.println("\nTokens:");
                for (Token token : tokens) {
                    System.out.println(token);
                }
            } catch (SyntaxError e) {
            }


            // Extract Symbols
            SymbolTableExtractor.extractSymbols("src/input.txt");
            SymbolTableExtractor.printSymbolTable();
            
            System.out.println("\nChecking for Lexical Errors:");
            LexicalErrorHandler.checkErrors("src/input.txt");
            
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
        }
    }
}