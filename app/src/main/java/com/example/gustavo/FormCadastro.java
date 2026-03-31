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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    private Button btnCadastrar;
    private TextView btnVoltarLogin;

    private boolean isSenhaVisivel = false;

    // VARIÁVEIS DO FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        // INICIANDO O FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
            edtEmail.setError("Informe um e-mail válido");
            edtEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(senha)) {
            edtSenha.setError("Informe a palavra-passe");
            edtSenha.requestFocus();
            return false;
        }

        if (senha.length() < 6) {
            edtSenha.setError("A palavra-passe deve ter ao menos 6 caracteres");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }

    private void cadastrarUsuario() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        // COMANDO PARA SALVAR NO FIREBASE
        mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // CORREÇÃO: Obter o ID diretamente do resultado da criação da conta (Mais seguro)
                String usuarioID = task.getResult().getUser().getUid();

                // Envia o ID para a nossa função que guarda o nome no Firestore
                salvarDadosUsuario(usuarioID);

                Toast.makeText(this, "Registo realizado com sucesso!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                startActivity(intent);
                finish();
            } else {
                String erro;
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    erro = "Digite uma palavra-passe com no mínimo 6 caracteres.";
                } catch (FirebaseAuthUserCollisionException e) {
                    erro = "Esta conta de e-mail já está registada.";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    erro = "E-mail inválido.";
                } catch (Exception e) {
                    erro = "Erro ao registar utilizador: " + e.getMessage();
                }
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    // A função recebe o ID gerado pelo Firebase para gravar no Firestore
    private void salvarDadosUsuario(String usuarioID) {
        String nome = edtNome.getText().toString().trim();

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", nome);

        // Salva o nome na Base de Dados Firestore na coleção (pasta) "Usuarios"
        db.collection("Usuarios").document(usuarioID).set(usuario);
    }
}