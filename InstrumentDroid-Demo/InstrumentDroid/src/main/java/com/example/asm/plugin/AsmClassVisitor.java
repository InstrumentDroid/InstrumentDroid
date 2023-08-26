package com.example.asm.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

public class AsmClassVisitor extends ClassVisitor implements Opcodes {

    private String mClassName;
    private ArrayList<String> xmlPath = new ArrayList<String>();

    public AsmClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }
    public AsmClassVisitor(ClassVisitor cv, ArrayList<String> xmlPath) {
        super(Opcodes.ASM5, cv);
        this.xmlPath = xmlPath;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        for(String manifestFilePath: xmlPath) {
            try {
                File manifestFile = new File(manifestFilePath);
                List<String> activityNames = ManifestParser.getActivityNames(manifestFile);
                List<String> mainActivityNames = ManifestParser.getMainActivityName(manifestFile);
                String packageName = ManifestParser.getPackageName(manifestFile);
                /**
                 * flag = 0 this class is not an Activity
                 * flag = 1 this class is an Activity class but not Main Activity
                 * flag = 2 this class is Main Activity
                 **/
                int flag = 0;
                for(String mainActivity : mainActivityNames) {
                    if (mClassName.equals(mainActivity)) {
                        flag = 2;
                    }
                }
                for (String str : activityNames) {
                    if (mClassName.equals(str) && flag != 2) {
                        flag = 1;
                    }
                }
//                System.out.println(mClassName + "/" + name + ": flag = " + flag + ", package: " + packageName);
                return new AsmMethodVisitor(mv, mClassName, name, desc, flag, signature, packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new AsmMethodVisitor(mv, mClassName, name, desc, 0, signature, "error");
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

}
