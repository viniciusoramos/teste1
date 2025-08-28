import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class App {

    static final Charset UTF8 = Charset.forName("UTF-8");
    static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in, UTF8);
        try {
            // caminhos padrão (podem ser alterados por args)
            String arqProdutos = args.length > 0 ? args[0] : "dadosProdutos.csv";
            String arqPedidos  = args.length > 1 ? args[1] : "dadosPedidos.csv";

            Map<String, Produto> catalogo = carregarProdutos(arqProdutos);
            Pedido[] pedidos = carregarPedidos(arqPedidos, catalogo);

            System.out.println("Arquivo de produtos: " + arqProdutos);
            System.out.println("Arquivo de pedidos:  " + arqPedidos);
            System.out.println("Total de produtos no catálogo: " + catalogo.size());
            System.out.println("Total de pedidos carregados:   " + pedidos.length);
            System.out.println();

            System.out.print("Informe uma data para busca (dd/MM/yyyy): ");
            String dataStr = teclado.nextLine().trim();
            LocalDate dataBusca = LocalDate.parse(dataStr, DF);

            listarPedidosPorData(pedidos, dataBusca);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            teclado.close();
        }
    }

    /** Lê o arquivo de produtos; primeira linha é quantidade. Demais: tipo;nome;preco;imposto;[validade] */
    public static Map<String, Produto> carregarProdutos(String nomeArquivo) throws IOException {
        Map<String, Produto> mapa = new LinkedHashMap<>();
        List<String> linhas = Files.readAllLines(Paths.get(nomeArquivo), UTF8);
        if (linhas.isEmpty()) return mapa;

        int qtdDeclarada = parseIntSafe(linhas.get(0).trim());
        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();
            if (linha.isEmpty()) continue;
            String[] partes = linha.split(";");
            if (partes.length < 4) continue;

            int tipo = parseIntSafe(partes[0]);
            String nome = partes[1].trim();
            double preco = Double.parseDouble(partes[2].replace(",", "."));
            double imposto = Double.parseDouble(partes[3].replace(",", "."));

            Produto prod;
            if (tipo == 2) {
                if (partes.length < 5) throw new IllegalArgumentException("Produto perecível sem validade: " + nome);
                LocalDate validade = LocalDate.parse(partes[4].trim(), DF);
                prod = new ProdutoPerecivel(nome, preco, imposto, validade);
            } else {
                prod = new ProdutoComum(nome, preco, imposto);
            }
            mapa.put(nome, prod);
        }
        // Opcional: validar qtdDeclarada
        if (qtdDeclarada != mapa.size()) {
            System.err.println("[Aviso] Quantidade declarada de produtos (" + qtdDeclarada + ") difere do lido (" + mapa.size() + ").");
        }
        return mapa;
    }

    /** Lê pedidos; primeira linha é quantidade. Demais: data;forma;nome1;nome2;... */
    public static Pedido[] carregarPedidos(String nomeArquivo, Map<String, Produto> catalogo) throws IOException {
        List<String> linhas = Files.readAllLines(Paths.get(nomeArquivo), UTF8);
        List<Pedido> lista = new ArrayList<>();
        if (linhas.isEmpty()) return new Pedido[0];

        int qtdDeclarada = parseIntSafe(linhas.get(0).trim());
        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();
            if (linha.isEmpty()) continue;
            String[] partes = linha.split(";");
            if (partes.length < 2) continue;

            LocalDate data = LocalDate.parse(partes[0].trim(), DF);
            int forma = parseIntSafe(partes[1].trim());

            Pedido p = new Pedido(data, forma);

            for (int j = 2; j < partes.length; j++) {
                String nomeProd = partes[j].trim();
                Produto prod = catalogo.get(nomeProd);
                if (prod == null) {
                    System.err.println("[Aviso] Produto não encontrado no catálogo: " + nomeProd + " (pedido " + p.getDataFormatada() + ")");
                    continue;
                }
                boolean ok = p.adicionarProduto(prod);
                if (!ok) {
                    System.err.println("[Aviso] Pedido atingiu o limite de itens: " + p.getDataFormatada());
                    break;
                }
            }
            lista.add(p);
        }
        if (qtdDeclarada != lista.size()) {
            System.err.println("[Aviso] Quantidade declarada de pedidos (" + qtdDeclarada + ") difere do lido (" + lista.size() + ").");
        }
        return lista.toArray(new Pedido[0]);
    }

    /** Lista e imprime os pedidos realizados na data informada. */
    public static void listarPedidosPorData(Pedido[] pedidos, LocalDate data) {
        System.out.println();
        System.out.println("Pedidos em " + data.format(DF) + ":");
        boolean encontrou = false;
        for (Pedido p : pedidos) {
            if (p.getDataPedido().equals(data)) {
                System.out.println("--------------------------------");
                System.out.println(p.toString());
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("(nenhum pedido encontrado para a data informada)");
        }
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
