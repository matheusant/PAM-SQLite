package br.com.local.pamsqlite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProdutosAdapter extends ArrayAdapter<Produtos> {

    Context ctx;
    int listaLayoutRes;
    List<Produtos> produtosList;
    SQLiteDatabase meuBancoDeDados;

    public ProdutosAdapter(Context ctx, int listaLayoutRes, List<Produtos> produtosList, SQLiteDatabase meuBancoDeDados) {
        super(ctx, listaLayoutRes, produtosList);
        this.ctx = ctx;
        this.listaLayoutRes = listaLayoutRes;
        this.produtosList = produtosList;
        this.meuBancoDeDados = meuBancoDeDados;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(listaLayoutRes, null);

        final Produtos produtos = produtosList.get(position);

        TextView txtViewNome, txtViewCategoria, txtViewPreco, txtDataEntrada;

        txtViewNome = view.findViewById(R.id.txtNomeViewProduto);
        txtViewCategoria = view.findViewById(R.id.txtCategoriaViewProduto);
        txtViewPreco = view.findViewById(R.id.txtPrecoViewProduto);
        txtDataEntrada = view.findViewById(R.id.txtDataViewProduto);

        txtViewNome.setText(produtos.getNomeProduto());
        txtViewCategoria.setText(produtos.getCategoriaProduto());
        txtViewPreco.setText(String.valueOf(produtos.getPreco()));
        txtDataEntrada.setText(produtos.getDataEntrada());

        ImageView imvEditar, imvDeletar;

        imvEditar = view.findViewById(R.id.imvEditarViewProduto);
        imvDeletar = view.findViewById(R.id.imvDeletarViewProduto);

        imvEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterarProduto(produtos);
            }
        });

        imvDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Deseja Excluir?");
                builder.setIcon(R.drawable.ic_delete_24);
                builder.setCancelable(false);
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM tbprodutos WHERE id = ?";
                        meuBancoDeDados.execSQL(sql, new Integer[]{produtos.getId()});
                        Toast.makeText(ctx, "Deletado com Sucesso", Toast.LENGTH_SHORT).show();

                    }
                });
                recarregarDados();
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recarregarDados();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    public void alterarProduto(final Produtos produtos){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        LayoutInflater inflater = LayoutInflater.from(ctx);

        View view = inflater.inflate(R.layout.caixa_alterar_produto, null);
        builder.setView(view);

        final EditText edtAlterarProduto = view.findViewById(R.id.edtEditarNomeProduto);
        final EditText edtAlterarPreco = view.findViewById(R.id.edtEditarPrecoProduto);
        final Spinner spnCategoria = view.findViewById(R.id.spnCategoria);

        edtAlterarProduto.setText(produtos.getNomeProduto());
        edtAlterarPreco.setText(String.valueOf(produtos.getPreco()));

        final AlertDialog dialog = builder.create();

        dialog.show();

        view.findViewById(R.id.btnEditarProduto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = edtAlterarProduto.getText().toString().trim();
                String preco = edtAlterarPreco.getText().toString().trim();
                String categoria = spnCategoria.getSelectedItem().toString().trim();

                if (nome.isEmpty()){
                    edtAlterarProduto.setError("Insira o Nome do Produto");
                    edtAlterarProduto.requestFocus();
                    return;
                }
                if (preco.isEmpty()){
                    edtAlterarPreco.setError("Insira o Preço do Produto");
                    edtAlterarPreco.requestFocus();
                    return;
                }
                String sql = "UPDATE tbprodutos SET nomeProduto = ?, categoriaProduto = ?, precoProduto = ? WHERE id = ?";
                meuBancoDeDados.execSQL(sql,
                        new String[]{nome, categoria, preco, String.valueOf(produtos.getId())});
                Toast.makeText(ctx, "Produto Alterado com Sucesso", Toast.LENGTH_SHORT).show();

                recarregarDados();

                dialog.dismiss();
            }
        });
    }

    public void recarregarDados(){
        Cursor cursorProdutos = meuBancoDeDados.rawQuery("SELECT * FROM tbprodutos", null);

        if (cursorProdutos.moveToFirst()){
            produtosList.clear();
            do {
                produtosList.add(new Produtos(
                        cursorProdutos.getInt(0),
                        cursorProdutos.getString(1),
                        cursorProdutos.getString(2),
                        cursorProdutos.getString(3),
                        cursorProdutos.getDouble(4)
                ));
            }while (cursorProdutos.moveToNext());
        }
        cursorProdutos.close();
        notifyDataSetChanged();
    }
}
