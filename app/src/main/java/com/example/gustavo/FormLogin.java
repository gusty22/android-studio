package com.example.gustavo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FormLogin extends AppCompatActivity {

    // DECLARAÇÃO DAS VARIÁVEIS (O erro indicava que isto estava em falta)
    private EditText edtEmail, edtSenha;
    private Button btnLogin;
    private TextView btnCriarConta;
    private boolean isSenhaVisivel = false;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UsuariosApp";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        // LIGAÇÃO COM OS IDs DO XML
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCriarConta = findViewById(R.id.btnCriarConta);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> {
            if (validarCampos()) {
                tentarLogin();
            }
        });

        btnCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(FormLogin.this, FormCadastro.class);
            startActivity(intent);
        });

        // Lógica para o ícone do olho na palavra-passe
        edtSenha.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtSenha.getRight() - edtSenha.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - edtSenha.getPaddingRight())) {
                    if (isSenhaVisivel) {
                        edtSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edtSenha.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_eye_off, 0);
                        isSenhaVisivel = false;
                    } else {
                        edtSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edtSenha.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_eye, 0);
                        isSenhaVisivel = true;
                    }
                    edtSenha.setSelection(edtSenha.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private boolean validarCampos() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Informe um email válido");
            edtEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(senha)) {
            edtSenha.setError("Informe a senha");
            edtSenha.requestFocus();
            return false;
        }
        return true;
    }

    private void tentarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senhaDigitada = edtSenha.getText().toString();

        if (!sharedPreferences.contains(email)) {
            Toast.makeText(this, "Utilizador não registado", Toast.LENGTH_SHORT).show();
            return;
        }

        String dadosUsuario = sharedPreferences.getString(email, "");
        String[] partes = dadosUsuario.split(";");
        String senhaArmazenada = partes[1];

        if (senhaDigitada.equals(senhaArmazenada)) {
            Intent intent = new Intent(FormLogin.this, TelaPerfil.class);
            intent.putExtra("nomeUsuario", partes[0]);
            intent.putExtra("emailUsuario", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show();
        }
    }
}