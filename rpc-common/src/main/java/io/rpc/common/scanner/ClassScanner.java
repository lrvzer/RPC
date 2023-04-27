package io.rpc.common.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 实现RPC服务核心注解的扫描与解析
 */
public class ClassScanner {

    /**
     * 文件
     */
    private static final String PROTOCOL_FILE = "file";

    /**
     * jar包
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * class文件后缀
     */
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * 扫描当前工程中指定包下的所有类信息
     *
     * @param packageName   扫描的包名
     * @param packagePath   包在磁盘上的完整路径
     * @param recursive     是否递归调用
     * @param classNameList 类名称的集合
     */
    private static void findAndAddClassesInPackageByFile(String packageName,
                                                         String packagePath,
                                                         final boolean recursive,
                                                         List<String> classNameList) {
        // 根据路径创建File
        File dir = new File(packagePath);
        // 文件不存在或者文件不是目录则直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 自定义过滤规则
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory() || (file.getName().endsWith(CLASS_FILE_SUFFIX))));

        if (dirFiles != null) {
            // 遍历所有文件
            for (File file : dirFiles) {
                if (file.isDirectory()) {
                    // 目录递归
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                            file.getAbsolutePath(),
                            recursive,
                            classNameList);
                } else {
                    // Java类文件，截取类名
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    // 入list
                    classNameList.add(packageName + "." + className);
                }
            }
        }
    }

    /**
     * 实现扫描jar文件下的类信息
     *
     * @param packageName    扫描包名
     * @param classNameList  类名称的集合
     * @param recursive      是否递归调用
     * @param packageDirName 当前包名的前面部分的名称
     * @param url            包的url地址
     * @return 处理后的包名，递归调用使用
     * @throws IOException
     */
    private static String findAndAddClassesInPackageByJar(String packageName,
                                                          List<String> classNameList,
                                                          boolean recursive,
                                                          String packageDirName,
                                                          URL url) throws IOException {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }

                if (((idx != -1) || recursive) && (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory())) {
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    classNameList.add(packageName + '.' + className);
                }
            }
        }
        return packageName;
    }

    /**
     * 扫描指定包下的所有类信息
     * @param packageName
     * @return
     * @throws Exception
     */
    public static List<String> getClassNameList(String packageName) throws Exception {
        List<String> classNameList = new ArrayList<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if (PROTOCOL_FILE.equals(protocol)) {
                String filePath = URLDecoder.decode(url.getPath(), "UTF-8");
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (PROTOCOL_JAR.equals(protocol)) {
                packageName = findAndAddClassesInPackageByJar(packageName, classNameList, recursive, packageDirName, url);
            }
        }
        return classNameList;
    }
}
