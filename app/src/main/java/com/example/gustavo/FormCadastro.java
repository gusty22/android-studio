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

public class FormCadastro extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    private Button btnCadastrar;
    private TextView btnVoltarLogin;

    private boolean isSenhaVisivel = false;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UsuariosApp";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltarLogin = findViewById(R.id.btnVoltarLogin);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        btnCadastrar.setOnClickListener(v -> {
            if (validarCampos()) {
                cadastrarUsuario();
            }
        });

        btnVoltarLogin.setOnClickListener(v -> {
            Intent intent = new Intent(FormCadastro.this, FormLogin.class);
            startActivity(intent);
            finish();
        });

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
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (TextUtils.isEmpty(nome)) {
            edtNome.setError("Informe o seu nome");
            edtNome.requestFocus();
            return false;
        }

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

        if (senha.length() < 6) {
            edtSenha.setError("A senha deve ter ao menos 6 caracteres");
            edtSenha.requestFocus();
            return false;
        }

        if (sharedPreferences.contains(email)) {
            edtEmail.setError("Este email já está cadastrado");
            edtEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(email, nome + ";" + senha);
        editor.apply();

        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(FormCadastro.this, FormLogin.class);
        startActivity(intent);
        finish();
    }
}