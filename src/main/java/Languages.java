import java.util.HashSet;
import java.util.Set;

// This is a Java class with wildcards
public class Languages {
    Set<?> contents() {
        Set<String> languages = new HashSet<>();
        languages.add("Scala");
        languages.add("Haskell");
        languages.add("Closure");
        return languages;
    }
}
