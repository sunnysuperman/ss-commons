package com.sunnysuperman.commons.test.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.commons.exception.Exceptions;
import com.sunnysuperman.commons.exception.UnexpectedException;

class ExceptionsTest {

	@Test
	void testWrapRuntimeException() {
		try {
			Exceptions.wrapRuntimeException("未知异常");
		} catch (Exception ex) {
			assertEquals(UnexpectedException.class, ex.getClass());
			assertEquals("未知异常", ex.getMessage());
			assertNull(ex.getCause());
		}

		try {
			Exceptions.wrapRuntimeException(new IllegalArgumentException("参数错误"));
		} catch (Exception ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
			assertEquals("参数错误", ex.getMessage());
			assertEquals(IllegalArgumentException.class, ex.getCause().getClass());
		}
		try {
			Exceptions.wrapRuntimeException(new IOException("IO异常"));
		} catch (Exception ex) {
			assertEquals(UnexpectedException.class, ex.getClass());
			assertNull(ex.getMessage());
			assertEquals(IOException.class, ex.getCause().getClass());
		}

		try {
			Exceptions.wrapRuntimeException(null, new IllegalArgumentException("未知异常XX"));
		} catch (Exception ex) {
			assertEquals(UnexpectedException.class, ex.getClass());
			assertNull(ex.getMessage());
			assertEquals(IllegalArgumentException.class, ex.getCause().getClass());
		}
		try {
			Exceptions.wrapRuntimeException("包装的错误消息1", new IllegalArgumentException("未知异常XX"));
		} catch (Exception ex) {
			assertEquals(UnexpectedException.class, ex.getClass());
			assertEquals("包装的错误消息1", ex.getMessage());
			assertEquals(IllegalArgumentException.class, ex.getCause().getClass());
		}
		try {
			Exceptions.wrapRuntimeException("包装的错误消息2", new IOException("未知异常XX"));
		} catch (Exception ex) {
			assertEquals(UnexpectedException.class, ex.getClass());
			assertEquals("包装的错误消息2", ex.getMessage());
			assertEquals(IOException.class, ex.getCause().getClass());
		}
	}

}
