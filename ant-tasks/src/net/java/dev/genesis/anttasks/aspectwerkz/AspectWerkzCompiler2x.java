package net.java.dev.genesis.anttasks.aspectwerkz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.codehaus.aspectwerkz.aspect.AdviceInfo;
import org.codehaus.aspectwerkz.cflow.CflowBinding;
import org.codehaus.aspectwerkz.cflow.CflowCompiler;
import org.codehaus.aspectwerkz.compiler.CompileException;
import org.codehaus.aspectwerkz.hook.ClassPreProcessor;
import org.codehaus.aspectwerkz.joinpoint.management.AdviceInfoContainer;
import org.codehaus.aspectwerkz.joinpoint.management.JoinPointManager;
import org.codehaus.aspectwerkz.transform.AspectWerkzPreProcessor;
import org.codehaus.aspectwerkz.transform.inlining.EmittedJoinPoint;
import org.codehaus.aspectwerkz.util.ContextClassLoader;

public class AspectWerkzCompiler2x implements AspectWerkzCompiler {
   private final Task task;
   private boolean verbose;
   private final Map attributes;

   public AspectWerkzCompiler2x(final Task task) {
      this.task = task;
      this.attributes = new HashMap();
   }

   public void setAttribute(String name, Object value) {
      attributes.put(name, value);
   }

   protected boolean isGenJP() {
      return Boolean.TRUE.equals(attributes.get("genjp"));
   }

   public void setVerbose(boolean verbose) {
      System.setProperty(SimplifiedAspectWerkzCTask.AW_TRANSFORM_DETAILS,
            "true");
      this.verbose = verbose;
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
         AspectWerkzPreProcessor.Output out = preProcess(preProcessor,
               className, buffer, loader);

         // write new file
         os = new FileOutputStream(destFile);
         os.write(out.bytecode);

         // if AW and genjp
         if (out.emittedJoinPoints != null && out.emittedJoinPoints.length > 0
               && isGenJP()) {
            generateJoinPoints(loader, out.emittedJoinPoints, destDir);
         }
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

   private void generateJoinPoints(ClassLoader loader,
         EmittedJoinPoint[] emittedJoinPoints, File destDir) throws IOException {
      FileOutputStream out = null;

      try {
         for (int i = 0; i < emittedJoinPoints.length; i++) {
            EmittedJoinPoint emittedJoinPoint = emittedJoinPoints[i];
            File jpFile = new File(destDir, emittedJoinPoint
                  .getJoinPointClassName()
                  + ".class");

            if (verbose) {
               task.log(" [genjp] " + jpFile.getAbsolutePath());
            }

            out = new FileOutputStream(jpFile);
            JoinPointManager.CompiledJoinPoint compiledJp = compileJoinPoint(
                  emittedJoinPoint, loader);
            out.write(compiledJp.bytecode);

            // handle cflow if any
            CflowCompiler.CompiledCflowAspect[] compiledCflowAspects = compileCflows(compiledJp);
            if (compiledCflowAspects.length > 0) {
               handleCflow(compiledCflowAspects, destDir);
            }
         }
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (IOException e) {
               task.log(e.getMessage(), Project.MSG_ERR);
            }
         }
      }
   }

   private void handleCflow(
         CflowCompiler.CompiledCflowAspect[] compiledCflowAspects, File destDir)
         throws IOException {
      FileOutputStream out = null;

      try {
         for (int j = 0; j < compiledCflowAspects.length; j++) {
            CflowCompiler.CompiledCflowAspect compiledCflowAspect = compiledCflowAspects[j];
            File cflowFile = new File(destDir, compiledCflowAspect.className
                  .replace('/', File.separatorChar)
                  + ".class");
            (new File(cflowFile.getParent())).mkdirs();

            if (verbose) {
               task.log(" [genjp] (cflow) " + cflowFile.getAbsolutePath());
            }
            out = new FileOutputStream(cflowFile);
            out.write(compiledCflowAspect.bytecode);
         }
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (IOException e) {
               task.log(e.getMessage(), Project.MSG_ERR);
            }
         }
      }
   }

   private AspectWerkzPreProcessor.Output preProcess(
         ClassPreProcessor preProcessor, String className, byte[] bytecode,
         ClassLoader compilationLoader) {
      if (preProcessor instanceof AspectWerkzPreProcessor) {
         return ((AspectWerkzPreProcessor) preProcessor).preProcessWithOutput(
               className, bytecode, compilationLoader);
      }

      byte[] newBytes = preProcessor.preProcess(className, bytecode,
            compilationLoader);
      AspectWerkzPreProcessor.Output out = new AspectWerkzPreProcessor.Output();
      out.bytecode = newBytes;
      return out;
   }

   private JoinPointManager.CompiledJoinPoint compileJoinPoint(
         EmittedJoinPoint emittedJoinPoint, ClassLoader loader)
         throws IOException {
      try {
         Class callerClass = ContextClassLoader.forName(emittedJoinPoint
               .getCallerClassName().replace('/', '.'));
         Class calleeClass = ContextClassLoader.forName(emittedJoinPoint
               .getCalleeClassName().replace('/', '.'));
         JoinPointManager.CompiledJoinPoint jp = JoinPointManager
               .compileJoinPoint(emittedJoinPoint.getJoinPointType(),
                     callerClass, emittedJoinPoint.getCallerMethodName(),
                     emittedJoinPoint.getCallerMethodDesc(), emittedJoinPoint
                           .getCallerMethodModifiers(), emittedJoinPoint
                           .getCalleeClassName(), emittedJoinPoint
                           .getCalleeMemberName(), emittedJoinPoint
                           .getCalleeMemberDesc(), emittedJoinPoint
                           .getCalleeMemberModifiers(), emittedJoinPoint
                           .getJoinPointHash(), emittedJoinPoint
                           .getJoinPointClassName(), calleeClass, loader);
         return jp;
      } catch (ClassNotFoundException e) {
         throw new IOException("Could not compile joinpoint : " + e.toString());
      }
   }

   private CflowCompiler.CompiledCflowAspect[] compileCflows(
         JoinPointManager.CompiledJoinPoint jp) {
      List allCflowBindings = new ArrayList();
      AdviceInfoContainer adviceInfoContainer = jp.compilationInfo
            .getInitialModel().getAdviceInfoContainer();

      AdviceInfo[] advices = adviceInfoContainer.getAllAdviceInfos();
      for (int i = 0; i < advices.length; i++) {
         AdviceInfo adviceInfo = advices[i];
         List cflowBindings = CflowBinding
               .getCflowBindingsForCflowOf(adviceInfo.getExpressionInfo());
         allCflowBindings.addAll(cflowBindings);
      }

      List compiledCflows = new ArrayList();
      for (Iterator iterator = allCflowBindings.iterator(); iterator.hasNext();) {
         CflowBinding cflowBinding = (CflowBinding) iterator.next();
         compiledCflows.add(CflowCompiler.compileCflowAspect(cflowBinding
               .getCflowID()));
      }

      return (CflowCompiler.CompiledCflowAspect[]) compiledCflows
            .toArray(new CflowCompiler.CompiledCflowAspect[0]);
   }
}
