package com.sunnysuperman.commons.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
	public static final String LINE = System.getProperty("line.separator");
	public static final char SLASH_CHAR = '/';
	public static final String SLASH = "/";

	public static void close(Closeable in) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 从文件中读取字节数组
	 * 
	 * @param file 待读取文件
	 * @return 字节数组
	 * @throws IOException
	 */
	public static byte[] readAsByteArray(File file) throws IOException {
		// TODO use fixed buffer
		return readAsByteArray(new FileInputStream(file));
	}

	/***
	 * 
	 * read bytes from input stream, then close the stream
	 * 
	 ***/
	public static byte[] readAsByteArray(InputStream inStream) throws IOException {
		try {
			int size = 1024;
			byte ba[] = new byte[size];
			int readSoFar = 0;
			do {
				int nRead = inStream.read(ba, readSoFar, size - readSoFar);
				if (nRead == -1) {
					break;
				}
				readSoFar += nRead;
				if (readSoFar == size) {
					int newSize = size * 2;
					byte newBa[] = new byte[newSize];
					System.arraycopy(ba, 0, newBa, 0, size);
					ba = newBa;
					size = newSize;
				}
			} while (true);
			byte newBa[] = new byte[readSoFar];
			System.arraycopy(ba, 0, newBa, 0, readSoFar);
			return newBa;
		} finally {
			close(inStream);
		}
	}

	/**
	 * 文件是否有效
	 * 
	 * @param allowedExts 允许的扩展名
	 * @param fileName    文件名
	 * @return 若文件无扩展名，则返回null；若文件扩展名在allowedExts中，则返回true；否则，返回false
	 */
	public static boolean isValidFile(String[] allowedExts, String fileName) {
		String ext = getFileExt(fileName);
		if (ext == null) {
			return false;
		}
		ext = ext.trim().toLowerCase();
		for (int i = 0; i < allowedExts.length; i++) {
			if (ext.equals(allowedExts[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName 文件名
	 * @return 文件扩展名
	 */
	public static String getFileExt(String fileName) {
		if (fileName == null) {
			return null;
		}
		fileName = fileName.trim();
		int index = fileName.lastIndexOf('.');
		if (index > 0 && index < fileName.length() - 1) {
			return fileName.substring(index + 1).toLowerCase();
		}
		return null;
	}

	/**
	 * 写string到文件
	 * 
	 * @param file          目标文件
	 * @param content       待写入字符串
	 * @param outputCharset 写入的字符编码
	 * @param append        是否是追加写
	 * @throws IOException
	 */
	private static void writeString(File file, String content, String outputCharset, boolean append)
			throws IOException {
		if (outputCharset == null) {
			outputCharset = StringUtil.UTF8;
		}
		if (content == null) {
			return;
		}
		BufferedWriter output = null;
		try {
			ensureFile(file);
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), outputCharset));
			output.write(content);
		} finally {
			close(output);
		}
	}

	/**
	 * 写流到文件
	 * 
	 * @param file          目标文件
	 * @param outputCharset 写入的字符编码
	 * @param in            待写入的流
	 * @param inputCharset  读取流的编码
	 * @param append        是否是追加写
	 * @throws IOException
	 */
	private static void writeBlob(File file, String outputCharset, InputStream in, String inputCharset, boolean append)
			throws IOException {
		if (outputCharset == null) {
			outputCharset = StringUtil.UTF8;
		}
		if (inputCharset == null) {
			inputCharset = StringUtil.UTF8;
		}
		Writer output = null;
		BufferedReader input = null;
		try {
			ensureFile(file);
			input = new BufferedReader(new InputStreamReader(in, inputCharset));
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), outputCharset));
			int len = 0;
			char[] buf = new char[2048];
			while ((len = input.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
		} finally {
			close(input);
			close(output);
		}
	}

	/**
	 * 写字符串到文件，追加写，默认为utf8编码
	 * 
	 * @param file    目标文件
	 * @param content 待写入的字符串
	 * @throws IOException
	 */
	public static void append(File file, String content) throws IOException {
		writeString(file, content, null, true);
	}

	/**
	 * 写字符串到文件，追加写
	 * 
	 * @param file          目标文件
	 * @param content       待写入的字符串
	 * @param outputCharset 写入的字符串编码
	 * @throws IOException
	 */
	public static void append(File file, String content, String outputCharset) throws IOException {
		writeString(file, content, outputCharset, true);
	}

	/**
	 * 写流到文件，追加写
	 * 
	 * @param file          目标文件
	 * @param outputCharset 写入的编码
	 * @param in            待写入的流
	 * @param inputCharset  读取流的编码
	 * @throws IOException
	 */
	public static void append(File file, String outputCharset, InputStream in, String inputCharset) throws IOException {
		writeBlob(file, outputCharset, in, inputCharset, true);
	}

	/**
	 * 写字符串到文件，覆盖写
	 * 
	 * @param file    目标文件
	 * @param content 待写入的字符串
	 * @throws IOException
	 */
	public static void write(File file, String content) throws IOException {
		writeString(file, content, null, false);
	}

	/**
	 * 写字符串到文件，覆盖写
	 * 
	 * @param file          目标文件
	 * @param content       待写入的字符串
	 * @param outputCharset 写入的字符串编码
	 * @throws IOException
	 */
	public static void write(File file, String content, String outputCharset) throws IOException {
		writeString(file, content, outputCharset, false);
	}

	/**
	 * 写流到文件，覆盖写
	 * 
	 * @param file          目标文件
	 * @param outputCharset 写入的编码
	 * @param in            待写入的流
	 * @param inputCharset  读取流的编码
	 * @throws IOException
	 */
	public static void write(File file, String outputCharset, InputStream in, String inputCharset) throws IOException {
		writeBlob(file, outputCharset, in, inputCharset, false);
	}

	/**
	 * 读文件，默认utf8编码
	 * 
	 * @param path 待读取的文件的路径
	 * @return 文件中的字符串
	 * @throws IOException
	 */
	public static String read(String path) throws IOException {
		return read(new File(path), null);
	}

	/**
	 * 读文件，默认utf8编码
	 * 
	 * @param f 待读取的文件
	 * @return 文件中的字符串
	 * @throws IOException
	 */
	public static String read(File f) throws IOException {
		return read(f, null);
	}

	/**
	 * 读流，默认utf8编码
	 * 
	 * @param in 读取的流
	 * @return 流中的字符串
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException {
		return read(in, null);
	}

	/**
	 * 读文件
	 * 
	 * @param path    待读取的文件的路径
	 * @param charset 读取的编码
	 * @return 文件中的字符串
	 * @throws IOException
	 */
	public static String read(String path, String charset) throws IOException {
		return read(new File(path), charset);
	}

	/**
	 * 读文件
	 * 
	 * @param f       待读取的文件
	 * @param charset 读取的编码
	 * @return 文件中的字符串
	 * @throws IOException
	 */
	public static String read(File f, String charset) throws IOException {
		return read(new FileInputStream(f), charset);
	}

	/**
	 * 读文件
	 * 
	 * @param in      读取的流
	 * @param charset 读取的编码
	 * @return 文件中的字符串
	 * @throws IOException
	 */
	public static String read(InputStream in, String charset) throws IOException {
		if (charset == null) {
			charset = StringUtil.UTF8;
		}
		StringBuilder buf = new StringBuilder();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(in, charset));
			int len = 0;
			char[] cbuf = new char[2048];
			while ((len = input.read(cbuf)) > 0) {
				buf.append(cbuf, 0, len);
			}
			return buf.toString();
		} finally {
			close(input);
		}
	}

	/**
	 * 读文件操作，每行的处理类
	 * 
	 * 
	 *
	 */
	public static interface ReadLineHandler {
		/**
		 * 处理每行的字符串
		 * 
		 * @param s    当前行的字符串
		 * @param line 当前行
		 * @return 是否处理成功
		 * @throws Exception
		 */
		boolean handle(String s, int line) throws Exception;
	}

	/**
	 * 读取流，且处理每一行的字符串
	 * 
	 * @param in      输入流
	 * @param charset 读取的编码
	 * @param handler 每一行的处理器
	 * @throws Exception
	 */
	public static void read(InputStream in, String charset, ReadLineHandler handler) throws Exception {
		String s = null;
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(in, charset == null ? StringUtil.UTF8 : charset));
			int i = 0;
			while ((s = input.readLine()) != null) {
				if (!handler.handle(s, ++i)) {
					break;
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			close(input);
		}
	}

	/**
	 * 拷贝输入流到输出流，每次读写8024字节
	 * 
	 * @param input  输入流
	 * @param output 输出流
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException {
		copy(input, output, 8024);
	}

	/**
	 * 拷贝输入流到输出流
	 * 
	 * @param input      输入流
	 * @param output     输出流
	 * @param buffersize 缓冲数组的长度，每次读写的字节数
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output, int buffersize) throws IOException {
		byte[] buf = new byte[buffersize];
		int n;
		while ((n = input.read(buf)) != -1) {
			if (n > 0) {
				output.write(buf, 0, n);
			}
		}
	}

	/**
	 * 拷贝文件
	 * <p>
	 * 可支持拷贝文件夹
	 * 
	 * @param src  源文件
	 * @param dest 目标文件
	 * @throws IOException
	 */
	public static void copy(File src, File dest) throws IOException {
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath() + ".");
		} else if (!src.canRead()) {
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath() + ".");
		}
		if (src.isDirectory()) {
			if (!dest.exists()) {
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
				}
			}
			String[] list = src.list();
			for (int i = 0; i < list.length; i++) {
				File src1 = new File(src, list[i]);
				File dest1 = new File(dest, list[i]);
				copy(src1, dest1);
			}
		} else {
			ensureFile(dest);

			BufferedInputStream fin = null;
			BufferedOutputStream fout = null;
			try {
				fin = new BufferedInputStream(new FileInputStream(src));
				fout = new BufferedOutputStream(new FileOutputStream(dest));
				copy(fin, fout);
			} finally {
				close(fin);
				close(fout);
			}
		}
	}

	/**
	 * 拷贝流到文件
	 * 
	 * @param in       待拷贝的流
	 * @param filePath 目标文件的路径
	 * @throws IOException
	 */
	public static void save(InputStream in, String filePath) throws IOException {
		BufferedOutputStream out = null;
		try {
			File file = new File(filePath);
			ensureFile(file);
			out = new BufferedOutputStream(new FileOutputStream(file));
			copy(in, out);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * 获取file
	 * 
	 * @param paths 路径数组，拼装到一起，即为文件的全路径
	 * @return 文件
	 * @throws IOException
	 */
	public static File getFile(String[] paths) {
		if (paths == null || paths.length == 0) {
			throw new RuntimeException("Bad paths");
		}
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (path == null || path.isEmpty()) {
				continue;
			}
			path = StringUtil.replaceAll(path, "\\", SLASH);
			// remove head slash
			if (buf.length() > 0) {
				char firstChar = path.charAt(0);
				if (firstChar == SLASH_CHAR) {
					path = path.substring(1);
					if (path.isEmpty()) {
						continue;
					}
				}
			}
			// remove tail slash
			{
				int len = path.length();
				char lastChar = path.charAt(len - 1);
				if (lastChar == SLASH_CHAR) {
					path = path.substring(0, len - 1);
					if (path.isEmpty()) {
						continue;
					}
				}
			}
			// append slash
			if (buf.length() > 0) {
				buf.append(SLASH_CHAR);
			}
			buf.append(path);
		}
		return new File(buf.toString());
	}

	/**
	 * 创建父路径的文件夹
	 * 
	 * @param file 文件
	 * @return 父文件不存在，或创建父文件夹失败，返回false；否则，返回true
	 * @throws IOException
	 */
	public static boolean makeDirByChild(File file) throws IOException {
		File parent = file.getParentFile();
		if (parent != null) {
			return parent.mkdirs();
		}
		return false;
	}

	/**
	 * 新建文件
	 */
	public static boolean createFile(File file) throws IOException {
		makeDirByChild(file);
		return file.createNewFile();
	}

	/**
	 * 新建文件
	 */
	public static boolean ensureFile(File file) throws IOException {
		if (file.exists()) {
			return false;
		}
		makeDirByChild(file);
		return file.createNewFile();
	}

	/**
	 * 删除文件，或递归删除文件夹
	 * 
	 * @param file 待删除文件
	 * @return 若文件不存在，或删除失败，则返回false；否则，返回ture
	 * @throws IOException
	 */
	public static boolean delete(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}
		if (file.isFile()) {
			return file.delete();
		}
		for (File subFile : file.listFiles()) {
			delete(subFile);
		}
		return file.delete();
	}

	public static void deleteQuietly(File file) {
		if (file == null) {
			return;
		}
		try {
			delete(file);
		} catch (Throwable t) {
			LOG.error(null, t);
		}
	}

	/**
	 * 
	 * 
	 *
	 */
	public static interface FileListHandler {

		boolean willOpenStream(String fileName, String fullPath, boolean isDirectory) throws Exception;

		void streamOpened(String fileName, String fullPath, InputStream in) throws Exception;
	}

	/**
	 * 
	 * @param jarPath
	 * @param dirPath
	 * @param handler
	 * @throws Exception
	 */
	public static void listClassPathFiles(String jarPath, String dirPath, FileListHandler handler) throws Exception {
		JarFile jar = new JarFile(jarPath);
		dirPath = toUnixFilePath(dirPath);
		try {
			Enumeration<JarEntry> entries = jar.entries();
			String relativePath = dirPath;
			// conf/locales
			if (!relativePath.isEmpty()) {
				if (relativePath.charAt(0) == SLASH_CHAR) {
					relativePath = dirPath.substring(1);
				}
				if (!relativePath.isEmpty()) {
					if (relativePath.charAt(relativePath.length() - 1) != SLASH_CHAR) {
						relativePath += SLASH_CHAR;
					}
				}
			}
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				String fullEntryName = toUnixFilePath(entry.getName());
				String entryName = fullEntryName;
				if (!relativePath.isEmpty()) {
					if (!fullEntryName.startsWith(relativePath) || fullEntryName.length() == relativePath.length()) {
						continue;
					}
					entryName = fullEntryName.substring(relativePath.length());
				}
				if (entryName.charAt(entryName.length() - 1) == SLASH_CHAR) {
					entryName = entryName.substring(0, entryName.length() - 1);
				}
				int slashOffset = entryName.lastIndexOf(SLASH_CHAR);
				String simpleName = entryName;
				if (slashOffset > 0) {
					simpleName = entryName.substring(slashOffset + 1);
				}
				if (!handler.willOpenStream(simpleName, entryName, entry.isDirectory())) {
					continue;
				}
				if (!entry.isDirectory()) {
					handler.streamOpened(simpleName, entryName, jar.getInputStream(entry));
				}
			}
		} finally {
			close(jar);
		}
	}

	/**
	 * 
	 * @param dir
	 * @param basePath
	 * @param handler
	 * @throws Exception
	 */
	private static void listSystemPathFiles(final File dir, final String basePath, final FileListHandler handler)
			throws Exception {
		int headLen = basePath.length();
		// case1 / /a -> a
		// case2 /b /b/c -> c
		if (headLen > 1) {
			headLen++;
		}
		for (File file : dir.listFiles()) {
			String relativePath = toUnixFilePath(file.getAbsolutePath().substring(headLen));
			if (handler.willOpenStream(file.getName(), relativePath, file.isDirectory())) {
				if (file.isDirectory()) {
					listSystemPathFiles(file, basePath, handler);
				} else {
					handler.streamOpened(file.getName(), relativePath, new FileInputStream(file));
				}
			}
		}
	}

	/**
	 * 
	 * @param clazz
	 * @param dirPath
	 * @param handler
	 * @throws Exception
	 */
	public static void listClassPathFiles(Class<?> clazz, String dirPath, FileListHandler handler) throws Exception {
		URL dirURL = clazz.getResource(dirPath);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			File file = new File(dirURL.getFile());
			if (!file.isDirectory()) {
				throw new IOException("Not a directory");
			}
			listSystemPathFiles(file, file.getAbsolutePath(), handler);
		} else if (dirURL.getProtocol().equals("jar")) {
			// file:/xxx/xxx.jar!/conf/locales
			String dirInJarPath = dirURL.getPath();
			String jarKey = ".jar!";
			int jarOffset = dirInJarPath.indexOf(jarKey);
			String jarPath = dirInJarPath.substring(5, jarOffset + jarKey.length() - 1);
			dirPath = dirInJarPath.substring(jarOffset + jarKey.length());
			listClassPathFiles(jarPath, dirPath, handler);
		} else {
			throw new IOException("Bad dirPath: " + dirPath);
		}
	}

	/**
	 * 将文件路径转换为unix文件路径，即将"\\"转换为"/"
	 * 
	 * @param s 文件路径
	 * @return 转换后的路径
	 */
	public static String toUnixFilePath(String s) {
		if (File.separatorChar == SLASH_CHAR) {
			return s;
		}
		if (s == null || s.isEmpty()) {
			return s;
		}
		return StringUtil.replaceAll(s, "\\", SLASH);
	}

	public static class ReadPropertiesLineHandler implements ReadLineHandler {
		private String concatKey = null;
		private String concatStr = null;
		private LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
		private boolean escapeSpecialChars;

		public ReadPropertiesLineHandler(boolean escapeSpecialChars) {
			super();
			this.escapeSpecialChars = escapeSpecialChars;
		}

		public String[] getKV(String s) {
			int offset = s.indexOf('=');
			if (offset <= 0) {
				throw new RuntimeException("Bad config line: " + s);
			}
			String key = s.substring(0, offset).trim();
			if (key.isEmpty()) {
				throw new RuntimeException("Empty key");
			}
			String value = s.substring(offset + 1).trim();
			return new String[] { key, value };
		}

		@Override
		public boolean handle(String s, int line) throws Exception {
			s = s.trim();
			if (concatStr == null) {
				if (s.isEmpty() || s.charAt(0) == '#') {
					return true;
				}
			} else {
				if (s.isEmpty()) {
					properties.put(concatKey, concatStr);
					concatStr = null;
					return true;
				}
			}
			s = StringUtil.parseUnicodeString(s);
			if (escapeSpecialChars) {
				s = StringUtil.escapeSpecialChars(s);
			}
			if (s.charAt(s.length() - 1) == '\\') {
				s = s.substring(0, s.length() - 1);
				if (concatStr == null) {
					String[] kv = getKV(s);
					concatKey = kv[0];
					concatStr = kv[1];
				} else {
					concatStr += s;
				}
				return true;
			} else if (concatStr != null) {
				concatStr += s;
				properties.put(concatKey, concatStr);
				concatStr = null;
				return true;
			}
			String[] kv = getKV(s);
			properties.put(kv[0], kv[1]);
			return true;
		}

		public LinkedHashMap<String, String> getProperties() {
			return properties;
		}

	}

	public static LinkedHashMap<String, String> readProperties(InputStream in, String charset,
			boolean escapeSpecialChars) throws Exception {
		ReadPropertiesLineHandler handler = new ReadPropertiesLineHandler(escapeSpecialChars);
		read(in, charset, handler);
		return handler.getProperties();
	}

	private static void addFileToZip(File srcFile, ZipOutputStream out, String base) throws IOException {
		if (!base.isEmpty() && base.charAt(base.length() - 1) != '/') {
			base += '/';
		}
		String entryName = base + srcFile.getName();
		if (srcFile.isFile()) {
			out.putNextEntry(new ZipEntry(entryName));
			FileInputStream in = null;
			try {
				in = new FileInputStream(srcFile);
				copy(in, out);
			} finally {
				close(in);
			}
			out.closeEntry();
		} else {
			File[] files = srcFile.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					addFileToZip(files[i], out, entryName);
				}
			}
		}
	}

	public static void zip(File archiveFile, File[] srcFiles, String encoding) throws IOException {
		ensureFile(archiveFile);
		if (encoding == null) {
			encoding = StringUtil.UTF8;
		}
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(archiveFile), Charset.forName(encoding));
			for (File srcFile : srcFiles) {
				addFileToZip(srcFile, out, StringUtil.EMPTY);
			}
		} finally {
			close(out);
		}
	}

	public static void zip(File archiveFile, File srcFile, String encoding) throws IOException {
		if (!srcFile.exists()) {
			throw new IOException("Source file " + srcFile.getAbsolutePath() + " does not exists");
		}
		ensureFile(archiveFile);
		if (encoding == null) {
			encoding = StringUtil.UTF8;
		}
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(archiveFile), Charset.forName(encoding));
			addFileToZip(srcFile, out, StringUtil.EMPTY);
		} finally {
			close(out);
		}
	}

	public static void unzip(File archiveFile, File destDir, String encoding) throws IOException {
		if (!archiveFile.exists()) {
			throw new IOException("archive file " + archiveFile.getAbsolutePath() + " does not exists");
		}
		if (!destDir.exists()) {
			destDir.mkdirs();
		} else if (!destDir.isDirectory()) {
			throw new IOException(destDir.getAbsolutePath() + " is not a valid decompress destination folder");
		}
		if (encoding == null) {
			encoding = StringUtil.UTF8;
		}

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(archiveFile, Charset.forName(encoding));
			Enumeration<? extends ZipEntry> en = zipFile.entries();
			ZipEntry zipEntry = null;
			while (en.hasMoreElements()) {
				zipEntry = en.nextElement();
				String name = zipEntry.getName();
				File destFile = getFile(new String[] { destDir.getAbsolutePath(), name });
				if (zipEntry.isDirectory()) {
					destFile.mkdirs();
				} else {
					ensureFile(destFile);
					InputStream in = null;
					FileOutputStream out = null;
					try {
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(destFile);
						copy(in, out);
					} finally {
						close(in);
						close(out);
					}
				}
			}
		} finally {
			close(zipFile);
		}
	}

}
