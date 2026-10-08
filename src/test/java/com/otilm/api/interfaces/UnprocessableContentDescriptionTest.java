package com.otilm.api.interfaces;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Every documented 422 uses RFC 9110's reason phrase, "Unprocessable Content", which replaced RFC 4918's. */
class UnprocessableContentDescriptionTest {

    private static final Path MAIN_SOURCES = Path.of("src/main/java");

    private static final Pattern RFC_4918_PHRASE = Pattern.compile("Unprocessable Entity", Pattern.CASE_INSENSITIVE);

    @Test
    void every422IsDescribedWithTheRfc9110ReasonPhrase() throws IOException {
        try (Stream<Path> tree = Files.walk(MAIN_SOURCES)) {
            List<Path> spellingTheOldPhrase = tree
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(UnprocessableContentDescriptionTest::spellsTheOldPhrase)
                    .sorted()
                    .toList();
            assertEquals(List.of(), spellingTheOldPhrase, "describe a 422 as \"Unprocessable Content\"");
        }
    }

    private static boolean spellsTheOldPhrase(Path source) {
        try {
            return RFC_4918_PHRASE.matcher(Files.readString(source)).find();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
