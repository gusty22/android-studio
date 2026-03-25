package com.example.gustavo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TelaPerfil extends AppCompatActivity {

    private TextView txtNomePerfil, txtEmailPerfil;
    private Button btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);

        txtNomePerfil = findViewById(R.id.txtNomePerfil);
        txtEmailPerfil = findViewById(R.id.txtEmailPerfil);
        btnSair = findViewById(R.id.btnSair);

        String nomeUsuario = getIntent().getStringExtra("nomeUsuario");
        String emailUsuario = getIntent().getStringExtra("emailUsuario");

        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            txtNomePerfil.setText(nomeUsuario);
        }

        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            txtEmailPerfil.setText(emailUsuario);
        } else {
            txtEmailPerfil.setText("Erro ao carregar email");
        }

        btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(TelaPerfil.this, FormLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}