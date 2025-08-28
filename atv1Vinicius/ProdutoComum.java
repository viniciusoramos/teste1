public class ProdutoComum extends Produto {
    public ProdutoComum(String nome, double precoBase, double imposto) {
        super(nome, precoBase, imposto);
    }
    @Override
    public boolean isPerecivel() { return false; }
}
