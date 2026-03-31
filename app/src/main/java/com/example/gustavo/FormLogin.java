package com.example.gustavo;

import android.annotation.SuppressLint;
import android.content.Intent;
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

// IMPORTAÇÕES DO FIREBASE
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FormLogin extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnLogin;
    private TextView btnCriarConta;
    private boolean isSenhaVisivel = false;

    // VARIÁVEIS DO FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCriarConta = findViewById(R.id.btnCriarConta);

        // INICIANDO O FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            if (validarCampos()) {
                tentarLogin();
            }
        });

        btnCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(FormLogin.this, FormCadastro.class);
            startActivity(intent);
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
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Informe um email válido");
            edtEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(senha)) {
            edtSenha.setError("Informe a palavra-passe");
            edtSenha.requestFocus();
            return false;
        }
        return true;
    }

    private void tentarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senhaDigitada = edtSenha.getText().toString();

        // COMANDO PARA LOGIN NO FIREBASE
        mAuth.signInWithEmailAndPassword(email, senhaDigitada).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // CORREÇÃO: Obter o ID diretamente da tarefa de login (Mais seguro)
                String usuarioID = task.getResult().getUser().getUid();

                // Busca o nome da pessoa no Banco de Dados
                db.collection("Usuarios").document(usuarioID).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String nome = documentSnapshot.getString("nome");

                            // Manda para a TelaPerfil com os dados
                            Intent intent = new Intent(FormLogin.this, TelaPerfil.class);
                            intent.putExtra("nomeUsuario", nome);
                            intent.putExtra("emailUsuario", email);
                            startActivity(intent);
                            finish();
                        });

            } else {
                Toast.makeText(this, "E-mail ou palavra-passe incorretos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}