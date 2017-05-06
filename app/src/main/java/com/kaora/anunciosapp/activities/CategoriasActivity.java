package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.CategoriasAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.PerfilAnunciante;

import java.util.List;

public class CategoriasActivity extends AppCompatActivity {

    MyDatabaseHelper database;
    List<Categoria> categorias;

    CategoriasAdapter categoriasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        database = MyDatabaseHelper.getInstance(this);
        categorias = database.categoriasPreferidas();

        categoriasAdapter = new CategoriasAdapter(categorias, this);
        ListView lvCategorias = (ListView) findViewById(R.id.lvOfertas);
        lvCategorias.setAdapter(categoriasAdapter);

        lvCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Categoria categoriaSelecionada = (Categoria) view.getTag();
                Intent intent = new Intent(CategoriasActivity.this, AnunciantesActivity.class);
                intent.putExtra("idCategoria", categoriaSelecionada._id);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarNovoAnuncio();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        categorias.clear();
        for (Categoria categoria : database.categoriasPreferidas()) {
            categorias.add(categoria);
        }
        categoriasAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categorias, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_criar_anuncio:
                criarNovoAnuncio();
                break;
            case R.id.action_meus_anuncios:
                mostraActivityMeusAnuncios();
                break;
            case R.id.action_perfis:
                mostraActivityPerfis();
                break;
            case R.id.action_configuracoes:
                mostraActivityPreferencias();
                break;
        }
        return true;
    }

    private void criarNovoAnuncio() {
        int qtdePerfisCadastrados = database.todosPerfis().size();
        if (qtdePerfisCadastrados==0) {
            mostraActivityCriacaoPerfil();
        } else if (qtdePerfisCadastrados==1) {
            mostraActivityNovoAnuncio();
        } else {
            mostraActivitySelecaoPerfil();
        }
    }

    private void mostraActivityCriacaoPerfil() {
        Intent intent = new Intent(this, AvisoPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivitySelecaoPerfil() {
        Intent intent = new Intent(this, SelecionarPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivityPerfis() {
        Intent intent = new Intent(this, SelecionarPerfilActivity.class);
        intent.putExtra("modoEdicao", 1);
        startActivity(intent);
    }

    private void mostraActivityMeusAnuncios() {
        Intent intent = new Intent(this, MeusAnunciosActivity.class);
        startActivity(intent);
    }

    private void mostraActivityNovoAnuncio() {
        Intent intent = new Intent(this, NovoAnuncioActivity.class);
        intent.putExtra("idPerfil", 1L); // id do primeiro perfil
        startActivity(intent);
    }

    private void mostraActivityPreferencias() {
        PerfilAnunciante perfil = database.selecionaPerfil();
        Intent intent = new Intent(this, PreferenciasActivity.class);
        intent.putExtra("idperfil", perfil._id);
        startActivity(intent);
    }

}
