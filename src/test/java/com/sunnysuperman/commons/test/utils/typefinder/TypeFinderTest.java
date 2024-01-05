package com.sunnysuperman.commons.test.utils.typefinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.typefinder.AnnotatedEvt4;
import org.typefinder.ConcreteEvt4;
import org.typefinder.SubAnnotatedEvt4;

import com.sunnysuperman.commons.test.typefinder.AnnotatedEvt3;
import com.sunnysuperman.commons.test.typefinder.ConcreteEvt3;
import com.sunnysuperman.commons.test.utils.typefinder.sub.AnnotatedEvt2;
import com.sunnysuperman.commons.test.utils.typefinder.sub.ConcreteEvt2;
import com.sunnysuperman.commons.test.utils.typefinder.sub.MyAnotherEvt2;
import com.sunnysuperman.commons.util.TypeFinder;

class TypeFinderTest {

	@Test
	void testFindTypesAnnotatedWith() {
		{
			Set<Class<?>> classes = TypeFinder.findTypesAnnotatedWith(
					new String[] { "com.sunnysuperman.commons.test.utils.typefinder" }, EvtAnnotation.class);
			assertEquals(2, classes.size());
			assertTrue(classes.contains(AnnotatedEvt.class));
			assertTrue(classes.contains(AnnotatedEvt2.class));
		}
		System.out.println("====================");
		{
			Set<Class<?>> classes = TypeFinder.findTypesAnnotatedWith(new String[] { "com.sunnysuperman" },
					EvtAnnotation.class);
			assertEquals(3, classes.size());
			assertTrue(classes.contains(AnnotatedEvt.class));
			assertTrue(classes.contains(AnnotatedEvt2.class));
			assertTrue(classes.contains(AnnotatedEvt3.class));
		}
		System.out.println("====================");
		{
			Set<Class<?>> classes = TypeFinder.findTypesAnnotatedWith(
					new String[] { "com.sunnysuperman.commons.test.utils.typefinder", "org.typefinder" },
					EvtAnnotation.class);
			assertEquals(4, classes.size());
			assertTrue(classes.contains(AnnotatedEvt.class));
			assertTrue(classes.contains(AnnotatedEvt2.class));
			assertTrue(classes.contains(AnnotatedEvt4.class));
			assertTrue(classes.contains(SubAnnotatedEvt4.class));
		}
		System.out.println("====================");
		{
			assertNotNull(SubAnnotatedEvt4.class.getAnnotation(EvtAnnotation.class));
			Set<Class<?>> classes = TypeFinder.findTypesAnnotatedWith(
					new String[] { "com.sunnysuperman.commons.test.utils.typefinder", "org.typefinder" }, false,
					EvtAnnotation.class);
			assertEquals(3, classes.size());
			assertTrue(classes.contains(AnnotatedEvt.class));
			assertTrue(classes.contains(AnnotatedEvt2.class));
			assertTrue(classes.contains(AnnotatedEvt4.class));
		}
	}

	@Test
	void testFindTypesAnnotatedWith2() {
		{
			Set<Class<?>> classes = TypeFinder.findTypesAnnotatedWith(
					new String[] { "com.sunnysuperman.commons.test.utils.typefinder" }, EvtAnnotation.class,
					AnotherEvtAnnotation.class);
			assertEquals(3, classes.size());
			assertTrue(classes.contains(AnnotatedEvt.class));
			assertTrue(classes.contains(AnnotatedEvt2.class));
			assertTrue(classes.contains(MyAnotherEvt2.class));
		}
		System.out.println("====================");
		{
			Set<Class<?>> classes = TypeFinder.findTypesAnnotatedWith(
					new String[] { "com.sunnysuperman.commons.test.utils.typefinder.sub" }, EvtAnnotation.class,
					AnotherEvtAnnotation.class);
			assertEquals(2, classes.size());
			assertTrue(classes.contains(AnnotatedEvt2.class));
			assertTrue(classes.contains(MyAnotherEvt2.class));
		}
	}

	@Test
	void testFindSubTypesOf() {
		{
			Set<Class<?>> classes = TypeFinder
					.findSubTypesOf(new String[] { "com.sunnysuperman.commons.test.utils.typefinder" }, Evt.class);
			assertEquals(2, classes.size());
			assertTrue(classes.contains(ConcreteEvt.class));
			assertTrue(classes.contains(ConcreteEvt2.class));
		}
		System.out.println("====================");
		{
			Set<Class<?>> classes = TypeFinder.findSubTypesOf(new String[] { "com.sunnysuperman" }, Evt.class);
			assertEquals(3, classes.size());
			assertTrue(classes.contains(ConcreteEvt.class));
			assertTrue(classes.contains(ConcreteEvt2.class));
			assertTrue(classes.contains(ConcreteEvt3.class));
		}
		System.out.println("====================");
		{
			Set<Class<?>> classes = TypeFinder.findSubTypesOf(
					new String[] { "com.sunnysuperman.commons.test.utils.typefinder", "org.typefinder" }, Evt.class);
			assertEquals(3, classes.size());
			assertTrue(classes.contains(ConcreteEvt.class));
			assertTrue(classes.contains(ConcreteEvt2.class));
			assertTrue(classes.contains(ConcreteEvt4.class));
		}
	}

}
