package com.rinko.practicafinalstewardle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;

    private UserRepository userRepository;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Obtiene las preferencias y la configuración de las preferencias para el idioma y modo oscuro
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = this.getResources().getConfiguration();
        if(preferences.getBoolean("dark_mode", false)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (!preferences.getString("language", "es").equals(config.getLocales().get(0).getLanguage())){
            Locale locale = new Locale(preferences.getString("language", "es"));
            Locale.setDefault(locale);
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            recreate();
        }
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        //Abrimos el repositorio de usuarios
        userRepository = new UserRepository(this);
        userRepository.open();

        //Lógica botón login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        //Lógica botón registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userRepository.close();
    }

    //Este método se usa para loguear a los usuarios cuando ya están registrados
    private void loginUser() {
        String username = editTextUsername.getText().toString();
        //Aqui se hace uso de la clase HashPassword para hacer un uso seguro de las contraseñas
        String password = HashPassword.sha256(editTextPassword.getText().toString());

        if (userRepository.checkUser(username, password)) {
            // Inicio de sesión exitoso
            Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            //Guardamos el usuario actual en el archivo de preferencias (se usará para obtener estadísticas, ranking...)
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("UsuarioActual", username);
            editor.apply();

            // Cambia a la pantalla de juego
            Intent intent = new Intent(LoginActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    //Este método se usa para registrar usuarios en la base de datos
    private void registerUser() {
        String inputUsername = editTextUsername.getText().toString();
        //Aqui se hace uso de la clase HashPassword para cifrar las contraseñas de los usuarios que se registran
        String inputPassword = HashPassword.sha256(editTextPassword.getText().toString());

        // Verificar si el usuario ya existe en la base de datos
        if (!userRepository.checkUser(inputUsername, inputPassword)) {
            // Si no existe, agregarlo
            User user = new User(inputUsername, inputPassword);
            userRepository.addUser(user);

            // Mostrar un mensaje de registro exitoso
            Toast.makeText(LoginActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            // Mostrar un mensaje indicando que el usuario ya está registrado
            Toast.makeText(LoginActivity.this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
        }
    }
}


