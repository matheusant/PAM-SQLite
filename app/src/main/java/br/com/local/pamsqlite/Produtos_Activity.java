package br.com.local.pamsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Produtos_Activity extends AppCompatActivity {

    List<Produtos> produtos;
    ProdutosAdapter produtosAdapter;
    SQLiteDatabase meuBancoDeDados;
    ListView listViewProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produtos_layout);

        listViewProdutos = findViewById(R.id.listarProdutosView);

        produtos = new ArrayList<>();

        meuBancoDeDados = openOrCreateDatabase(MainActivity.NOME_BANCO_DE_DADOS, MODE_PRIVATE, null);

        visualizarProdutos();

    }

    private void visualizarProdutos(){
        Cursor cursorProdutos = meuBancoDeDados.rawQuery("SELECT * FROM tbprodutos", null);

        if (cursorProdutos.moveToFirst()){
            do {
                produtos.add(new Produtos(
                        cursorProdutos.getInt(0),
                        cursorProdutos.getString(1),
                        cursorProdutos.getString(2),
                        cursorProdutos.getString(3),
                        cursorProdutos.getDouble(4)
                ));
            }while (cursorProdutos.moveToNext());
        }
        cursorProdutos.close();

        produtosAdapter = new ProdutosAdapter(this, R.layout.list_view_produtos, produtos, meuBancoDeDados);

        listViewProdutos.setAdapter(produtosAdapter);
    }
}