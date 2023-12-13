import org.objectweb.asm.*;
public class MorningVisitor extends ClassVisitor {
    private int fieldCount;
    private int localVariableCount;
    private int conditionalTransition;

    public MorningVisitor(int api) {
        super(api);
    }
    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        fieldCount++;
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(super.api,mv) {
            @Override
            public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                localVariableCount++;
                super.visitLocalVariable(name, descriptor, signature, start, end, index);
            }
            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if (opcode >= Opcodes.IFEQ && opcode <= Opcodes.IF_ACMPNE || opcode == Opcodes.IFNONNULL || opcode == Opcodes.IFNULL) {
                    conditionalTransition++;
                }
                super.visitJumpInsn(opcode, label);
            }
        };
    }

    public int getFieldCount(){
        return fieldCount;
    }
    public int getLocalVariableCount(){
        return localVariableCount;
    }
    public int getConditionalTransition(){
        return conditionalTransition;
    }
}
