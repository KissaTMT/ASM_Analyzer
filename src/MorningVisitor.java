import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MorningVisitor extends ClassVisitor {
    private int fieldCount;
    private int localVariableCount;
    private int methodCount;
    private int opcodeCount;
    private int conditionalTransition;
    private HashMap<String, ArrayList<Integer>> opcodes;

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        fieldCount++;
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        methodCount++;
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(super.api,mv) {
            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                conditionalTransition++;
                super.visitTryCatchBlock(start, end, handler, type);
            }
            @Override
            public void visitInsn(int opcode) {
                opcodeCount++;
                if(opcodes.containsKey(name)){
                    opcodes.get(name).add(opcode);
                }
                else opcodes.put(name,new ArrayList<>(Arrays.asList(opcode)));
            }
            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if(opcode >= Opcodes.IFEQ && opcode<= Opcodes.IF_ACMPNE || opcode == Opcodes.IFNONNULL || opcode == Opcodes.IFNULL){
                    conditionalTransition++;
                }
                super.visitJumpInsn(opcode, label);
            }

            @Override
            public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                localVariableCount++;
                super.visitLocalVariable(name, descriptor, signature, start, end, index);
            }
        };
    }

    public MorningVisitor(int api) {
        super(api);
        opcodes = new HashMap<>();
    }
    public int getFieldCount(){
        return fieldCount;
    }
    public int getLocalVariableCount(){
        return localVariableCount;
    }
    public int getMethodCount(){
        return methodCount;
    }
    public int getConditionalTransition(){
        return conditionalTransition;
    }
    public int getOpcodeCount(){
        return opcodeCount;
    }

    public HashMap<String, ArrayList<Integer>> getOpcodes() {
        return opcodes;
    }
}
