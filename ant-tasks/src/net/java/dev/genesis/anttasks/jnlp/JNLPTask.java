/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.anttasks.jnlp;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.anttasks.jnlp.JNLPTask.Resources.Jar;
import net.java.dev.genesis.anttasks.jnlp.JNLPTask.Resources.NativeLib;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/**
 * Creates a Java Web Start file.
 * 
 * Based on orangevolts JNLPTask
 */
public class JNLPTask extends Task implements JNLPTaskMember {
    private static final FileUtils fu = FileUtils.newFileUtils();

    public class AppletDesc extends XDesc {
        public class Param implements JNLPTaskMember {
            private String name = null;

            private String value = null;

            public void setName(String string) {
                name = string;
            }

            public void setValue(String string) {
                value = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<param");

                if (name == null)
                    throw new RuntimeException(
                            "<param> requires attribute name");
                else
                    sb.append(" name=\"").append(name).append('"');

                if (value == null)
                    throw new RuntimeException(
                            "<param> requires attribute value");
                else
                    sb.append(" value=\"").append(value).append('"');

                sb.append("/>").append(NEW_LINE);
            }
        }

        String documentBase = null, mainClass = null, name = null,
                width = null, height = null;

        ArrayList params = new ArrayList();;

        public Param createArgument() {
            Param param = new Param();
            params.add(param);
            return param;
        }

        public void setDocumentBase(String string) {
            documentBase = string;
        }

        public void setHeight(String string) {
            height = string;
        }

        public void setMainClass(String string) {
            mainClass = string;
        }

        public void setName(String string) {
            name = string;
        }

        public void setWidth(String string) {
            width = string;
        }

        public void toString(StringBuffer sb, int depth) {
            appendTabs(sb, depth);
            sb.append("<applet-desc");

            if (name == null)
                throw new RuntimeException(
                        "<applet_desc> requires attribute name");
            else
                sb.append(" name=\"").append(name).append('"');

            if (mainClass == null)
                throw new RuntimeException(
                        "<applet_desc> requires attribute main_class");
            else
                sb.append(" mainClass=\"").append(mainClass).append('"');

            if (width == null)
                throw new RuntimeException(
                        "<applet_desc> requires attribute width");
            else
                sb.append(" width=\"").append(width).append('"');

            if (height == null)
                throw new RuntimeException(
                        "<applet_desc> requires attribute height");
            else
                sb.append(" height=\"").append(height).append('"');

            if (documentBase != null)
                sb.append(" documentbase=\"").append(documentBase).append('"');

            if (params.size() == 0)
                sb.append("\">").append(NEW_LINE);
            else
                sb.append('>').append(NEW_LINE);

            for (int i = 0; i < params.size(); i++) {
                Param param = (Param) params.get(i);
                param.toString(sb, depth + 1);
            }

            if (params.size() > 0) {
                appendTabs(sb, depth);
                sb.append("</applet-desc>").append(NEW_LINE);
            }
        }

    }

    public class ApplicationDesc extends XDesc {
        public class Argument implements JNLPTaskMember {
            private String text = null;

            public void addText(String s) {
                text = getProject().replaceProperties(s);
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<argument>");

                if (text == null)
                    throw new RuntimeException(
                            "<argument> requires #pcdata content");
                else
                    sb.append(text);

                sb.append("</argument>").append(NEW_LINE);
            }
        }

        private List arguments = new ArrayList();

        private String mainClass = null;

        public Argument createArgument() {
            Argument argument = new Argument();
            arguments.add(argument);
            return argument;
        }

        public void setMain_Class(String string) {
            mainClass = string;
        }

        public void setMainClass(String mainClass) {
            this.mainClass = mainClass;
        }

        public void toString(StringBuffer sb, int depth) {
            appendTabs(sb, depth);
            sb.append("<application-desc");

            if (mainClass != null)
                sb.append(" main-class=\"").append(mainClass).append('"');

            if (arguments.size() == 0)
                sb.append("/>").append(NEW_LINE);
            else
                sb.append('>').append(NEW_LINE);

            for (int i = 0; i < arguments.size(); i++) {
                Argument arg = (Argument) arguments.get(i);
                arg.toString(sb, depth + 1);
            }

            if (arguments.size() > 0) {
                appendTabs(sb, depth);
                sb.append("</application-desc>").append(NEW_LINE);
            }
        }
    }

    public class ComponentDesc extends XDesc {

        public void toString(StringBuffer sb, int depth) {
            appendTabs(sb, depth);
            sb.append("<component-desc/>").append(NEW_LINE);
        }
    };

    public class Information implements JNLPTaskMember {
        public class Description implements JNLPTaskMember {
            private String kind = null;

            private String text = "";

            public void addText(String s) {
                text = getProject().replaceProperties(s);
            }

            public void setKind(String string) {
                kind = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<description");
                if (kind != null)
                    sb.append(" kind=\"").append(kind).append('\"');
                sb.append('>');
                sb.append(text);
                sb.append("</description>").append(NEW_LINE);
            }

        }

        public class Homepage implements JNLPTaskMember {
            private String href = null;

            public void setHref(String string) {
                href = string;
            }

            public void toString(StringBuffer sb, int depth) {
                if (href != null) {
                    appendTabs(sb, depth);
                    sb.append("<homepage href=\"").append(href).append("\"/>")
                            .append(NEW_LINE);
                }
            }
        }

        public class Icon implements JNLPTaskMember {
            private String depth = null;

            private String height = null;

            private String href = null;

            private String kind = null;

            private String size = null;

            private String version = null;

            private String width = null;

            public void setDepth(String depth) {
                this.depth = depth;
            }

            public void setHeight(String string) {
                height = string;
            }

            public void setHref(String s) {
                href = s;
            }

            public void setKind(String s) {
                kind = s;
            }

            public void setSize(String string) {
                size = string;
            }

            public void setVersion(String string) {
                version = string;
            }

            public void setWidth(String string) {
                width = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<icon");
                if (href != null)
                    sb.append(" href=\"").append(href).append('\"');

                if (version != null)
                    sb.append(" version=\"").append(version).append('\"');

                if (width != null)
                    sb.append(" width=\"").append(width).append('\"');

                if (height != null)
                    sb.append(" height=\"").append(height).append('\"');

                if (kind != null)
                    sb.append(" kind=\"").append(kind).append('\"');

                if (this.depth != null)
                    sb.append(" depth=\"").append(this.depth).append('\"');

                if (size != null)
                    sb.append(" size=\"").append(size).append('\"');

                sb.append("/>").append(NEW_LINE);
            }

        }

        public class Shortcut implements JNLPTaskMember {
            public class Desktop implements JNLPTaskMember {

                public void toString(StringBuffer sb, int depth) {
                    appendTabs(sb, depth);
                    sb.append("<desktop/>").append(NEW_LINE);
                }

            }

            public class Menu implements JNLPTaskMember {
                private String submenu = null;

                public void setSubmenu(String submenu) {
                    this.submenu = submenu;
                }

                public void toString(StringBuffer sb, int depth) {
                    appendTabs(sb, depth);
                    sb.append("<menu submenu=\"").append(submenu)
                            .append("\"/>").append(NEW_LINE);
                }
            }

            private Desktop desktop = null;

            private Menu menu = null;

            private String online = "true";

            public Desktop createDesktop() {
                desktop = new Desktop();
                return desktop;
            }

            public Menu createMenu() {
                menu = new Menu();
                return menu;
            }

            public void setOnline(String online) {
                if (online == null) {
                    online = "true";
                }
                this.online = online;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<shortcut");
                sb.append(" online=\"").append(online).append("\">").append(
                        NEW_LINE);

                if (desktop != null) {
                    desktop.toString(sb, depth + 1);
                }

                if (menu != null) {
                    menu.toString(sb, depth + 1);
                }

                appendTabs(sb, depth);
                sb.append("</shortcut>").append(NEW_LINE);

            }

        }

        public class Title implements JNLPTaskMember {
            private String title = null;

            public void addText(String s) {
                title = getProject().replaceProperties(s);
            }

            public void toString(StringBuffer sb, int depth) {
                if (title != null) {
                    appendTabs(sb, depth);
                    sb.append("<title>").append(title).append("</title>")
                            .append(NEW_LINE);
                }
            }

        }

        public class Vendor {
            private String vendor = null;

            public void addText(String s) {
                vendor = getProject().replaceProperties(s);
            }

            public void toString(StringBuffer sb, int depth) {
                if (vendor != null) {
                    appendTabs(sb, depth);
                    sb.append("<vendor>").append(vendor).append("</vendor>")
                            .append(NEW_LINE);
                }
            }
        }

        private List descriptions = new ArrayList();

        private Homepage homepage = new Homepage();

        private List icons = new ArrayList();

        private String locale = null;

        private Boolean offlineAllowed = null;

        private Shortcut shortcut = null;

        private Title title = new Title();

        private Vendor vendor = new Vendor();
        
        public Description createDescription() {
            Description description = new Description();
            descriptions.add(description);
            return description;
        }

        public Homepage createHomepage() {
            return homepage;
        }

        public Icon createIcon() {
            Icon icon = new Icon();
            icons.add(icon);

            return icon;
        }

        public Boolean createOffline_Allowed() {
            return (offlineAllowed = Boolean.TRUE);
        }

        public Shortcut createShortcut() {
            shortcut = new Shortcut();
            return shortcut;
        }

        public Title createTitle() {
            return title;
        }

        public Vendor createVendor() {
            return vendor;
        }

        public void setHomepage(Homepage homepage) {
            this.homepage = homepage;
        }

        public void setLocale(String string) {
            locale = string;
        }

        public void setOfflineAllowed(Boolean offlineAllowed) {
            this.offlineAllowed = offlineAllowed;
        }

        public void setShortcut(Shortcut shortcut) {
            this.shortcut = shortcut;
        }

        public void setTitle(Title title) {
            this.title = title;
        }

        public void setVendor(Vendor vendor) {
            this.vendor = vendor;
        }

        public void toString(StringBuffer sb, int depth) {
            appendTabs(sb, depth);
            sb.append("<information");
            if (locale != null)
                sb.append(" locale=\"").append(locale).append('\"');
            sb.append('>').append(NEW_LINE);

            title.toString(sb, depth + 1);
            vendor.toString(sb, depth + 1);
            homepage.toString(sb, depth + 1);

            for (int i = 0; i < descriptions.size(); i++) {
                Description description = (Description) descriptions.get(i);
                description.toString(sb, depth + 1);
            }

            for (int i = 0; i < icons.size(); i++) {
                Icon icon = (Icon) icons.get(i);
                icon.toString(sb, depth + 1);
            }

            if (offlineAllowed != null && offlineAllowed.booleanValue()) {
                appendTabs(sb, depth + 1);
                sb.append("<offline-allowed/>").append(NEW_LINE);
            }

            if (shortcut != null) {
                shortcut.toString(sb, depth + 1);
            }

            appendTabs(sb, depth);
            sb.append("</information>").append(NEW_LINE);
        }
    }

    public class InstallerDesc extends XDesc {
        private String mainClass = null;

        public void setMain_Class(String string) {
            mainClass = string;
        }

        public void setMainClass(String mainClass) {
            this.mainClass = mainClass;
        }

        public void toString(StringBuffer sb, int depth) {
            appendTabs(sb, depth);
            sb.append("<installer-desc");

            if (mainClass == null)
                throw new RuntimeException(
                        "<installer_desc> requires attribute main_class");
            else
                sb.append(" main-class=\"").append(mainClass).append('"');

            sb.append("</installer-desc>").append(NEW_LINE);
        }
    }

    //TODO add to documentation apl
    public static class InstallScript {
        private File name = null;

        public boolean isUnixShell() {
            return name.getName().endsWith(".sh");
        }

        public boolean isWinShell() {
            return (name.getName().endsWith(".bat") || name.getName().endsWith(
                    ".cmd"));
        }

        /**
         * Name of the install script.
         * this name is relative to @see tofile
         * @param name
         */
        public void setName(File name) {
            this.name = name;
        }
    }

    public class Resources implements JNLPTaskMember {
        public class Extension implements JNLPTaskMember {
            public class ExtDownload implements JNLPTaskMember {
                String extPart = null, download = null, part = null;

                public void setDownload(String string) {
                    download = string;
                }

                public void setExt_Part(String string) {
                    extPart = string;
                }

                public void setPart(String string) {
                    part = string;
                }

                public void toString(StringBuffer sb, int depth) {
                    appendTabs(sb, depth);
                    sb.append("<ext-download");

                    if (extPart == null)
                        throw new RuntimeException(
                                "<ext-download> requires attribute ext-part");
                    else
                        sb.append(" ext-part=\"").append(extPart).append('"');

                    if (download != null)
                        sb.append(" download=\"").append(download).append('"');

                    if (part != null)
                        sb.append(" part=\"").append(part).append('"');

                    sb.append("/>").append(NEW_LINE);
                }
            }

            private List extDownloads = new ArrayList();

            private String href = null;

            private String name = null;

            private String version = null;

            public ExtDownload createExtDownload() {
                ExtDownload extDownload = new ExtDownload();
                extDownloads.add(extDownloads);
                return extDownload;
            }

            public void setHref(String string) {
                href = string;
            }

            public void setName(String string) {
                name = string;
            }

            public void setVersion(String string) {
                version = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<extension");

                if (href == null)
                    throw new RuntimeException(
                            "<extension> requires attribute href");
                else
                    sb.append(" href=\"").append(href).append('"');

                if (version != null)
                    sb.append(" version=\"").append(version).append('"');

                if (name != null)
                    sb.append(" name=\"").append(name).append('"');

                if (extDownloads.size() > 0)
                    sb.append('>').append(NEW_LINE);
                else
                    sb.append("/>").append(NEW_LINE);

                for (int i = 0; i < extDownloads.size(); i++) {
                    ExtDownload extDownload = (ExtDownload) extDownloads.get(i);
                    extDownload.toString(sb, depth + 1);
                }

                if (extDownloads.size() > 0) {
                    appendTabs(sb, depth);
                    sb.append("</extension>").append(NEW_LINE);
                }
            }
        }

        public class FileResource {
            protected String arch = null;

            protected String download = null;

            protected final String[] FILE_EXTENTIONS = { ".jar", ".so", ".dll" };

            protected List filesets = new ArrayList();

            protected String href = null;

            protected String locale = null;

            protected String os = null;

            protected String part = null;

            protected String size = null;

            protected String version = null;

            /**
             * Add fileset to this jar.
             * The attributes from this jar will be templates to the files in the fielset.
             * filename and href must be null.
             * 
             * @see filename
             * @see href
             * 
             * @param fileset
             */
            public void addFileset(FileSet fileset) {
                filesets.add(fileset);
            }

            public int getFilesCount() {
                int result = href != null ? 1 : 0;
                for (int i = 0; i < filesets.size(); i++) {
                    FileSet fileset = (FileSet) filesets.get(i);
                    result += fileset.getDirectoryScanner(getProject())
                            .getIncludedFilesCount();

                }
                return result;
            }

            public void setArch(String arch) {
                if (this.arch != null) {
                    throw new BuildException(
                            "arch was previously set, aborting!");
                }
                this.arch = arch;
            }

            public void setDownload(String string) {
                download = string;
            }

            public void setFilename(String filename) {
                if ((jnlpType != null) && jnlpType.equals(JNLP_DOWNLOAD_SERVLET)) {
                    String[] nameparts = filename.split("__");
                    String lHref = nameparts[0];
                    String lLocale = null;
                    String lArch = null;
                    String lOs = null;

                    for (int i = 1; i < nameparts.length; i++) {
                        if (i == nameparts.length - 1) {

                            for (int j = 0; j < FILE_EXTENTIONS.length; j++) {
                                String fileExtention = FILE_EXTENTIONS[j];
                                if (nameparts[i].endsWith(fileExtention)) {
                                    lHref = lHref + fileExtention;
                                    nameparts[i] = nameparts[i].substring(0,
                                            nameparts[i].length()
                                                    - fileExtention.length());
                                }
                            }
                        }
                        switch (nameparts[i].charAt(0)) {
                        case 'V': {
                            setVersion(nameparts[i].substring(1));
                            break;
                        }
                        case 'A': {
                            lArch = ((lArch != null) ? lArch + "," : "")
                                    + nameparts[i].substring(1);
                            break;
                        }
                        case 'O': {
                            lOs = ((lOs != null) ? lOs + "," : "")
                                    + nameparts[i].substring(1);
                            break;
                        }
                        case 'L': {
                            lLocale = ((lLocale != null) ? lLocale + "," : "")
                                    + nameparts[i].substring(1);
                            break;
                        }
                        }
                    }
                    if (lArch != null) {
                        setArch(lArch);
                    }
                    if (lLocale != null) {
                        setLocale(lLocale);
                    }
                    if (lOs != null) {
                        setOs(lOs);
                    }
                    setHref(lHref);
                } else {
                    setHref(filename);
                }
            }

            public void setHref(String href) {
                if (this.href != null) {
                    throw new BuildException(
                            "href was previously set, aborting!");
                }
                this.href = href;
            }

            public void setLocale(String locale) {
                if (this.locale != null) {
                    throw new BuildException(
                            "locale  was previously set, aborting!");
                }
                this.locale = locale;
            }

            public void setOs(String os) {
                if (this.os != null) {
                    throw new BuildException("os was previously set, aborting!");
                }
                this.os = os;
            }

            public void setPart(String string) {
                part = string;
            }

            public void setSize(String string) {
                size = string;
            }

            public void setVersion(String string) {
                if (this.version != null) {
                    throw new BuildException(
                            "version was previously set, aborting!");
                }
                version = string;
            }

            /**
             */
            protected void validate() {
                if (getFilesCount() == 0) {
                    throw new BuildException(
                            "requires href or filename if no fileset is specified");
                }
            }
        }

        public class J2SE implements JNLPTaskMember {
            private String href = null;

            private String initialHeapSize = null;

            private String maxHeapSize = null;

            private List resources = new ArrayList();

            private String version = null;

            public Resources createResources() {
                Resources _resources = new Resources();
                resources.add(_resources);
                return _resources;
            }

            public void setHref(String string) {
                href = string;
            }

            public void setInitial_Heap_Size(String string) {
                initialHeapSize = string;
            }

            public void setMax_Heap_Size(String string) {
                maxHeapSize = string;
            }

            public void setVersion(String string) {
                version = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<j2se");
                if (version == null)
                    throw new RuntimeException(
                            "<j2se> requires attribute version");
                else
                    sb.append(" version=\"").append(version).append('"');

                if (href != null)
                    sb.append(" href=\"").append(href).append('"');

                if (initialHeapSize != null)
                    sb.append(" initial-heap-size=\"").append(initialHeapSize)
                            .append('"');

                if (maxHeapSize != null)
                    sb.append(" max-heap-size=\"").append(maxHeapSize).append(
                            '"');

                if (this.resources.size() == 0)
                    sb.append("/>").append(NEW_LINE);
                else
                    sb.append('>').append(NEW_LINE);

                for (int i = 0; i < resources.size(); i++) {
                    JNLPTask.Resources resources = (JNLPTask.Resources) this.resources
                            .get(i);
                    resources.toString(sb, depth + 1);
                }

                if (this.resources.size() > 0) {
                    appendTabs(sb, depth);
                    sb.append("</j2se>").append(NEW_LINE);
                }
            }
        }

        public class Jar extends FileResource implements JNLPTaskMember {

            private Boolean main = null;

            private boolean isMain() {
                return (main == null) ? false : main.equals(Boolean.TRUE
                        .toString());
            }

            public void setMain(Boolean main) {
                this.main = main;
            }

            public void toString(StringBuffer sb, int depth) {
                validate();

                // Write single jar
                if (href != null) {
                    writeJar(sb, depth);
                }

                // Write jars from fileset
                Iterator iterator = filesets.iterator();
                while (iterator.hasNext()) {

                    FileSet fileSet = (FileSet) iterator.next();
                    DirectoryScanner directoryscanner = fileSet
                            .getDirectoryScanner(getProject());
                    String files[] = directoryscanner.getIncludedFiles();

                    for (int j = 0; j < files.length; j++) {
                        Jar tempJar = new Jar();
                        if (arch != null) {
                            tempJar.setArch(arch);
                        }
                        if (download != null) {
                            tempJar.setDownload(download);
                        }
                        if (locale != null) {
                            tempJar.setLocale(locale);
                        }
                        if (main != null) {
                            tempJar.setMain(main);
                        }
                        if (os != null) {
                            tempJar.setOs(os);
                        }
                        if (part != null) {
                            tempJar.setPart(part);
                        }
                        if (size != null) {
                            tempJar.setSize(size);
                        }
                        if (version != null) {
                            tempJar.setVersion(version);
                        }
                        if (href != null) {
                            tempJar.setHref(href);
                        }
                        // order is important!! href, version, os, arch and locale will be calculated from filename!!
                        tempJar.setFilename(files[j]);
                        tempJar.toString(sb, depth);
                    }
                }
            }

            /**
             */
            protected void validate() {
                try {
                    super.validate();
                } catch (BuildException ex) {
                    throw new BuildException("<jar>" + ex.getMessage());
                }

                if ((getFilesCount() > 1) && isMain()) {
                    throw new BuildException(
                            "<jar> with more than one jar-archive and main=\"true\" make no sense");
                }
            }

            private void writeJar(StringBuffer sb, int depth) {

                appendTabs(sb, depth);
                sb.append("<jar");
                if (href == null)
                    throw new RuntimeException("<jar> requires attribute href");
                else
                    sb.append(" href=\"").append(correctHRefPath(href)).append(
                            '"');

                if (version != null)
                    sb.append(" version=\"").append(version).append('"');

                if (os != null)
                    sb.append(" os=\"").append(os).append('"');

                if (arch != null)
                    sb.append(" arch=\"").append(arch).append('"');

                if (locale != null)
                    sb.append(" locale=\"").append(locale).append('"');

                if (main != null)
                    sb.append(" main=\"").append(main.toString()).append('"');

                if (download != null)
                    sb.append(" download=\"").append(download).append('"');

                if (size != null)
                    sb.append(" size=\"").append(size).append('"');

                if (part != null)
                    sb.append(" part=\"").append(part).append('"');

                sb.append("/>").append(NEW_LINE);
            }
        }

        public class NativeLib extends FileResource implements JNLPTaskMember {

            public void toString(StringBuffer sb, int depth) {
                validate();

                // Write single jar
                if (href != null) {
                    writeNativeLib(sb, depth);
                }

                // Write jars from fileset
                Iterator iterator = filesets.iterator();
                while (iterator.hasNext()) {

                    FileSet fileSet = (FileSet) iterator.next();
                    DirectoryScanner directoryscanner = fileSet
                            .getDirectoryScanner(getProject());
                    String files[] = directoryscanner.getIncludedFiles();

                    for (int j = 0; j < files.length; j++) {
                        NativeLib tempNativeLib = new NativeLib();
                        if (arch != null) {
                            tempNativeLib.setArch(arch);
                        }
                        if (download != null) {
                            tempNativeLib.setDownload(download);
                        }
                        if (locale != null) {
                            tempNativeLib.setLocale(locale);
                        }
                        if (os != null) {
                            tempNativeLib.setOs(os);
                        }
                        if (part != null) {
                            tempNativeLib.setPart(part);
                        }
                        if (size != null) {
                            tempNativeLib.setSize(size);
                        }
                        if (version != null) {
                            tempNativeLib.setVersion(version);
                        }
                        if (href != null) {
                            tempNativeLib.setHref(href);
                        }
                        // order is important!! href, version, os, arch and locale will be calculated from filename!!
                        tempNativeLib.setFilename(files[j]);
                        tempNativeLib.toString(sb, depth);
                    }
                }

            }

            public void writeNativeLib(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<nativelib");

                if (href == null)
                    throw new RuntimeException(
                            "<nativelib> requires attribute href");
                else
                    sb.append(" href=\"").append(correctHRefPath(href)).append(
                            '"');

                if (version != null)
                    sb.append(" version=\"").append(version).append('"');

                if (os != null)
                    sb.append(" os=\"").append(os).append('"');

                if (arch != null)
                    sb.append(" arch=\"").append(arch).append('"');

                if (locale != null)
                    sb.append(" locale=\"").append(locale).append('"');

                if (download != null)
                    sb.append(" download=\"").append(download).append('"');

                if (size != null)
                    sb.append(" size=\"").append(size).append('"');

                if (part != null)
                    sb.append(" part=\"").append(part).append('"');

                sb.append("/>").append(NEW_LINE);
            }
        }

        public class Package implements JNLPTaskMember {
            private String name = null;

            private String part = null;

            private String recursive = null;

            public void setName(String string) {
                name = string;
            }

            public void setPart(String string) {
                part = string;
            }

            public void setRecursive(String string) {
                recursive = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<package");

                if (name == null)
                    throw new RuntimeException(
                            "<package> requires attribute name");
                else
                    sb.append(" name=\"").append(name).append('"');

                if (part == null)
                    throw new RuntimeException(
                            "<package> requires attribute part");
                else
                    sb.append(" part=\"").append(part).append('"');

                if (recursive != null)
                    sb.append(" recursive=\"").append(recursive).append('"');

                sb.append("/>").append(NEW_LINE);
            }
        }

        public class Property implements JNLPTaskMember {
            private String name = null;

            private String value = null;

            public void setName(String string) {
                name = string;
            }

            public void setValue(String string) {
                value = string;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append("<property");

                if (name == null)
                    throw new RuntimeException(
                            "<property> requires attribute name");
                else
                    sb.append(" name=\"").append(name).append('"');

                if (value == null)
                    throw new RuntimeException(
                            "<property> requires attribute value");
                else
                    sb.append(" value=\"").append(value).append('"');

                sb.append("/>").append(NEW_LINE);
            }
        }

        private String arch = null;

        private List extensions = new ArrayList();

        private List filesets = new ArrayList();

        private List j2ses = new ArrayList();

        private List jars = new ArrayList();

        private String locale = null;

        private List nativeLibs = new ArrayList();

        private String os = null;

        private List packages = new ArrayList();

        private List properties = new ArrayList();

        public void addFileset(FileSet fileset) {
            filesets.add(fileset);
        }

        public Extension createExtension() {
            Extension extension = new Extension();
            extensions.add(extension);
            return extension;
        };

        public J2SE createJ2SE() {
            J2SE j2se = new J2SE();
            j2ses.add(j2se);
            return j2se;
        }

        public Jar createJar() {
            Jar jar = new Jar();
            jars.add(jar);
            return jar;
        };

        public NativeLib createNativeLib() {
            NativeLib nativeLib = new NativeLib();
            nativeLibs.add(nativeLib);
            return nativeLib;
        };

        public Package createPackage() {
            Package _package = new Package();
            packages.add(_package);
            return _package;
        };

        public Property createProperty() {
            Property property = new Property();
            properties.add(property);
            return property;
        }

        /**
         * Expand the fileset to jars and nativelibs
         * 
         * if the extention is .jar it will be a jar, otherwise it will be a native lib (i.e. *.dll or *.so)
         *
         */
        private void expandFileset() {
            for (int i = 0; i < filesets.size(); i++) {
                FileSet fileSet = (FileSet) filesets.get(i);
                DirectoryScanner directoryscanner = fileSet
                        .getDirectoryScanner(getProject());
                String files[] = directoryscanner.getIncludedFiles();

                for (int j = 0; j < files.length; j++) {
                    String string = files[j];
                    if (string.endsWith(".jar")) {
                        Jar jar = createJar();
                        jar.setFilename(string);
                    } else {
                        NativeLib nativeLib = createNativeLib();
                        nativeLib.setFilename(string);
                    }
                }
            }
        }

        public void setArch(String string) {
            arch = string;
        }

        public void setLocale(String string) {
            locale = string;
        }

        public void setOs(String string) {
            os = string;
        }

        public void toString(StringBuffer sb, int depth) {
            // Houskeeping
            expandFileset();

            appendTabs(sb, depth);
            sb.append("<resources");
            if (os != null)
                sb.append(" os=\"").append(os).append('"');
            if (arch != null)
                sb.append(" arch=\"").append(arch).append('"');
            if (locale != null)
                sb.append(" locale=\"").append(locale).append('"');
            sb.append('>').append(NEW_LINE);

            for (int i = 0; i < j2ses.size(); i++) {
                J2SE j2se = (J2SE) j2ses.get(i);
                j2se.toString(sb, depth + 1);
            }

            for (int i = 0; i < jars.size(); i++) {
                Jar jar = (Jar) jars.get(i);
                jar.toString(sb, depth + 1);
            }

            for (int i = 0; i < nativeLibs.size(); i++) {
                NativeLib nativeLib = (NativeLib) nativeLibs.get(i);
                nativeLib.toString(sb, depth + 1);
            }

            for (int i = 0; i < extensions.size(); i++) {
                Extension extension = (Extension) extensions.get(i);
                extension.toString(sb, depth + 1);
            }

            for (int i = 0; i < properties.size(); i++) {
                Property property = (Property) properties.get(i);
                property.toString(sb, depth + 1);
            }

            for (int i = 0; i < packages.size(); i++) {
                Package _package = (Package) packages.get(i);
                _package.toString(sb, depth + 1);
            }

            appendTabs(sb, depth);
            sb.append("</resources>").append(NEW_LINE);
        }
    }

    public class NativeResources implements JNLPTaskMember {
      private List files = new ArrayList();
      private List baseFiles = new ArrayList();
      private Map osMap = new HashMap();
      private Map archMap = new HashMap();
      private String download;

      public void setDownload(String download) {
         this.download = download;
      }

      public void addFileset(FileSet fileset) {
         files.add(fileset);
      }
      
      protected void initFilesets() {
         for (Iterator iter = files.iterator(); iter.hasNext();) {
            FileSet fileset = (FileSet) iter.next();
            DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
            String[] f = ds.getIncludedFiles();
            File currentDir = fileset.getDir(getProject());

            for (int i = 0; i < f.length; i++) {
               File file = fu.resolveFile(currentDir, f[i]);
               if (!file.isFile()) {
                  continue;
               }

               if (currentDir.equals(file.getParentFile())) {
                  baseFiles.add(file);
                  continue;
               }
               
               if (file.getParentFile() == null) {
                  continue;
               }
               
               if (currentDir.equals(file.getParentFile().getParentFile())) {
                  List osList = (List) osMap.get(file.getParentFile().getName());
                  if (osList == null) {
                     osList = new ArrayList();
                     osMap.put(file.getParentFile().getName(), osList);
                  }

                  osList.add(file);
                  continue;
               }

               if (file.getParentFile().getParentFile() == null) {
                  continue;
               }
               
               if (currentDir.equals(file.getParentFile().getParentFile().getParentFile())) {
                  OsArchKey key = new OsArchKey();
                  key.os = file.getParentFile().getParentFile().getName();
                  key.arch = file.getParentFile().getName();

                  List archList = (List) archMap.get(key);
                  if (archList == null) {
                     archList = new ArrayList();
                     archMap.put(key, archList);
                  }

                  archList.add(file);
                  continue;
               }
            }
         }
      }

      public void toString(StringBuffer sb, int depth) {
         initFilesets();

         for (Iterator iter = baseFiles.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            Resources resources = new Resources();

            if (file.getName().toLowerCase().startsWith(nativeprefix)) {
               NativeLib nativeLib = resources.new NativeLib();
               nativeLib.href = file.getName();
               nativeLib.download = download;
               resources.nativeLibs.add(nativeLib);

               continue;
            }

            Jar jar = resources.new Jar();
            jar.href = file.getName();
            jar.download = download;
            resources.jars.add(jar);
            
            resources.toString(sb, depth);
         }
         
         for (Iterator iter = osMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry  entry = (Map.Entry) iter.next();
            List osFiles = (List) entry.getValue();
            String os = (String) entry.getKey();
            Resources resources = new Resources();
            resources.setOs(os);
            
            for (Iterator iterator = osFiles.iterator(); iterator.hasNext();) {
               File file = (File) iterator.next();
               
               if (file.getName().toLowerCase().startsWith(nativeprefix)) {
                  NativeLib nativeLib = resources.new NativeLib();
                  nativeLib.href = os + '/' + file.getName();
                  nativeLib.download = download;
                  resources.nativeLibs.add(nativeLib);

                  continue;
               }

               Jar jar = resources.new Jar();
               jar.href = os + '/' + file.getName();
               jar.download = download;
               resources.jars.add(jar);

               resources.toString(sb, depth);
            }
         }
         
         for (Iterator iter = archMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry  entry = (Map.Entry) iter.next();
            List archFiles = (List) entry.getValue();
            OsArchKey key = (OsArchKey) entry.getKey();
            Resources resources = new Resources();
            resources.setOs(key.os);
            resources.setArch(key.arch);
            
            for (Iterator iterator = archFiles.iterator(); iterator.hasNext();) {
               File file = (File) iterator.next();
               
               if (file.getName().toLowerCase().startsWith(nativeprefix)) {
                  NativeLib nativeLib = resources.new NativeLib();
                  nativeLib.href = key.os + '/' + key.arch + '/' + file.getName();
                  nativeLib.download = download;
                  resources.nativeLibs.add(nativeLib);

                  continue;
               }

               Jar jar = resources.new Jar();
               jar.href = key.os + '/' + key.arch + '/' + file.getName();
               jar.download = download;
               resources.jars.add(jar);

               resources.toString(sb, depth);
            }
         }
      }
      
      public class OsArchKey {
         String os;
         String arch;

         public boolean equals(Object obj) {
            OsArchKey that = (OsArchKey) obj;

            if (this == that) {
               return true;
            }

            return this.os.equals(that.os) && this.arch.equals(that.arch);
         }

         public int hashCode() {
            return this.os.hashCode() + this.arch.hashCode();
         }
      }
   }

    public class Security implements JNLPTaskMember {
        public class Permission implements JNLPTaskMember {
            private String name = null;

            public Permission(String s) {
                name = s;
            }

            public void toString(StringBuffer sb, int depth) {
                appendTabs(sb, depth);
                sb.append('<').append(name).append("/>").append(NEW_LINE);
            }
        }

        private Permission permission = null;

        public Permission createAll_Permissions() {
            return permission = new Permission("all-permissions");
        }

        public Permission createJ2ee_Application_Client_Permissions() {
            return permission = new Permission(
                    "j2ee-application-client-permissions");
        }

        public void toString(StringBuffer sb, int depth) {
            appendTabs(sb, depth);
            sb.append("<security>").append(NEW_LINE);
            if (permission == null)
                throw new RuntimeException(
                        "<security> requires either <all_permissions/> or <j2ee_application_client_permissions/> as child element.");
            else
                permission.toString(sb, depth + 1);

            appendTabs(sb, depth);
            sb.append("</security>").append(NEW_LINE);
        }
    }

    abstract public class XDesc implements JNLPTaskMember {
    }

    /**
     * marker for JnlpDownloadServlet.
     */
    public static final String JNLP_DOWNLOAD_SERVLET = "JnlpDownloadServlet";

    private static final String NEW_LINE = "\r\n";

    private static String correctHRefPath(String filePath) {
        return filePath.replace('\\', '/').replace(' ', '+');
    }

    private String codebase = null;

    private String encoding = "UTF-8";

    private String href = null;

    private List informations = new ArrayList();

    private String jnlpType = null;

    private List resources = new ArrayList();
    
    private List nativeResources = new ArrayList();

    private Security security = null;

    private String spec = "1.0+";

    private File toFile = null;

    private String version = null;
    
    private String nativeprefix = "native";

    private XDesc xDesc = null;

    private void appendTabs(StringBuffer sb, int depth) {
        while (depth-- > 0) {
            sb.append('\t');
        }
    }

    public AppletDesc createApplet_Desc() {
        if (xDesc != null)
            throw new RuntimeException(
                    "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
        xDesc = new AppletDesc();
        return (AppletDesc) xDesc;
    }

    public ApplicationDesc createApplication_Desc() {
        if (xDesc != null)
            throw new RuntimeException(
                    "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
        xDesc = new ApplicationDesc();
        return (ApplicationDesc) xDesc;
    }

    public ComponentDesc createComponent_Desc() {
        if (xDesc != null)
            throw new RuntimeException(
                    "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
        xDesc = new ComponentDesc();
        return (ComponentDesc) xDesc;
    }

    public Information createInformation() {
        Information information = new Information();
        informations.add(information);
        return information;
    }

    public InstallerDesc createInstaller_Desc() {
        if (xDesc != null)
            throw new RuntimeException(
                    "<jnlp> : Only one of application-desc | applet-desc | component-desc | installer-desc expected");
        xDesc = new InstallerDesc();
        return (InstallerDesc) xDesc;
    }

    public Resources createResources() {
        Resources _resources = new Resources();
        resources.add(_resources);
        return _resources;
    }

    public NativeResources createNativeResources() {
       NativeResources _resources = new NativeResources();
       nativeResources.add(_resources);
       return _resources;
   }

    public Security createSecurity() {
        return security = new Security();
    }

    public void execute() throws BuildException {
        try {
            if (toFile == null)
                throw new BuildException("Attribute toFile undefined");

            String s = toString();
            log(s, Project.MSG_INFO);
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(toFile));
                pw.print(s);
                pw.close();
            } catch (IOException ex) {
                throw new BuildException(
                        "Error writing created jnlp signature to "
                                + toFile.getAbsolutePath(), ex);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BuildException(ex);
        }

    }

    public void setCodebase(String string) {
        codebase = string;
    }

    public void setNativeprefix(String prefix) {
       nativeprefix = prefix;
    }
    /**
     * Set the encoding
     * @param encoding The new encoding toi use.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * The Href
     * @param href The new href
     */
    public void setHref(String href) {
        this.href = href;
    }

    public void setJnlpType(String jnlpType) {
        this.jnlpType = jnlpType;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public void setSpec(String string) {
        spec = string;
    }

    /** 
     * The output filename for; required
     * @param toFile The new File to write to. File extention .jnlp
     */
    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public void setVersion(String string) {
        version = string;
    }

    public String toString() {
        /*        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder;
         try {
         builder = factory.newDocumentBuilder();
         Document document = builder.newDocument();  
         writeJnlpElement(document);
         } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
         }
         */
        StringBuffer sb = new StringBuffer();
        toString(sb, 0);
        return sb.toString();
    }

    /*    private void writeJnlpElement(Document document) {
     Element jnlpElement = document.createElement("jnlp");
     if (getSpec() != null) {
     jnlpElement.setAttribute("spec", getSpec());
     }
     if( getVersion() !=null)
     jnlpElement.setAttribute("version", getVersion());
     if( getCodebase() != null)
     jnlpElement.setAttribute("codebase", getCodebase());         
     if( getHref() != null)
     jnlpElement.setAttribute("href", getHref());  
     
     if (informations.size() < 1) {
     throw new RuntimeException( "<jnlp> : At least one information expected");
     }

     for (int i = 0; i < informations.size(); i++)
     {
     Information information = (Information)informations.get( i);
     information.writeInformationsElement(document, jnlpElement);
     }
     
     if( security!=null)
     security.writeSecurityElement(document, jnlpElement);
     
     for (int i = 0; i<resources.size(); i++)
     {
     JNLPTask.Resources resources= (JNLPTask.Resources)this.resources.get( i);
     resources.writeResourcesElement( sb, 1);
     }
     
     if(xDesc == null) {
     throw new RuntimeException( "<jnlp> : Sub element missing ( application-desc | applet-desc | component-desc | installer-desc)");
     }
     
     xDesc.toString( sb, depth+1);   

     }
     */
    public void toString(StringBuffer sb, int depth) {
        sb.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append(
                "\"?>").append(NEW_LINE);
        sb
                .append(
                        "<!DOCTYPE jnlp PUBLIC \"-//Sun Microsystems, Inc//DTD JNLP Discriptor 1.1//EN\" \"http://java.sun.com/dtd/JNLP-1.5.dtd\">")
                .append(NEW_LINE);
        sb.append("<jnlp");
        if (spec != null)
            sb.append(" spec=\"").append(spec).append("\"");
        if (version != null)
            sb.append(" version=\"").append(version).append("\"");
        if (codebase != null)
            sb.append(" codebase=\"").append(codebase).append("\"");
        if (href != null)
            sb.append(" href=\"").append(href).append("\"");
        sb.append('>').append(NEW_LINE);

        if (informations.size() < 1) {
            throw new RuntimeException(
                    "<jnlp> : At least one information expected");
        }

        for (int i = 0; i < informations.size(); i++) {
            Information information = (Information) informations.get(i);
            information.toString(sb, 1);
        }

        if (security != null)
            security.toString(sb, depth + 1);

        for (int i = 0; i < resources.size(); i++) {
            JNLPTask.Resources resources = (JNLPTask.Resources) this.resources
                    .get(i);
            resources.toString(sb, 1);
        }

        for (int i = 0; i < nativeResources.size(); i++) {
            JNLPTask.NativeResources resources = (JNLPTask.NativeResources) this.nativeResources
                   .get(i);
            resources.toString(sb, 1);
        }

        if (xDesc == null) {
            throw new RuntimeException(
                    "<jnlp> : Sub element missing ( application-desc | applet-desc | component-desc | installer-desc)");
        }

        xDesc.toString(sb, depth + 1);

        sb.append("</jnlp>").append(NEW_LINE);
    }
}

/**
 * @version    1.0
 * @since      03.09.2003
 * @author     lars.gersmann@orangevolt.com
 *
 * (c) Copyright 2005 Orangevolt (www.orangevolt.com).
 */

interface JNLPTaskMember {
    public void toString(StringBuffer sb, int depth);
}

