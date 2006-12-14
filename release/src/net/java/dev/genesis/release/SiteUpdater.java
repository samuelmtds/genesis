package net.java.dev.genesis.release;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class SiteUpdater {
   private static final String INDEX_PAGE = "index.html";
   private static final String MAVEN_SITE = "maven-site";

   public static void main(String[] args) throws Exception {
      final File www = new File(args[1], "../www").getCanonicalFile();

      if (!www.exists() || !www.isDirectory()) {
         throw new IllegalStateException(www.getCanonicalPath() + " folder " +
               "does not exist");
      }

      final File baseRelease = new File(www, args[0]);

      if (!baseRelease.exists() || !baseRelease.isDirectory()) {
         throw new IllegalStateException(baseRelease.getCanonicalPath() + 
               " folder does not exist");
      }

      final File baseReleaseSite = new File(baseRelease, MAVEN_SITE);

      if (!baseReleaseSite.exists() || !baseReleaseSite.isDirectory()) {
         throw new IllegalStateException(baseReleaseSite.getCanonicalPath() + 
               " folder does not exist");
      }

      final File[] wwwChildren = www.listFiles(new FileFilter() {
         public boolean accept(final File file) {
            try {
               return file.isDirectory() && !file.getCanonicalPath().equals(
                     baseRelease.getCanonicalPath()) && !file.getName().equals(
                     "CVS");
            } catch (IOException ioe) {
               throw new RuntimeException(ioe);
            }
         }
      });

      Arrays.sort(wwwChildren);

      for (int i = 0; i < wwwChildren.length; i++) {
         process(wwwChildren[i].getCanonicalFile(), baseReleaseSite, www);
      }
   }

   private static void process(File oldRelease, File baseReleaseSite, File www) 
         throws Exception {
      System.out.println("Processing " + oldRelease);

      final File oldReleaseSite = new File(oldRelease, MAVEN_SITE);

      if (!oldReleaseSite.exists() || !oldReleaseSite.isDirectory()) {
         System.out.println(oldReleaseSite.getCanonicalPath() + 
               " folder does not exist");
         return;
      }

      processFiles(oldReleaseSite, baseReleaseSite, baseReleaseSite, www);
   }

   private static void processFiles(final File oldReleaseDir, 
         File baseReleaseDir, File baseReleaseSite, File www) 
         throws IOException {
      System.out.println("Processing " + oldReleaseDir);

      final File[] oldFiles = oldReleaseDir.listFiles();
      
      if (oldFiles == null) {
         System.out.println(oldReleaseDir + " is empty");
         return;
      }
      
      Arrays.sort(oldFiles);

      for (int i = 0; i < oldFiles.length; i++) {
         if (!oldFiles[i].isDirectory()) {
            String name = oldFiles[i].getName();
            int indexOfDot = name.lastIndexOf('.');

            boolean delete = true;

            if (indexOfDot != -1 && indexOfDot != name.length() - 1) {
               String extension = name.substring(indexOfDot + 1);

               if (extension.toLowerCase().startsWith("htm")) {
                  delete = false;
               }
            }

            if (delete) {
               System.out.println("Deleting " + oldFiles[i]);
               oldFiles[i].delete();
            } else {
               rewrite(oldFiles[i], baseReleaseDir, baseReleaseSite, www);
            }

            continue;
         }

         if (oldFiles[i].getName().equals("CVS")) {
            continue;
         }

         processFiles(oldFiles[i], new File(baseReleaseDir, 
               oldFiles[i].getName()), baseReleaseSite, www);
      }
   }

   private static void rewrite(File file, File baseReleaseDir, 
         File baseReleaseSite, File www) throws IOException {
      String forwardTo = getForwardTo(www, file, getFullForwardTo(file, 
            baseReleaseDir));

      System.out.println("Rewriting " + file + " to " + forwardTo);

      BufferedWriter bw = new BufferedWriter(new FileWriter(file), 4096);

      try {
         bw.write("<html>\n");
         bw.write("<head>\n");
         bw.write("<title>Redirecting to ");
         bw.write(forwardTo);
         bw.write("</title>\n");
         bw.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n");
         bw.write("<meta http-equiv=\"refresh\" content=\"0;URL=");
         bw.write(forwardTo);
         bw.write("\">\n");
         bw.write("</head>\n");
         bw.write("<body onLoad=\"javascript:fwd();\">\n");
         bw.write("<script language=\"JavaScript\">\n");
         bw.write("<!--\n");
         bw.write("function fwd() {\n");
         bw.write("document.location.href='");
         bw.write(forwardTo);
         bw.write("';\n");
         bw.write("}\n");
         bw.write("// -->\n");
         bw.write("</script>\n");
         bw.write("<center>Redirecting to docs for the latest release; click <a " +
               "href=\"javascript:fwd();\">here</a></font> if it doesn't work" +
               "</center>\n");
         bw.write("</body>\n");
         bw.write("</html>");
      } finally {
         bw.close();
      }
   }

   private static File getFullForwardTo(final File file, File baseReleaseDir) {
      File fullForwardTo = null;

      if (baseReleaseDir.exists()) {
         fullForwardTo = new File(baseReleaseDir, file.getName());

         if (!fullForwardTo.exists()) {
            fullForwardTo = new File(baseReleaseDir, INDEX_PAGE);

            if (!fullForwardTo.exists()) {
               fullForwardTo = null;
            }
         }
      }

      if (fullForwardTo == null) {
         do {
            baseReleaseDir = baseReleaseDir.getParentFile();
         } while (!(fullForwardTo = new File(baseReleaseDir, INDEX_PAGE)).exists());
      }
      return fullForwardTo;
   }

   private static String getForwardTo(File www, File file, File fullForwardTo) 
         throws IOException {
      StringBuffer forwardTo = new StringBuffer();

      String wwwPath = www.getCanonicalPath();
      String filePath = file.getParentFile().getCanonicalPath();
      
      int depth = 0;
      int index = wwwPath.length();

      while ((index = filePath.indexOf(File.separatorChar, index) + 1) != 0) {
         depth++;
      }

      for (int i = 0; i < depth; i++) {
         forwardTo.append("../");
      }

      return forwardTo.append(fullForwardTo.getCanonicalPath().substring(
            wwwPath.length() + 1).replace(File.separatorChar, '/')).toString();
   }
}