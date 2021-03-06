package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.Pozole;
import spoon.test.reference.testclasses.Tortillas;
import spoon.testing.utils.ModelUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class VariableAccessTest {

	@Test
	public void testVariableAccessDeclarationInAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.reference", "FooBar");
		assertEquals("FooBar", type.getSimpleName());

		final CtParameterReference<?> ref = type.getReferences(new AbstractReferenceFilter<CtParameterReference<?>>(CtParameterReference.class) {
			@Override
			public boolean matches(CtParameterReference<?> reference) {
				return "myArg".equals(reference.getSimpleName());
			}
		}).get(0);

		assertNotNull("Parameter can't be null", ref.getDeclaration());
		assertNotNull("Declaring method reference can't be null", ref.getDeclaringExecutable());
		assertNotNull("Declaring type of the method can't be null", ref.getDeclaringExecutable().getDeclaringType());
		assertNotNull("Declaration of declaring type of the method can't be null", ref.getDeclaringExecutable().getDeclaringType().getDeclaration());
		assertNotNull("Declaration of root class can't be null", ref.getDeclaringExecutable().getDeclaringType().getDeclaringType().getDeclaration());
	}

	@Test
	public void name() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);
		final CtMethod<Object> m2 = aPozole.getMethod("m2");
		final CtArrayWrite<?> ctArrayWrite = m2.getElements(new TypeFilter<CtArrayWrite<?>>(CtArrayWrite.class)).get(0);
		final CtLocalVariable expected = m2.getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).get(0);

		assertEquals(expected, ((CtVariableAccess) ctArrayWrite.getTarget()).getVariable().getDeclaration());
	}

	@Test
	public void testDeclarationOfVariableReference() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Foo2.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtVariableReference>(CtVariableReference.class) {
			@Override
			public boolean matches(CtVariableReference element) {
				try {
					element.clone().getDeclaration();
				} catch (NullPointerException e) {
					fail("Fail with " + element.getSimpleName() + " declared in " + element.getParent().getShortRepresentation());
				}
				return super.matches(element);
			}
		});
	}

	@Test
	public void testDeclaringTypeOfALambdaReferencedByParameterReference() {
		final spoon.Launcher launcher = new spoon.Launcher();
		launcher.addInputResource("src/test/resources/noclasspath/Foo3.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtExecutable<?>>(CtExecutable.class) {
			@Override
			public boolean matches(CtExecutable<?> exec) {
				final List<CtParameterReference<?>> guiParams = exec.getParameters().stream().map(CtParameter::getReference).collect(Collectors.toList());

				if (guiParams.size() != 1) {
					return false;
				}

				final CtParameterReference<?> param = guiParams.get(0);

				exec.getBody().getElements(new TypeFilter<CtParameterReference<?>>(CtParameterReference.class) {
					@Override
					public boolean matches(CtParameterReference<?> p) {
						assertEquals(p, param);
						return super.matches(p);
					}
				});

				return super.matches(exec);
			}
		});
	}

	@Test
	public void testGetDeclarationAfterClone() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/A2.java");
		launcher.buildModel();

		final CtClass<Object> a2 = launcher.getFactory().Class().get("A2");
		final CtClass<Object> a2Cloned = a2.clone();

		assertEquals(a2, a2Cloned);

		final CtMethod<Object> methodA2 = getMethod(launcher, a2);
		final CtMethod<Object> methodA2Cloned = getMethod(launcher, a2Cloned);

		final CtLocalVariable declaration = methodA2.getBody().getStatement(0);
		final CtLocalVariable declarationCloned = methodA2Cloned.getBody().getStatement(0);

		final CtLocalVariableReference localVarRef = getLocalVariableRefF1(methodA2);
		final CtLocalVariableReference localVarRefCloned = getLocalVariableRefF1(methodA2Cloned);

		assertEquals(localVarRef.getDeclaration(), declaration);
		assertTrue(localVarRef.getDeclaration() == declaration);
		assertEquals(localVarRefCloned.getDeclaration(), declarationCloned);
		assertTrue(localVarRefCloned.getDeclaration() == declarationCloned);
	}

	@Test
	public void testReferences() throws Exception {
		final CtType<Tortillas> aTortillas = buildClass(Tortillas.class);
		final CtMethod<Object> make = aTortillas.getMethod("make", aTortillas.getFactory().Type().stringType());
		System.out.println(make);
	}

	private CtMethod<Object> getMethod(Launcher launcher, CtClass<Object> a2) {
		return a2.getMethod("b", launcher.getFactory().Type().integerPrimitiveType());
	}

	private CtLocalVariableReference getLocalVariableRefF1(CtMethod<Object> method) {
		return method.getElements(new TypeFilter<CtLocalVariableReference>(CtLocalVariableReference.class) {
			@Override
			public boolean matches(CtLocalVariableReference element) {
				return "f1".equals(element.getSimpleName()) && super.matches(element);
			}
		}).get(0);
	}
}
