package processors;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import annotations.Isolating;

public class IsolatingProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Isolating.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<?> annotatedElements = roundEnv.getElementsAnnotatedWith(Isolating.class);
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.WARNING,
                "Running ISOLATING processor on " + annotatedElements.toString()
        );

        return false;
    }
}
