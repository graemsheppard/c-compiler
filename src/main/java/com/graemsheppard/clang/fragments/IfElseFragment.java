package com.graemsheppard.clang.fragments;

import com.graemsheppard.clang.enums.ControlType;
import com.graemsheppard.clang.nodes.ExpressionNode;
import com.graemsheppard.clang.nodes.StatementNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class IfElseFragment {

        @Getter
        private final ControlType type;

        @Getter
        private final ExpressionNode condition;

        @Getter
        private final List<StatementNode> body;

        @Getter
        private final String scope;

        public IfElseFragment(ControlType type, ExpressionNode condition, String scope) {
            this.type = type;
            this.condition = condition;
            this.body = new ArrayList<>();
            this.scope = scope;
        }

        public IfElseFragment(ControlType type, String scope) {
            this.type = type;
            this.body = new ArrayList<>();
            this.condition = null;
            this.scope = scope;
        }
}