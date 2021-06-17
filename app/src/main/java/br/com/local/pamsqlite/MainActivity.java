package br.com.local.pamsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String NOME_BANCO_DE_DADOS = "dbTI97.db";
    EditText edtNomeProduto, edtPreco;
    Spinner spnCategoriaProduto;

    Button btnAddProduto, btnVisualizaProduto;

    SQLiteDatabase meuBancoDeDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNomeProduto = findViewById(R.id.edtNomeNovoProduto);
        edtPreco = findViewById(R.id.edtPrecoNovoProduto);

        spnCategoriaProduto = findViewById(R.id.spnCategoriaNovo);
        btnAddProduto = findViewById(R.id.btnAddProduto);
        btnVisualizaProduto = findViewById(R.id.btnVisualizaProduto);

        btnAddProduto.setOnClickListener(this);
        btnVisualizaProduto.setOnClickListener(this);

        meuBancoDeDados = openOrCreateDatabase(NOME_BANCO_DE_DADOS, MODE_PRIVATE, null);

        criarTabelaProdutos();

    }

    private boolean verificarEntrada(String nome, String preco){
        if (nome.isEmpty()){
            edtNomeProduto.setError("Insira o nome do produto");
            edtNomeProduto.requestFocus();
            return false;
        }
        if (preco.isEmpty() || Integer.parseInt(preco) <= 0){
            edtPreco.setError("Insira o preço");
            edtPreco.requestFocus();
            return  false;
        }
        return  true;
    }

    private void adicionarProduto(){

        String nomeProd = edtNomeProduto.getText().toString().trim();
        String precoProd = edtPreco.getText().toString().trim();
        String categProd = spnCategoriaProduto.getSelectedItem().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dataEntrada = simpleDateFormat.format(calendar.getTime());

        if (verificarEntrada(nomeProd, precoProd)){
            String insertSQL = "INSERT INTO tbprodutos(" +
                    "nomeProduto," +
                    "categoriaProduto," +
                    "dataEntrada," +
                    "precoProduto)" +
                    "VALUES(?,?,?,?);";

            meuBancoDeDados.execSQL(insertSQL, new String[]{nomeProd, categProd, dataEntrada, precoProd});

            Toast.makeText(this, "Adicionado com Sucesso", Toast.LENGTH_SHORT).show();

            limpaCampos();
        }

    }

    public void limpaCampos(){
        edtNomeProduto.setText("");
        edtPreco.setText("");
        edtNomeProduto.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddProduto:
                adicionarProduto();
                break;
            case R.id.btnVisualizaProduto:
                startActivity(new Intent(getApplicationContext(), Produtos_Activity.class));
                break;
        }
    }

    private void criarTabelaProdutos(){
        meuBancoDeDados.execSQL(
                "CREATE TABLE IF NOT EXISTS tbprodutos(" +
                        "id integer PRIMARY KEY AUTOINCREMENT," +
                        "nomeProduto varchar(100) NOT NULL," +
                        "categoriaProduto varchar(100) NOT NULL," +
                        "dataEntrada datetime NOT NULL," +
                        "precoProduto double NOT NULL" +
                        ");"
        );
    }
}