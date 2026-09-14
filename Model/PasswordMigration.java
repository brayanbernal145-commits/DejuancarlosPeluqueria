package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.UsuarioRepository;
import java.util.List;

/**
 * One-time migration to hash existing plain-text passwords
 * This will run on application startup and hash any unhashed passwords
 */
@Component
public class PasswordMigration implements CommandLineRunner {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public void run(String... args) throws Exception {
        List<Usuario> usuarios = usuarioRepository.findAll();
        int migrated = 0;
        
        for (Usuario usuario : usuarios) {
            String password = usuario.getContrasenia();
            
            // Check if password is already hashed
            if (password != null && !PasswordHasher.isHashed(password)) {
                // Hash the plain-text password
                usuario.setContrasenia(PasswordHasher.hashPassword(password));
                usuarioRepository.save(usuario);
                migrated++;
            }
        }
        
        if (migrated > 0) {
            System.out.println("Password migration completed: " + migrated + " passwords hashed.");
        }
    }
}
