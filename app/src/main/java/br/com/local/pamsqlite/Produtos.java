package br.com.local.pamsqlite;

public class Produtos {

    private int id;
    private String nomeProduto, categoriaProduto, dataEntrada ;
    private double preco;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getCategoriaProduto() {
        return categoriaProduto;
    }

    public void setCategoriaProduto(String categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Produtos(int id, String nomeProduto, String categoriaProduto, String dataEntrada, double preco) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.categoriaProduto = categoriaProduto;
        this.dataEntrada = dataEntrada;
        this.preco = preco;
    }
}
