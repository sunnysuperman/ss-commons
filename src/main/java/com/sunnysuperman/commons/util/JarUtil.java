package com.sunnysuperman.commons.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.sunnysuperman.commons.exception.UnexpectedException;

public class JarUtil {

	protected JarUtil() {
	}

	/**
	 * 拷贝JAR里的文件夹至文件系统
	 *
	 * @param srcPath  当前JAR里文件夹路径
	 * @param destPath 拷贝至目标文件夹路径
	 */
	public static void copyJarDir(String srcPath, String destPath) throws IOException {
		File destDir = new File(destPath);
		URL srcUrl = getResource(srcPath);
		String protocol = srcUrl.getProtocol();

		if ("file".equals(protocol)) {
			FileUtil.copy(urlToFile(srcUrl), destDir);
		} else if ("jar".equals(protocol)) {
			JarFile jar = urlToJar(srcUrl);
			copyJarDir(jar, srcPath, destDir);
		} else {
			throw new UnexpectedException("Bad url: " + srcUrl);
		}
	}

	/**
	 * 拷贝JAR里的文件夹至文件系统
	 *
	 * @param jar     JAR文件
	 * @param srcPath JAR里文件夹路径
	 * @param destDir 拷贝至目标文件夹
	 */
	public static void copyJarDir(JarFile jar, String srcPath, File destDir) throws IOException {
		if (!destDir.exists() && !destDir.mkdirs()) {
			throw new IOException("Failed to mkdirs: " + destDir.getAbsolutePath());
		}
		Enumeration<JarEntry> entries = jar.entries();
		// /a/b -> a/b/
		String srcPrefix = srcPath;
		if (srcPrefix.startsWith("/")) {
			srcPrefix = srcPrefix.substring(1);
		}
		if (!srcPrefix.isEmpty() && !srcPrefix.endsWith("/")) {
			srcPrefix = srcPrefix + "/";
		}
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			String entryName = entry.getName();
			// "a/b/c" startsWith "a/b/"
			if (!entryName.startsWith(srcPrefix)) {
				continue;
			}
			String fileName = entryName.substring(srcPrefix.length());
			File file = new File(destDir, fileName);
			copyEntry(jar, entry, file);
		}
	}

	/**
	 * 拷贝JAR里的文件至文件系统
	 *
	 * @param srcPath  当前JAR文件中文件路径
	 * @param destPath 拷贝至目标文件路径
	 */
	public static void copyJarFile(String srcPath, String destPath) throws IOException {
		File destFile = new File(destPath);
		URL srcUrl = getResource(srcPath);
		String protocol = srcUrl.getProtocol();

		if ("file".equals(protocol)) {
			FileUtil.copy(urlToFile(srcUrl), destFile);
		} else if ("jar".equals(protocol)) {
			JarFile jar = urlToJar(srcUrl);
			copyJarFile(jar, srcPath, destFile);
		} else {
			throw new UnexpectedException("Bad url: " + srcUrl);
		}
	}

	/**
	 * 拷贝JAR里的文件至文件系统
	 *
	 * @param jar      JAR文件
	 * @param srcPath  JAR文件中文件路径
	 * @param destFile 拷贝至目标文件
	 */
	public static void copyJarFile(JarFile jar, String srcPath, File destFile) throws IOException {
		FileUtil.makeDirByChild(destFile);

		ZipEntry entry = jar.getEntry(srcPath.startsWith("/") ? srcPath.substring(1) : srcPath);
		copyEntry(jar, entry, destFile);
	}

	private static URL getResource(String src) {
		return JarUtil.class.getResource(src);
	}

	private static File urlToFile(URL url) throws IOException {
		return new File(URLDecoder.decode(url.getFile(), StringUtil.UTF8));
	}

	private static JarFile urlToJar(URL url) throws IOException {
		return ((JarURLConnection) url.openConnection()).getJarFile();
	}

	private static void copyEntry(JarFile jar, ZipEntry entry, File destFile) throws IOException {
		FileUtil.makeDirByChild(destFile);

		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = jar.getInputStream(entry);
			out = new BufferedOutputStream(new FileOutputStream(destFile));
			FileUtil.copy(in, out);
		} finally {
			FileUtil.close(in);
			FileUtil.close(out);
		}
	}

}
