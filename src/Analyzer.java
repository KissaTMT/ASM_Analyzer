import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Analyzer {
    private String path;
    private ClassReader classReader;
    private MorningVisitor visitor;
    private HashMap<String,ArrayList<Integer>> opcodesCycles;
    public Analyzer(String path) throws IOException {
        this.path = path;
        classReader = new ClassReader(new FileInputStream(path));
        visitor = new MorningVisitor(Opcodes.ASM9);
        opcodesCycles = new HashMap<>();
    }
    private int useApiTreeToCalculateCountOfCycles(){
        var classNode = new ClassNode();
        var counter = 0;
        classReader.accept(classNode, 0);

        for (var method : classNode.methods) {
            var instructions = method.instructions;

            for (int i = 0; i < instructions.size(); i++) {
                var instruction = instructions.get(i);
                if (instruction instanceof JumpInsnNode) {
                    var jumpInsn = (JumpInsnNode) instruction;
                    var label = jumpInsn.label;
                    if(i> instructions.indexOf(label)){
                        if(!opcodesCycles.containsKey(method.name)) {
                            opcodesCycles.put(method.name,new ArrayList<>());
                            var end = instructions.indexOf(label);
                            opcodesCycles.get(method.name).addAll(getOpcodes(instructions, i,end));
                        }
                        counter++;
                    }
                }
            }
        }
        return counter;
    }
    public void printClass(){
        var classNode = new ClassNode();
        classReader.accept(classNode, 0);
        for (MethodNode method : classNode.methods) {
            System.out.println("Method: " + method.name + method.desc);
            var instructions = method.instructions;
            for(var instruction : instructions){
                if(instruction.getOpcode()!=-1) System.out.println(instruction.getOpcode());
            }
        }
    }
    private ArrayList<Integer> getOpcodes(InsnList instructions, int end, int begin){
        var result = new ArrayList<Integer>();

        for(var i=begin;i<=end;i++) {
            if (instructions.get(i).getOpcode() != -1) {
                result.add(instructions.get(i).getOpcode());
            }
        }

        return result;
    }
    private ArrayList<Integer> getOpcodes(InsnList instructions, JumpInsnNode jump){
        var opcodes = new ArrayList<Integer>();
        for(var i = instructions.indexOf(jump);i>=0;i--) {
            var instruction = instructions.get(i);
            if (instruction.getOpcode() != -1) {
                opcodes.add(instruction.getOpcode());
                if (instruction instanceof JumpInsnNode) {
                    if (instructions.indexOf(((JumpInsnNode) instruction).label) > instructions.indexOf(jump)) break;
                }
            }
        }
        var result = new ArrayList<Integer>();

        for(var i=0;i<opcodes.size();i++){
            result.add(opcodes.get(opcodes.size()-1-i));
        }

        return result;
    }
    public void analyze(){
        classReader.accept(visitor, 0);
    }
    public ClassReader getClassReader(){
        return classReader;
    }
    public int getCycleCount(){
        return useApiTreeToCalculateCountOfCycles();
    }
    public HashMap<String, ArrayList<Integer>> getOpcodesCycle(){
        return opcodesCycles;
    }
    public int getConditionalTransition(){
        return visitor.getConditionalTransition();
    }
    public int getDeclaringVariables(){
        return visitor.getFieldCount() + visitor.getLocalVariableCount();
    }
    public int getMethodCount(){
        return visitor.getMethodCount();
    }
    public int calculateOpcodesCycle(){
        var result = 0;
        for(var method : opcodesCycles.keySet()){
            for(var i=0;i<opcodesCycles.get(method).size();i++){
                result++;
            }
        }
        return result;
    }

}
