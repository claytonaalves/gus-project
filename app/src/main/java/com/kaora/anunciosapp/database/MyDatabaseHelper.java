package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.models.Preferencia;
import com.kaora.anunciosapp.models.Publicacao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper instance = null;

    public static final String DATABASE_NAME = "anuncios_database";

    private static final int DATABASE_VERSION = 1;

    private static final String TABELA_CATEGORIA = "" +
            "CREATE TABLE categoria ( " +
            "id_categoria INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "id_cidade INTEGER NOT NULL, " +
            "descricao TEXT NOT NULL, " +
            "qtde_anunciantes INTEGER, " +
            "imagem TEXT)";

    private static final String TABELA_PREFERENCIA = "" +
            "CREATE TABLE preferencia ( " +
            "id_categoria INTEGER, " +
            "descricao TEXT NOT NULL)";

    private static final String TABELA_ANUNCIANTE = "" +
            "CREATE TABLE anunciante ( " +
            "guid_anunciante TEXT NOT NULL PRIMARY KEY, " +
            "nome_fantasia TEXT, " +
            "telefone TEXT, " +
            "endereco TEXT, " +
            "id_categoria INTEGER)";

    // Armazena os perfis de anunciante criados no aparelho
    private static final String TABELA_PERFIL_ANUNCIANTE = "" +
            "CREATE TABLE perfil_anunciante ( " +
            "guid_anunciante TEXT NOT NULL PRIMARY KEY, " +
            "nome_fantasia TEXT, " +
            "telefone TEXT, " +
            "celular TEXT, " +
            "email TEXT, " +
            "endereco TEXT, " +
            "numero TEXT, " +
            "bairro TEXT, " +
            "id_cidade INTEGER, " +
            "id_categoria INTEGER, " +
            "publicado INTEGER )";

    private static final String TABELA_PUBLICACOES = "" +
            "CREATE TABLE publicacao ( " +
            "guid_publicacao TEXT NOT NULL PRIMARY KEY, " +
            "guid_anunciante TEXT NOT NULL, " +
            "id_categoria INTEGER NOT NULL, " +
            "titulo TEXT, " +
            "descricao TEXT, " +
            "data_publicacao INTEGER NOT NULL, " +
            "data_validade INTEGER NOT NULL, " +
            "imagem TEXT, " +
            "arquivado INTEGER NOT NULL DEFAULT 0, " +
            "publicado INTEGER )";

    private MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MyDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABELA_CATEGORIA);
        db.execSQL(TABELA_PREFERENCIA);
        db.execSQL(TABELA_ANUNCIANTE);
        db.execSQL(TABELA_PERFIL_ANUNCIANTE);
        db.execSQL(TABELA_PUBLICACOES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void atualizaCategorias(List<Categoria> categorias) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM categoria");
        for (Categoria categoria : categorias) {
            ContentValues values = new ContentValues();
            values.put("_id", categoria.idCategoria);
            values.put("descricao", categoria.descricao);
            values.put("qtde_anunciantes", categoria.qtdeAnunciantes);
            values.put("imagem", categoria.imagem);
            database.insert("categoria", null, values);
        }
    }

    // ========================================================================
    // Perfis
    // ========================================================================

    public void salvaPerfil(PerfilAnunciante perfil) {
        perfil.guidAnunciante = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put("guid_anunciante", perfil.guidAnunciante);
        values.put("nome_fantasia", perfil.nomeFantasia);
        values.put("telefone", perfil.telefone);
        values.put("celular", perfil.celular);
        values.put("email", perfil.email);
        values.put("endereco", perfil.endereco);
        values.put("numero", perfil.numero);
        values.put("bairro", perfil.bairro);
        values.put("id_cidade", perfil.idCidade);
        values.put("id_categoria", perfil.idCategoria);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("perfil_anunciante", null, values);
    }

    public List<PerfilAnunciante> todosPerfis() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM perfil_anunciante", null);
        List<PerfilAnunciante> perfis = new ArrayList<>();
        while (cursor.moveToNext()) {
            PerfilAnunciante perfil = new PerfilAnunciante();
            perfil.guidAnunciante = cursor.getString(cursor.getColumnIndex("guid_anunciante"));
            perfil.nomeFantasia = cursor.getString(cursor.getColumnIndex("nome_fantasia"));
            perfis.add(perfil);
        }
        cursor.close();
        return perfis;
    }

    public PerfilAnunciante selecionaPerfil(String guidAnunciante) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT guid_anunciante, nome_fantasia, id_categoria FROM perfil_anunciante WHERE guid_anunciante='" + guidAnunciante + "'", null);
        cursor.moveToNext();
        PerfilAnunciante perfil = new PerfilAnunciante();
        perfil.guidAnunciante = cursor.getString(0);
        perfil.nomeFantasia = cursor.getString(1);
        perfil.idCategoria = cursor.getInt(2);
        cursor.close();
        return perfil;
    }

    public void marcaPerfilComoPublicado(String guidAnunciante) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE perfil_anunciante SET publicado=1 WHERE guid_anunciante='" + guidAnunciante + "'");
    }

    // ========================================================================
    // Preferências
    // ========================================================================

    public void salvaPreferencia(Preferencia preferencia) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM preferencia WHERE id_categoria=" + preferencia.idCategoria);
        if (preferencia.selecionanda) {
            ContentValues values = new ContentValues();
            values.put("id_categoria", preferencia.idCategoria);
            values.put("descricao", preferencia.descricao);
            database.insert("preferencia", null, values);
        }
    }

    public boolean preferenciasDefinidas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM preferencia", null);
        cursor.moveToFirst();
        boolean result = cursor.getInt(0) > 0;
        cursor.close();
        return result;
    }

    public List<Preferencia> preferenciasSelecionadasPorCidade(int idCidade) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_categoria, descricao FROM preferencia", null);
        List<Preferencia> preferencias = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preferencia preferencia = new Preferencia(cursor.getInt(0), cursor.getString(1));
            preferencia.selecionanda = true;
            preferencias.add(preferencia);
        }
        cursor.close();
        return preferencias;
    }

    public List<Preferencia> peferenciasSelecionadas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_categoria, descricao FROM preferencia", null);
        List<Preferencia> preferencias = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preferencia preferencia = new Preferencia(cursor.getInt(0), cursor.getString(1));
            preferencia.selecionanda = true;
            preferencias.add(preferencia);
        }
        cursor.close();
        return preferencias;
    }

    // ========================================================================
    // Publicações
    // ========================================================================

    public void salvaPublicacao(Publicacao publicacao) {
        publicacao.guidPublicacao = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put("guid_publicacao", publicacao.guidPublicacao);
        values.put("guid_anunciante", publicacao.guidAnunciante);
        values.put("titulo", publicacao.titulo);
        values.put("descricao", publicacao.descricao);
        values.put("id_categoria", publicacao.idCategoria);
        values.put("data_validade", publicacao.dataFormatada());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("publicacao", null, values);
    }

    // Retorna a lista de publicações não arquivadas
    public List<Publicacao> publicacoesSalvas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM publicacao WHERE arquivado=0", null);
        List<Publicacao> publicacoes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Publicacao publicacao = new Publicacao();
            publicacao.guidPublicacao = cursor.getString(cursor.getColumnIndex("guid_publicacao"));
            publicacao.guidAnunciante = cursor.getString(cursor.getColumnIndex("guid_anunciante"));
            publicacao.idCategoria = cursor.getInt(cursor.getColumnIndex("id_categoria"));
            publicacao.titulo = cursor.getString(cursor.getColumnIndex("titulo"));
            publicacao.descricao = cursor.getString(cursor.getColumnIndex("descricao"));
            publicacao.dataPublicacao = cursor.getLong(cursor.getColumnIndex("data_publicacao"));
            publicacao.dataValidade = cursor.getLong(cursor.getColumnIndex("data_validade"));
            publicacao.imagem = cursor.getString(cursor.getColumnIndex("imagem"));
            publicacoes.add(publicacao);
        }
        cursor.close();
        return publicacoes;
    }

    public void arquivaPublicacao(Publicacao publicacao) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE publicacao SET arquivado=1 WHERE guid_publicacao='" + publicacao.guidPublicacao + "'");
    }

    public void marcaAnuncioComoPublicado(String guidAnuncio) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE publicacao SET publicado=1 WHERE guid_publicacao='" + guidAnuncio + "'");
    }

    public void salvaPublicacoes(List<Publicacao> publicacoes) {
        SQLiteDatabase db = getWritableDatabase();
        for (Publicacao publicacao : publicacoes) {
            ContentValues values = new ContentValues();
            values.put("guid_publicacao", publicacao.guidPublicacao);
            values.put("guid_anunciante", publicacao.guidAnunciante);
            values.put("id_categoria", publicacao.idCategoria);
            values.put("titulo", publicacao.titulo);
            values.put("descricao", publicacao.descricao);
            values.put("data_publicacao", publicacao.dataPublicacao);
            values.put("data_validade", publicacao.dataValidade);
            values.put("imagem", publicacao.imagem);
            db.insert("publicacao", null, values);
        }
    }
}
