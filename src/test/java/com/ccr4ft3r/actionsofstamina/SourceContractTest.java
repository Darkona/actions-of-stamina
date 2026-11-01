package com.ccr4ft3r.actionsofstamina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceContractTest {

    private static final List<String> FOLDERS = List.of("src/main/java", "src/test/java", "src/gametest/java");

    private static boolean hasComment(String source) {
        int n = source.length();
        for (int i = 0; i < n; i++) {
            char c = source.charAt(i);
            if (c == '/' && i + 1 < n && (source.charAt(i + 1) == '/' || source.charAt(i + 1) == '*')) return true;
            if (c == '"' || c == '\'') {
                int end = i + 1;
                while (end < n && source.charAt(end) != c && source.charAt(end) != '\n') end += source.charAt(end) == '\\' ? 2 : 1;
                i = end;
            }
        }
        return false;
    }

    @Test
    void noCommentsInThePublicRepository() throws IOException {
        List<String> found = new ArrayList<>();
        for (String folder : FOLDERS) {
            try (Stream<Path> files = Files.walk(Path.of(folder))) {
                for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                    if (hasComment(Files.readString(file))) found.add(file.toString());
                }
            }
        }
        assertEquals(List.of(), found);
    }
}
