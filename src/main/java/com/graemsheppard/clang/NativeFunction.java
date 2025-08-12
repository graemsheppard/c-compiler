package com.graemsheppard.clang;

import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class NativeFunction {

    @Getter
    private final String name;

    public NativeFunction(String functionName) {
        name = functionName;
    }

    @Override
    public String toString() {
        var path = Path.of("nativeFunctions", name + ".asm").toString();
        try (var stream = Main.class.getClassLoader().getResourceAsStream(path) ) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Could not find file: " + path);
        }
    }
}
