package com.graemsheppard.clang;

import com.graemsheppard.clang.enums.DataType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTable {

    private final HashMap<String, List<Symbol>> table;

    public SymbolTable() {
        table = new HashMap<>();
        add("itoa", DataType.INTEGER, "_global_", "");
        add("print", DataType.VOID, "_global_", "");
        add("exit", DataType.VOID, "_global_", "");
    }

    public Symbol get(String symbol, String scope) {
        var symbols = table.get(symbol);
        if (symbols == null) return null;
        if (symbols.size() == 1) return symbols.get(0);
        return symbols.stream().filter(s -> s.scope.equals(scope)).findAny().get();
    }

    public void add(String name, DataType type, String scope, String outerScopes) {
        var symbol = new Symbol(name, type, scope, outerScopes);
        if (!table.containsKey(name)) {
            var list = new ArrayList<Symbol>(1);
            list.add(symbol);
            table.put(name, list);
        } else {
            var list = table.get(name);
            if (list.stream().anyMatch(s -> s.scope.equals(symbol.scope) || s.outerScopes.contains(symbol.scope) || symbol.outerScopes.contains(s.scope))) {
                throw new RuntimeException("Variable has already been declared.");
            }
            list.add(symbol);
        }
    }

    public static class Symbol {

        @Getter
        private final String name;

        @Getter
        private final DataType type;

        @Getter
        private final String scope;

        @Getter
        private final String outerScopes;

        public Symbol(String name, DataType type, String scope, String outerScopes) {
            this.name = name;
            this.type = type;
            this.scope = scope;
            this.outerScopes = outerScopes;
        }


    }
}
