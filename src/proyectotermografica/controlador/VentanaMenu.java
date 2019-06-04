/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectotermografica.controlador;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

/**
 *
 * @author Cho_S
 */
public class VentanaMenu extends JFrame {

    private JPanel menu;
    private JPanel cuerpo;
    private JPanel grafUsuario;
    private JPanel grafDatos;

    private CheckBox chkTodos;
    private DatePicker iniCalendario;
    private DatePicker finCalendario;
    private Button btnEjecutar;

    private JFXPanel jfxPanelUsuario = new JFXPanel();
    private ObservableList<Data> data2dUsuario = FXCollections.observableArrayList();
    private PieChart pieChartUsuario = new PieChart();

    private JFXPanel jfxPanelDatos = new JFXPanel();
    private ObservableList<Data> data2dDatos = FXCollections.observableArrayList();
    private PieChart pieChartDatos = new PieChart();

    private EjecutaQuery ejecutaQuery;

    enum DateParameterType {
        FROM_DATE, TO_DATE
    };

    public VentanaMenu() {
        ejecutaQuery = new EjecutaQuery();
        instanciar();
    }

    private void instanciar() {
        this.setTitle("TermoBoy Gráfica");
        this.setLayout(new BorderLayout());
        menu = new JPanel(new FlowLayout());

        //Contenido del menu
        JFXPanel fxPanel = new JFXPanel();
        menu.add(fxPanel);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fxPanel.setScene(createSceneMenuSuperior());
            }
        });

        this.add(menu, BorderLayout.NORTH);

        cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        grafUsuario = new JPanel(new BorderLayout());
        //Contenido del usuario
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pieChartUsuario.setTitle("Usuarios");//titulo del grafico
                pieChartUsuario.setLegendSide(Side.BOTTOM);//Posicion de leyenda
                pieChartUsuario.setData(getDataBaseUsuario());
                pieChartUsuario.setPadding(new Insets(10));
                updateColorsUsuario();
                jfxPanelUsuario.setScene(new Scene(pieChartUsuario));
            }
        });
        grafUsuario.add(jfxPanelUsuario, BorderLayout.CENTER);
        //Contenido de datos
        grafDatos = new JPanel(new BorderLayout());
        Platform.runLater(() -> {
            pieChartDatos.setTitle("Datos Vehiculos");//titulo del grafico
            pieChartDatos.setLegendSide(Side.BOTTOM);//Posicion de leyenda            
            pieChartDatos.setData(getDataBaseDatos());
            pieChartDatos.setPadding(new Insets(10));
            updateColorsDatos();
            jfxPanelDatos.setScene(new Scene(pieChartDatos));
        });
        grafDatos.add(jfxPanelDatos, BorderLayout.CENTER);

        grafDatos.setBackground(java.awt.Color.red);
        grafUsuario.setBackground(java.awt.Color.BLUE);
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;
        cuerpo.add(grafUsuario, BorderLayout.WEST);
        cuerpo.add(grafDatos, BorderLayout.EAST);

        this.add(cuerpo, BorderLayout.CENTER);

        this.setSize(1000, 550);
        //this.setResizable(false);
        //this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

    }

    private static final String pattern = "yyyy-MM-dd";

    private Scene createSceneMenuSuperior() {

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);

        //Orden 
        flowPane.setPadding(new Insets(10));
        flowPane.setHgap(15);
        flowPane.setPrefWrapLength(800);
        
        chkTodos = new CheckBox("Todos");
        chkTodos.setSelected(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        StringConverter<LocalDate> converter
                = new LocalDateStringConverter(formatter, null);
        // Input date picker
        Label inPickLabel = new Label("Fecha Entrada:");
        iniCalendario = new DatePicker(LocalDate.now());
        iniCalendario.setPromptText(pattern);
        iniCalendario.setConverter(converter);
        iniCalendario.setDisable(chkTodos.isSelected());

        // From and to date pickers
        Label pickLabel1 = new Label("Fecha Fin: ");
        finCalendario = new DatePicker(iniCalendario.getValue());
        finCalendario.setPromptText(pattern);
        finCalendario.setConverter(converter);
        finCalendario.setDisable(chkTodos.isSelected());

        iniCalendario.setDayCellFactory(getCustomDateCellFactory(DateParameterType.FROM_DATE));
        finCalendario.setDayCellFactory(getCustomDateCellFactory(DateParameterType.TO_DATE));

        // Button
        btnEjecutar = new Button("Ejecutar Query");

        //grid.setAlignment(Pos.CENTER_LEFT);
        flowPane.getChildren().add(chkTodos);

        flowPane.getChildren().addAll(inPickLabel, iniCalendario);
        flowPane.getChildren().addAll( pickLabel1, finCalendario);
        
        //grid.setAlignment(Pos.CENTER_RIGHT);
        flowPane.getChildren().add(btnEjecutar);

        //Listener
        chkTodos.setOnAction(event -> {
            iniCalendario.setDisable(chkTodos.isSelected());
            finCalendario.setDisable(chkTodos.isSelected());
            iniCalendario.setValue(LocalDate.now());
        });

        iniCalendario.setOnAction(event -> {

            finCalendario.setValue(iniCalendario.getValue());
            finCalendario.setPromptText(pattern);
            finCalendario.setConverter(converter);
        });

        btnEjecutar.setTooltip(new Tooltip("Find if the input date is within the from and to dates."));
        btnEjecutar.setOnAction(event -> {
            if (chkTodos.isSelected()) {
                //Todos
                setDataUsuario(ejecutaQuery.miraUsuariosALL());
                setDataDatos(ejecutaQuery.miraDatosALL());
            } else {
                LocalDate iniCal = iniCalendario.getValue();
                LocalDate finCal = finCalendario.getValue();
                if (iniCal.isBefore(finCal) || iniCal.isEqual(finCal)) {
                    setDataUsuario(ejecutaQuery.miraUsuarios(iniCal.toString(), finCal.toString()));
                    setDataDatos(ejecutaQuery.miraDatos(iniCal.toString(), finCal.toString()));
                } else {
                    JOptionPane.showMessageDialog(this, "La Fecha Inicial és superior al Final.");
                }
            }
        });

        return new Scene(flowPane);
    }

    private Callback<DatePicker, DateCell> getCustomDateCellFactory(DateParameterType dateParamType) {

        Callback<DatePicker, DateCell> dayCellFactory
                = new Callback<DatePicker, DateCell>() {

            @Override
            public DateCell call(DatePicker datePicker) {

                return new DateCell() {

                    @Override
                    public void updateItem(LocalDate select, boolean empty) {

                        super.updateItem(select, empty);

                        // From-date cannot be greater than to-date.
                        if ((dateParamType == DateParameterType.TO_DATE)
                                && (select.isBefore(iniCalendario.getValue()))) {
                            setDisable(true);
                        }

                    }
                };
            }
        };

        return dayCellFactory;
    }

    private ObservableList<Data> getDataBaseUsuario() {
        data2dUsuario.addAll(ejecutaQuery.miraUsuariosALL());
        return data2dUsuario;
    }

    private ObservableList<Data> getDataBaseDatos() {
        data2dDatos.addAll(ejecutaQuery.miraDatosALL());
        return data2dDatos;
    }

    private void updateColorsUsuario() {
        //colores para cada seccion de la torta
        Color[] colors = {Color.web("#04B404"), Color.web("#FF8000"), Color.web("#2D882D"),
            Color.web("#226666"), Color.web("#AA6C39"), Color.web("#AA3939")};

        int i = 0;
        //cambia colores de cada seccion de la torta
        for (PieChart.Data data : data2dUsuario) {
            String hex = String.format("#%02X%02X%02X",
                    (int) (colors[i].getRed() * 255),
                    (int) (colors[i].getGreen() * 255),
                    (int) (colors[i].getBlue() * 255));
            data.getNode().setStyle("-fx-pie-color: " + hex + ";");
            i++;
        }
        //cambia colores de la leyenda
        Set<Node> items;
        items = pieChartUsuario.lookupAll("Label.chart-legend-item");
        i = 0;
        for (Node item : items) {
            Label label = (Label) item;
            final Rectangle rectangle = new Rectangle(20, 20, colors[i]);
            label.setGraphic(rectangle);
            i++;
        }
    }

    private void updateColorsDatos() {
        //colores para cada seccion de la torta
        Color[] colors = {Color.web("#04B404"), Color.web("#FF8000"), Color.web("#2D882D"),
            Color.web("#226666"), Color.web("#AA6C39"), Color.web("#AA3939")};

        int i = 0;
        //cambia colores de cada seccion de la torta
        for (PieChart.Data data : data2dDatos) {
            String hex = String.format("#%02X%02X%02X",
                    (int) (colors[i].getRed() * 255),
                    (int) (colors[i].getGreen() * 255),
                    (int) (colors[i].getBlue() * 255));
            data.getNode().setStyle("-fx-pie-color: " + hex + ";");
            i++;
        }
        //cambia colores de la leyenda
        Set<Node> items;
        items = pieChartDatos.lookupAll("Label.chart-legend-item");
        i = 0;
        for (Node item : items) {
            Label label = (Label) item;
            final Rectangle rectangle = new Rectangle(20, 20, colors[i]);
            label.setGraphic(rectangle);
            i++;
        }
    }

    private void setDataUsuario(ArrayList<PieChart.Data> datosGrafica) {
        if (datosGrafica.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Vació");
        }
        Platform.runLater(() -> {
            data2dUsuario.clear();
            data2dUsuario.addAll(datosGrafica);
            updateColorsUsuario();
        });
    }

    private void setDataDatos(ArrayList<PieChart.Data> datosGrafica) {
        if (datosGrafica.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Vació");
        }
        Platform.runLater(() -> {
            data2dDatos.clear();
            data2dDatos.addAll(datosGrafica);
            updateColorsDatos();
        });

    }

}
