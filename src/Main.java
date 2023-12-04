import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        var pathToFile = args[0];
        var analyzer = new Analyzer(pathToFile);
        analyzer.analyze();

        System.out.println("Count of declaring variables: " + analyzer.getDeclaringVariables());
        System.out.println("Count of conditional transition: " + analyzer.getConditionalTransition());
        System.out.println("Count of methods: " + analyzer.getMethodCount());
        System.out.println("Count of opcode: " + analyzer.getOpcodeCount());
        var opcodes = analyzer.getOpcodes();
        for(var key : opcodes.keySet()){
            var list = opcodes.get(key);
            var size = list.size();
            System.out.println(key);
            for(var i=0;i<size;i++){
                System.out.println(String.format("0x%02X", list.get(i)));
            }
            System.out.println();
        }
    }
}
