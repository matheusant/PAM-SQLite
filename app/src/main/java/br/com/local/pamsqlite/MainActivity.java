package br.com.local.pamsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String NOME_BANCO_DE_DADOS = "dbTI97.db";
    private static String basePath = android.os.Environment.getExternalStorageDirectory().toString();
    private static final String FILE_SQL = "file.sql";
    EditText edtNomeProduto, edtPreco;
    Spinner spnCategoriaProduto;
    Boolean success = false;

    Button btnAddProduto, btnVisualizaProduto, btnInserirDados, btnEscrever;

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
        btnInserirDados = findViewById(R.id.btnInserirDados);
        btnEscrever = findViewById(R.id.btnEscrever);

        btnAddProduto.setOnClickListener(this);
        btnVisualizaProduto.setOnClickListener(this);
        btnInserirDados.setOnClickListener(this);
        btnEscrever.setOnClickListener(this);

        meuBancoDeDados = openOrCreateDatabase(NOME_BANCO_DE_DADOS, MODE_PRIVATE, null);

        criarTabelaProdutos();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        // setStoragePath();

        criarPasta();
    }

    private boolean verificarEntrada(String nome, String preco) {
        if (nome.isEmpty()) {
            edtNomeProduto.setError("Insira o nome do produto");
            edtNomeProduto.requestFocus();
            return false;
        }
        if (preco.isEmpty() || Integer.parseInt(preco) <= 0) {
            edtPreco.setError("Insira o preço");
            edtPreco.requestFocus();
            return false;
        }
        return true;
    }

    private void adicionarProduto() {

        String nomeProd = edtNomeProduto.getText().toString().trim();
        String precoProd = edtPreco.getText().toString().trim();
        String categProd = spnCategoriaProduto.getSelectedItem().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dataEntrada = simpleDateFormat.format(calendar.getTime());

        if (verificarEntrada(nomeProd, precoProd)) {
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

    public void limpaCampos() {
        edtNomeProduto.setText("");
        edtPreco.setText("");
        edtNomeProduto.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddProduto:
                adicionarProduto();
                break;
            case R.id.btnVisualizaProduto:
                startActivity(new Intent(getApplicationContext(), Produtos_Activity.class));
                break;
            case R.id.btnInserirDados:
                try {
                    int insertCount = insertFromFile();
                    Toast.makeText(this, "Total de itens inseridos: "+ String.valueOf(insertCount), Toast.LENGTH_SHORT).show();
                    success = true;
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnEscrever:

                try {
                    FileWriter fw = new FileWriter(basePath+"/PASTA_SQL/teste.txt", success);
                    PrintWriter pw = new PrintWriter(fw);
                    pw.println("testando!!!!");
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void criarTabelaProdutos() {
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

    public int insertFromFile() throws IOException {
        int result = 0;

        File file = new File(basePath, "/PASTA_SQL/insert_sql.sql");
        FileInputStream insertStream = new FileInputStream(file);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertStream));

        while (insertReader.ready()) {
            String insertStmt = insertReader.readLine();
            this.meuBancoDeDados.execSQL(insertStmt);
            result++;
        }
        insertReader.close();

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && (grantResults.length > 0)  && (grantResults[0] ==
                PackageManager.PERMISSION_GRANTED)){
            criarPasta();
        }
    }

    public void criarPasta(){
        File folder = new File(basePath + "/PASTA_SQL");
        if (!folder.exists()){
            folder.mkdir();
        }
    }
}
