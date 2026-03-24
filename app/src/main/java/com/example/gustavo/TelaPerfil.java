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

        // Recebe os dados que enviamos do FormLogin.java
        String nomeUsuario = getIntent().getStringExtra("nomeUsuario");
        String emailUsuario = getIntent().getStringExtra("emailUsuario");

        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            txtNomePerfil.setText(nomeUsuario);
        }

        // Pega o Email cadastrado e atualiza a tela
        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            txtEmailPerfil.setText(emailUsuario);
        } else {
            // Só vai cair aqui se der algum erro muito estranho no sistema
            txtEmailPerfil.setText("Erro ao carregar email");
        }

        // Ação do Botão de Sair que encerra a sessão
        btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(TelaPerfil.this, FormLogin.class);
            // Limpa as telas anteriores para o usuário não voltar clicando no botão "Voltar" do celular
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}