/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectotermografica.controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.PieChart;
import proyectotermografica.modelo.Connexion;

/**
 *
 * @author Cho_S
 */
public class EjecutaQuery {

    private Connection conn;
    private ArrayList<PieChart.Data> listaUsuarioGrafica;
    private ArrayList<PieChart.Data> listaDatosGrafica;

    public EjecutaQuery() {
        try {
            conn = Connexion.getConnection();
            listaUsuarioGrafica = new ArrayList<>();
            listaDatosGrafica = new ArrayList<>();
        } catch (SQLException ex) {
            Logger.getLogger(EjecutaQuery.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ArrayList<PieChart.Data> miraUsuariosALL() {
        listaUsuarioGrafica.clear();
        try {
            String queryFecha = "select "
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.genero = 'Mujer') as 'Mujeres',"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.genero = 'Hombre') as 'Hombres',"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.genero = 'Otro') as 'Otros',"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.genero is null) as 'Anónimos'";

            PreparedStatement sentenciaP = conn.prepareStatement(queryFecha);

            ResultSet rs = sentenciaP.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    listaUsuarioGrafica.add(new PieChart.Data(rsmd.getColumnName(i), Double.parseDouble(columnValue)));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(EjecutaQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaUsuarioGrafica;
    }

    public ArrayList<PieChart.Data> miraDatosALL() {
        listaDatosGrafica.clear();
        try {
            String queryFecha = "select usa.fk_transporte, count(usa.fk_transporte) from Usar usa group by usa.fk_transporte";

            PreparedStatement sentenciaP = conn.prepareStatement(queryFecha);

            ResultSet rs = sentenciaP.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                String nombreDato = "";
                double valor = 0;
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i % 1 == 0) {
                        nombreDato = rs.getString(1);
                    }
                    if (i % 2 == 0) {
                        valor = (double) Integer.parseInt(rs.getString(2));
                    }
                }
                listaDatosGrafica.add(new PieChart.Data(nombreDato, valor));
            }

        } catch (SQLException ex) {
            Logger.getLogger(EjecutaQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDatosGrafica;
    }

    public ArrayList<PieChart.Data> miraUsuarios(String iniDia, String iniFin) {
        listaUsuarioGrafica.clear();
        try {
            String queryFecha = "select"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.fk_codigo in ("
                    + "select usar.fk_usuario from Usar usar where usar.fk_dia >= (?) AND usar.fk_dia <= (?) )"
                    + "AND usu.genero = 'Mujer') as 'Mujeres' ,"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.fk_codigo in ("
                    + "select usar.fk_usuario from usar where usar.fk_dia >= (?) AND usar.fk_dia <= (?) )"
                    + "AND usu.genero = 'Hombre') as 'Hombres',"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.fk_codigo in ("
                    + "select usar.fk_usuario from Usar usar  where usar.fk_dia >= (?) AND usar.fk_dia <= (?) )"
                    + "AND usu.genero = 'Otro') as 'Otros',"
                    + "(select count(usu.fk_codigo) from Usuario usu where usu.fk_codigo in ("
                    + "select usar.fk_usuario from Usar usar where usar.fk_dia >= (?) AND usar.fk_dia <= (?) )"
                    + "AND usu.genero is null) as 'Anónimos'";

            PreparedStatement sentenciaP = conn.prepareStatement(queryFecha);

            sentenciaP.setObject(1, iniDia);
            sentenciaP.setObject(2, iniFin);
            sentenciaP.setObject(3, iniDia);
            sentenciaP.setObject(4, iniFin);
            sentenciaP.setObject(5, iniDia);
            sentenciaP.setObject(6, iniFin);
            sentenciaP.setObject(7, iniDia);
            sentenciaP.setObject(8, iniFin);

            ResultSet rs = sentenciaP.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    listaUsuarioGrafica.add(new PieChart.Data(rsmd.getColumnName(i), Double.parseDouble(columnValue)));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(EjecutaQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaUsuarioGrafica;
    }

    public ArrayList<PieChart.Data> miraDatos(String iniDia, String iniFin) {
        listaDatosGrafica.clear();
        try {
            String queryFecha
                    = "select usar.fk_transporte, count(usar.fk_transporte) from Usar usar where usar.fk_dia >= (?) AND usar.fk_dia <= (?) group by usar.fk_transporte";

            PreparedStatement sentenciaP = conn.prepareStatement(queryFecha);
            sentenciaP.setObject(1, iniDia);
            sentenciaP.setObject(2, iniFin);

            ResultSet rs = sentenciaP.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                String nombreDato = "";
                double valor = 0;
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i % 1 == 0) {
                        nombreDato = rs.getString(1);
                    }
                    if (i % 2 == 0) {
                        valor = (double) Integer.parseInt(rs.getString(2));
                    }
                }
                listaDatosGrafica.add(new PieChart.Data(nombreDato, valor));
            }

        } catch (SQLException ex) {
            Logger.getLogger(EjecutaQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDatosGrafica;
    }
}
