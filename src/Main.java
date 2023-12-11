import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        var pathToFile = args[0];
        var analyzer = new Analyzer(pathToFile);
        analyzer.analyze();

        System.out.println("Count of declaring variables: " + analyzer.getDeclaringVariables());
        System.out.println("Count of conditional transition: " + analyzer.getConditionalTransition());
        System.out.println("Count of methods: " + analyzer.getMethodCount());
        System.out.println("Count of cycle: " + analyzer.getCycleCount());

        System.out.println();

        var opcodesCycle = analyzer.getOpcodesCycle();
        for(var method: opcodesCycle.keySet()){
            System.out.println(method);
            var opcodes = opcodesCycle.get(method);
            for(var opcode : opcodes){
                System.out.println(opcode);
            }
        }
        System.out.println("Count of opcodes cycle: " + analyzer.calculateOpcodesCycle());

        System.out.println();

        analyzer.printClass();
    }

}
