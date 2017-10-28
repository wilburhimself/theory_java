package io.retrorock.theory.components;

public class Where {
    private String conditional;
    private String modifier;

    public Where(String conditional, String modifier) {
        this.conditional = conditional;
        this.modifier = modifier;
    }

    public void setConditional(String conditional) {
        this.conditional = conditional;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        if (!this.modifier.equals("")) {
            output.append(this.modifier).append(" ");
        }
        output.append("WHERE ").append(this.conditional).append(" ");
        return output.toString();
    }
}
