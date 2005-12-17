package net.java.dev.genesis.anttasks.aspectwerkz;

import java.io.File;

import org.codehaus.aspectwerkz.compiler.CompileException;
import org.codehaus.aspectwerkz.hook.ClassPreProcessor;

public interface AspectWerkzCompiler {
   public void compileClass(File destDir, File file, File destFile,
         String packageName, ClassLoader loader, ClassPreProcessor preProcessor)
         throws CompileException;
   public void setAttribute(String name, Object value);
   public void setVerbose(boolean verbose);
}
