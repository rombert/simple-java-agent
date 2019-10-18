package nu.muntea.sja;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtMethod;
import javassist.bytecode.Descriptor;

public class Agent {
    private static final String CLASS_TO_TRANSFORM = Descriptor.toJvmName("nu.muntea.sja.App");
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                
                if ( !CLASS_TO_TRANSFORM.equals(className) )
                    return null;
                
                try {
                    ClassPool classPool = ClassPool.getDefault();
                    CtMethod method = classPool.getMethod(Descriptor.toJavaName(className), "main");
                    method.insertAfter("System.out.println(\"... hello yourself!...\");");
                    byte[] newClass = method.getDeclaringClass().toBytecode();
                    method.getDeclaringClass().detach();
                    
                    return newClass;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }
        });
    }
}
