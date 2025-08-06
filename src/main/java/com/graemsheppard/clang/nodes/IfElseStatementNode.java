package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.fragments.IfElseFragment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class IfElseStatementNode extends StatementNode {

    @Getter
    private final List<IfElseFragment> parts;


    public IfElseStatementNode() {
        parts = new ArrayList<>();
    }

    public List<String> getScopes(int count) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            var fragment = parts.get(i);
            res.add(switch(fragment.getType()) {
                case IF -> "if_" + count;
                case IF_ELSE -> "if_" + count + "_elif_" + i;
                case ELSE -> "if_" + count + "_else";
            });
        }
        res.add("endif_" + count);
        return res;
    }

}
