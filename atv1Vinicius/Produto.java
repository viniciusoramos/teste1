public abstract class Produto {
    private final String nome;
    private final double precoBase;
    /**
     * Imposto como fração (ex.: 0.15 = 15%)
     */
    private final double imposto;

    protected Produto(String nome, double precoBase, double imposto) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do produto vazio.");
        if (precoBase < 0) throw new IllegalArgumentException("Preço não pode ser negativo.");
        if (imposto < 0) throw new IllegalArgumentException("Imposto não pode ser negativo.");
        this.nome = nome.trim();
        this.precoBase = precoBase;
        this.imposto = imposto;
    }

    public String getNome() { return nome; }
    public double getPrecoBase() { return precoBase; }
    public double getImposto() { return imposto; }

    /** Preço com imposto aplicado. */
    public double getPrecoComImposto() {
        return precoBase * (1.0 + imposto);
    }

    /** Flag para identificar se o produto é perecível. */
    public abstract boolean isPerecivel();

    @Override
    public String toString() {
        return String.format("%s (R$ %.2f, imposto %.0f%%)", nome, precoBase, imposto * 100.0);
    }
}
