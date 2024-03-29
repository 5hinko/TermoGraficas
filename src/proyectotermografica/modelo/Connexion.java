/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectotermografica.modelo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lorenzo
 */
public class Connexion {

    private static final String DRIVER_CLASS_NAME;
    private static final String DRIVER_URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties propiedades = new Properties();
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            propiedades.load(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
        }

        DRIVER_CLASS_NAME = propiedades.getProperty("Driver_Name");
        DRIVER_URL = propiedades.getProperty("driver_url");
        USER = propiedades.getProperty("user");
        PASSWORD = propiedades.getProperty("passwd");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DRIVER_URL, USER, PASSWORD);
    }

    public static Connection getConnectionAdmin() throws SQLException {
        return DriverManager.getConnection(DRIVER_URL, USER, PASSWORD);
    }
}
