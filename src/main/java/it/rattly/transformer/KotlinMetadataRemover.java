package it.rattly.transformer;

import dev.mdma.qprotect.api.jar.JarFile;
import dev.mdma.qprotect.api.transformer.ClassTransformer;
import dev.mdma.qprotect.api.transformer.TransformException;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class KotlinMetadataRemover extends ClassTransformer {

    public KotlinMetadataRemover() {
        super("KotlinMetadataRemover", "Removed {} kotlin metadata");
    }

    @Override
    public boolean runOnClass(String className, ClassNode classNode, JarFile jarFile) throws TransformException {
        AtomicReference<Boolean> modified = new AtomicReference<>(false);
        Predicate<AnnotationNode> predicate = annotationNode -> {
            if (annotationNode.desc.equals("Lkotlin/Metadata;") ||
                    annotationNode.desc.equals("Lkotlin/coroutines/jvm/internal/DebugMetadata;") ||
                    annotationNode.desc.equals("Lkotlin/jvm/internal/SourceDebugExtension;")) {
                modified.set(true);
                System.out.println("Removed " + annotationNode.desc + " from " + className);
                return true;
            } else return false;
        };

        if (classNode.visibleAnnotations != null)
            classNode.visibleAnnotations.removeIf(predicate);

        if (classNode.invisibleAnnotations != null)
            classNode.invisibleAnnotations.removeIf(predicate);

        if (classNode.visibleTypeAnnotations != null)
            classNode.visibleTypeAnnotations.removeIf(predicate);

        if (classNode.invisibleTypeAnnotations != null)
            classNode.invisibleTypeAnnotations.removeIf(predicate);

        return modified.get();
    }
}
