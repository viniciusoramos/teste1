import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Pedido {
    /** Quantidade máxima de produtos de um pedido */
    private static final int MAX_PRODUTOS = 10;

    /** Porcentagem de desconto para pagamentos à vista */
    private static final double DESCONTO_PG_A_VISTA = 0.15;

    /** Vetor para armazenar os produtos do pedido */
    private final Produto[] produtos;

    /** Data de criação do pedido */
    private final LocalDate dataPedido;

    /** 1 = à vista; 2 = a prazo (convenção do CSV) */
    private final int formaPagamento;

    /** Quantidade efetiva de produtos inseridos */
    private int qtd;

    /**
     * Cria um pedido informando a data e a forma de pagamento.
     * @param dataPedido data do pedido (não nula)
     * @param formaPagamento 1 = à vista; 2 = a prazo
     */
    public Pedido(LocalDate dataPedido, int formaPagamento) {
        if (dataPedido == null) throw new IllegalArgumentException("dataPedido nula");
        if (formaPagamento != 1 && formaPagamento != 2) throw new IllegalArgumentException("formaPagamento inválida");
        this.dataPedido = dataPedido;
        this.formaPagamento = formaPagamento;
        this.produtos = new Produto[MAX_PRODUTOS];
        this.qtd = 0;
    }

    /**
     * Tenta adicionar um produto ao pedido (respeita o limite MAX_PRODUTOS).
     * @return true se adicionado; false se não houver mais espaço.
     */
    public boolean adicionarProduto(Produto p) {
        if (p == null) return false;
        if (qtd >= MAX_PRODUTOS) return false;
        produtos[qtd++] = p;
        return true;
    }

    public LocalDate getDataPedido() { return dataPedido; }
    public int getFormaPagamento() { return formaPagamento; }
    public int getQuantidadeItens() { return qtd; }
    public Produto[] getProdutos() { return Arrays.copyOf(produtos, qtd); }

    /** Soma (preço com imposto) de todos os itens. */
    public double calcularSubtotal() {
        double total = 0.0;
        for (int i = 0; i < qtd; i++) {
            total += produtos[i].getPrecoComImposto();
        }
        return total;
    }

    /** Aplica desconto se pagamento à vista (formaPagamento == 1). */
    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        if (formaPagamento == 1) {
            return subtotal * (1.0 - DESCONTO_PG_A_VISTA);
        }
        return subtotal;
    }

    /** Retorna a data no formato dd/MM/yyyy. */
    public String getDataFormatada() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dataPedido.format(fmt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data: ").append(getDataFormatada()).append("\n");
        sb.append("Pagamento: ").append(formaPagamento == 1 ? "À vista (15% desc.)" : "A prazo").append("\n");
        sb.append("Itens (").append(qtd).append("):\n");
        for (int i = 0; i < qtd; i++) {
            Produto p = produtos[i];
            sb.append("  - ").append(p.toString()).append(String.format(" -> R$ %.2f", p.getPrecoComImposto())).append("\n");
        }
        sb.append(String.format("Subtotal: R$ %.2f\n", calcularSubtotal()));
        sb.append(String.format("Total:    R$ %.2f", calcularTotal()));
        return sb.toString();
    }

    /**
     * Igualdade de pedidos: caso possuam a mesma data.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pedido other = (Pedido) obj;
        return dataPedido.equals(other.dataPedido);
    }

    @Override
    public int hashCode() {
        return dataPedido.hashCode();
    }
}
