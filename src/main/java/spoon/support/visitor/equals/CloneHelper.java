/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.visitor.equals;

import spoon.reflect.declaration.CtElement;
import spoon.support.visitor.clone.CloneVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class CloneHelper {
	public static <T extends CtElement> T clone(T element) {
		final CloneVisitor cloneVisitor = new CloneVisitor();
		cloneVisitor.scan(element);
		return cloneVisitor.getClone();
	}

	public static <T extends CtElement> Collection<T> clone(Collection<T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<>();
		}
		Collection<T> others = new ArrayList<>();
		for (T element : elements) {
			others.add(CloneHelper.clone(element));
		}
		return others;
	}

	public static <T extends CtElement> List<T> clone(List<T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<>();
		}
		List<T> others = new ArrayList<>();
		for (T element : elements) {
			others.add(CloneHelper.clone(element));
		}
		return others;
	}

	public static <T extends CtElement> Set<T> clone(Set<T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new TreeSet<>();
		}
		Set<T> others = new TreeSet<>();
		for (T element : elements) {
			others.add(CloneHelper.clone(element));
		}
		return others;
	}

	public static <T extends CtElement> Map<String, T> clone(Map<String, T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new HashMap<>();
		}
		Map<String, T> others = new HashMap<>();
		for (Map.Entry<String, T> tEntry : elements.entrySet()) {
			others.put(tEntry.getKey(), CloneHelper.clone(tEntry.getValue()));
		}
		return others;
	}

	private CloneHelper() {
		throw new AssertionError("No instance.");
	}
}
