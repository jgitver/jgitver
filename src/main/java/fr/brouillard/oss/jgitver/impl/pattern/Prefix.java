package fr.brouillard.oss.jgitver.impl.pattern;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Prefix implements Function<Optional<String>, Optional<String>> {
    private final Mode mode;
    private final Supplier<String> prefixProducer;

    Prefix(Mode mode, String prefix) {
        this(mode, () -> prefix);
    }

    Prefix(Mode mode, Supplier<String> prefixProducer) {
        this.mode = mode;
        this.prefixProducer = prefixProducer;
    }

    @Override
    public Optional<String> apply(Optional<String> s) {
        if (Mode.OPTIONAL.equals(this.mode) && !s.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(prefixProducer.get() + s.get());
    }
}
