import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Analyzer {
    private ClassReader classReader;
    private ClassNode classNode;
    private MorningVisitor visitor;
    private HashMap<String, ArrayList<Integer>> opcodesCycles;

    public Analyzer(String path) throws IOException {
        classReader = new ClassReader(new FileInputStream(path));
        visitor = new MorningVisitor(Opcodes.ASM9);
        classNode = new ClassNode();
        opcodesCycles = new HashMap<>();
    }

    public void analyze() {
        classReader.accept(visitor, 0);
        classReader.accept(classNode, 0);
        scanOpcodesMethods();
    }

    public int getConditionalTransition() {
        return visitor.getConditionalTransition();
    }

    public int getDeclaringVariables() {
        return visitor.getFieldCount() + visitor.getLocalVariableCount();
    }
    public HashMap<String, ArrayList<Integer>> getOpcodesCycle(){
        return opcodesCycles;
    }

    public int calculateOpcodesCycle() {
        var result = 0;
        for (var key : opcodesCycles.keySet()) {
            result += opcodesCycles.get(key).size();
        }
        return result;
    }
    public void printClass(){
        for (var method : classNode.methods) {
            System.out.println("Method: " + method.name + method.desc);
            var instructions = method.instructions;
            for(var i = 0; i< instructions.size();i++){
                if(instructions.get(i).getOpcode()!=-1) System.out.println(i + ":" + instructions.get(i).getOpcode());
            }
        }
    }

    private void scanOpcodesMethods() {
        for (var method : classNode.methods) {
            var instructions = method.instructions;
            for (int i = 0; i < instructions.size(); i++) {
                var instruction = instructions.get(i);
                if (instruction instanceof JumpInsnNode) {
                    var jump = (JumpInsnNode) instruction;
                    var label = jump.label;
                    if (i > instructions.indexOf(label)) {
                        var key = method.name + " "+ i;
                        if (!opcodesCycles.containsKey(key)) {
                            opcodesCycles.put(key, new ArrayList<>());
                            opcodesCycles.get(key).addAll(getOpcodes(instructions, i, instructions.indexOf(label)));
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Integer> getOpcodes(InsnList instructions, int end, int begin) {
        var result = new ArrayList<Integer>();
        for (var i = begin; i <= end; i++) {
            if (instructions.get(i).getOpcode() != -1) {
                result.add(instructions.get(i).getOpcode());
            }
        }
        return result;
    }
}