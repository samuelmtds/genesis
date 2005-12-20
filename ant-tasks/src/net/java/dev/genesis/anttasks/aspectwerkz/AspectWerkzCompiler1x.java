package net.java.dev.genesis.anttasks.aspectwerkz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.codehaus.aspectwerkz.compiler.CompileException;
import org.codehaus.aspectwerkz.hook.ClassPreProcessor;

public class AspectWerkzCompiler1x implements AspectWerkzCompiler {
   private final Task task;
   private final Map attributes;

   public AspectWerkzCompiler1x(final Task task) {
      this.task = task;
      this.attributes = new HashMap();
   }

   public void setVerbose(boolean verbose) {
      System.setProperty(SimplifiedAspectWerkzCTask.AW_TRANSFORM_VERBOSE, verbose ? "true"
            : "false");
   }

   public void setAttribute(String name, Object value) {
      attributes.put(name, value);
   }

   public void compileClass(File destDir, File file, File destFile,
         String packageName, ClassLoader loader, ClassPreProcessor preProcessor)
         throws CompileException {

      InputStream in = null;
      OutputStream os = null;
      int fileSize = (int) file.length();
      // Dont work for files larger than 2 Gb :-p
      try {
         in = new FileInputStream(file);
         byte[] buffer = new byte[fileSize];
         in.read(buffer);

         String className = packageName + '.'
               + file.getName().substring(0, file.getName().length() - 6);

         // transform
         byte[] transformed = preProcessor
               .preProcess(className, buffer, loader);

         // write new file
         os = new FileOutputStream(destFile);
         os.write(transformed);
      } catch (Exception e) {
         throw new BuildException("weaving " + file.getAbsolutePath()
               + " failed", e, task.getLocation());
      } finally {
         try {
            in.close();
         } catch (Throwable e) {
            task.log(e.getMessage(), Project.MSG_ERR);
         }

         try {
            os.close();
         } catch (Throwable e) {
            task.log(e.getMessage(), Project.MSG_ERR);
         }
      }
   }
}
