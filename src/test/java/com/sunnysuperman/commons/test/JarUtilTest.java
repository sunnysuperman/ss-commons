package com.sunnysuperman.commons.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.jar.JarFile;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.commons.util.FileUtil;
import com.sunnysuperman.commons.util.JarUtil;

class JarUtilTest {

	@Test
	void copyJarDir() throws Exception {
		String dest = System.getProperty("user.dir") + "/tmp/jar-util-copydir";
		File destDir = new File(dest);
		JarFile jar = new JarFile(
				JarUtilTest.class.getResource("/com/sunnysuperman/commons/test/jar-util/test.jar").getFile());

		{
			FileUtil.delete(destDir);
			JarUtil.copyJarDir(jar, "/com/sunnysuperman/commons/test/jar-util", destDir);

			assertTrue(new File(dest, "test.properties").exists());
			assertTrue(new File(dest, "foo/test2.yml").exists());
			assertTrue(!new File(destDir, "__test.txt").exists());
		}
		{
			FileUtil.delete(destDir);
			JarUtil.copyJarDir(jar, "/com/sunnysuperman/commons/test", destDir);
			assertTrue(new File(dest, "jar-util/test.properties").exists());
			assertTrue(new File(dest, "jar-util/foo/test2.yml").exists());
			assertTrue(new File(destDir, "__test.txt").exists());
		}
	}

	@Test
	void copyJarDir2() throws Exception {
		String dest = System.getProperty("user.dir") + "/tmp/jar-util-copydir2";
		File destDir = new File(dest);

		{
			FileUtil.delete(destDir);
			JarUtil.copyJarDir("/com/sunnysuperman/commons/test/jar-util", dest);

			assertTrue(new File(dest, "test.properties").exists());
			assertTrue(new File(dest, "foo/test2.yml").exists());
			assertTrue(!new File(destDir, "__test.txt").exists());
		}
		{
			FileUtil.delete(destDir);
			JarUtil.copyJarDir("/com/sunnysuperman/commons/test", dest);
			assertTrue(new File(dest, "jar-util/test.properties").exists());
			assertTrue(new File(dest, "jar-util/foo/test2.yml").exists());
			assertTrue(new File(destDir, "__test.txt").exists());
		}
	}

	@Test
	void copyJarFile() throws Exception {
		{
			String dest = System.getProperty("user.dir") + "/tmp/test.properties";
			File destFile = new File(dest);
			FileUtil.delete(destFile);
			JarUtil.copyJarFile("/com/sunnysuperman/commons/test/jar-util/test.properties", dest);

			assertTrue(destFile.exists());
		}
		{
			String dest = System.getProperty("user.dir") + "/tmp/xx.yml";
			File destFile = new File(dest);
			FileUtil.delete(destFile);
			JarUtil.copyJarFile("/com/sunnysuperman/commons/test/jar-util/foo/test2.yml", dest);

			assertTrue(destFile.exists());
		}
	}

	@Test
	void copyJarFile2() throws Exception {
		JarFile jar = new JarFile(
				JarUtilTest.class.getResource("/com/sunnysuperman/commons/test/jar-util/test.jar").getFile());

		{
			String dest = System.getProperty("user.dir") + "/tmp/test.properties";
			File destFile = new File(dest);
			FileUtil.delete(destFile);
			JarUtil.copyJarFile(jar, "/com/sunnysuperman/commons/test/jar-util/test.properties", destFile);

			assertTrue(destFile.exists());
		}
		{
			String dest = System.getProperty("user.dir") + "/tmp/xx.yml";
			File destFile = new File(dest);
			FileUtil.delete(destFile);
			JarUtil.copyJarFile(jar, "/com/sunnysuperman/commons/test/jar-util/foo/test2.yml", destFile);

			assertTrue(destFile.exists());
		}
	}

}
