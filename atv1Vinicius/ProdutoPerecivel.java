import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProdutoPerecivel extends Produto {
    private final LocalDate validade; // dd/MM/yyyy

    public ProdutoPerecivel(String nome, double precoBase, double imposto, LocalDate validade) {
        super(nome, precoBase, imposto);
        if (validade == null) throw new IllegalArgumentException("Validade obrigatória para perecíveis.");
        this.validade = validade;
    }

    public LocalDate getValidade() { return validade; }

    public boolean estaVencido(LocalDate referencia) {
        return referencia != null && validade.isBefore(referencia);
    }

    @Override
    public boolean isPerecivel() { return true; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return super.toString() + " - validade: " + validade.format(fmt);
    }
}
