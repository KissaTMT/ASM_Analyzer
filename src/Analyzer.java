import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Analyzer {
    private String path;
    private ClassReader classReader;
    private MorningVisitor visitor;
    public Analyzer(String path) throws IOException {
        this.path = path;
        classReader = new ClassReader(new FileInputStream(path));
        visitor = new MorningVisitor(Opcodes.ASM9);
    }
    public void analyze(){
        classReader.accept(visitor, 0);
    }
    public ClassReader getClassReader(){
        return classReader;
    }
    public int getOpcodeCount(){
        return visitor.getOpcodeCount();
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
    public HashMap<String, ArrayList<Integer>> getOpcodes(){
        return visitor.getOpcodes();
    }

}
