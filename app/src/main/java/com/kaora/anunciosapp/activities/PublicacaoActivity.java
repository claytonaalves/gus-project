package com.kaora.anunciosapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Publicacao;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.utils.DateUtils;

import java.text.DateFormat;

public class PublicacaoActivity extends AppCompatActivity {

    private Publicacao publicacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacao);

        publicacao = (Publicacao) getIntent().getSerializableExtra("publicacao");
        atualizaDadosPublicacao(publicacao);
    }

    private void atualizaDadosPublicacao(Publicacao publicacao) {
        TextView tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        TextView tvDescricao = (TextView) findViewById(R.id.tvDescricao);
        TextView tvNomeAnunciante = (TextView) findViewById(R.id.tvNomeAnunciante);
        TextView tvDataPublicacao = (TextView) findViewById(R.id.tvDataPublicacao);
        TextView tvDataValidade = (TextView) findViewById(R.id.tvDataValidade);
        Button btLigarPara = (Button) findViewById(R.id.btLigarPara);

        if (!(publicacao.imagem==null) && (!publicacao.imagem.equals(""))) {
            SimpleDraweeView imagem = (SimpleDraweeView) findViewById(R.id.imagem_publicacao);
            imagem.setImageURI(ApiRestAdapter.PUBLICATIONS_IMAGE_PATH + publicacao.imagem);
        }

        tvTitulo.setText(publicacao.titulo);
        tvDescricao.setText(publicacao.descricao);
        tvNomeAnunciante.setText(publicacao.anunciante.nomeFantasia);
        tvDataPublicacao.setText(DateUtils.textoDataPublicacao(publicacao.dataPublicacao));
        btLigarPara.setText("Ligar para " + publicacao.anunciante.nomeFantasia + "\n" + publicacao.anunciante.telefone);

        Resources res = this.getResources();
        DateFormat df = DateFormat.getDateInstance();
        tvDataValidade.setText(
            String.format(res.getString(R.string.data_validade), df.format(publicacao.dataValidade))
        );
    }

    public void ligarParaAnunciante(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + publicacao.anunciante.telefone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }
}
