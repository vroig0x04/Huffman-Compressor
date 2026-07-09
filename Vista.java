import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.net.URI;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Vista extends JFrame {

    Controlador cntrl = new Controlador();

    JFileChooser filechooser;
    File archivoSeleccionado = null;

    JLabel ficheroEscogido;
    JLabel sizeFichero;
    JLabel estado;

    JTable tablaSimbolos;
    TableRowSorter<DefaultTableModel> sorter;

    JLabel tFrecuencia;
    JLabel estadisticas;
    JLabel entropia;
    JLabel longitudMedia;
    JLabel rCompresion;
    JLabel tiempo;

    JButton bComprimir;
    JButton bDescomprimir;
    JButton bMostrarArbol;

    JProgressBar barraProgreso;

    long longitudArchivoOrg;

    DefaultTableModel modelo;

    public Vista() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el tema nativo.");
        }

        filechooser = new JFileChooser();
        cntrl.inicializar_estructura("BinaryHeap");
        initVista();
    }

    private void initVista() {
        setTitle("Compresor de archivos Huffman");
        setSize(950, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelSuperior = new JPanel(new GridLayout(2, 2));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        fila1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fila2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelLateralIzquierdo = new JPanel(new GridLayout(3, 1));
        JPanel panelLateralDerecho = new JPanel(new BorderLayout());

        panelLateralIzquierdo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelLateralDerecho.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelInferior = new JPanel(new GridLayout(1, 4, 15, 15));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInferior.setBackground(new Color(245, 245, 245));

        JMenuBar menuBar = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenu ayuda = new JMenu("Ayuda");

        JMenuItem abrir = new JMenuItem("Abrir");
        JMenuItem cerrar = new JMenuItem("Cerrar");
        JMenuItem acerca = new JMenuItem("Acerca de");

        modelo = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int col) {
                return (col == 1 || col == 2) ? Integer.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        modelo.addColumn("Símbolo");
        modelo.addColumn("ASCII");
        modelo.addColumn("Frecuencia");
        modelo.addColumn("Código");

        tablaSimbolos = new JTable(modelo);

        sorter = new TableRowSorter<>(modelo);
        sorter.setComparator(2, Comparator.comparingInt(o -> (Integer) o));
        tablaSimbolos.setRowSorter(sorter);

        DefaultTableCellRenderer rendererIzquierda = new DefaultTableCellRenderer();
        rendererIzquierda.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < modelo.getColumnCount(); i++) {
            tablaSimbolos.getColumnModel().getColumn(i).setCellRenderer(rendererIzquierda);
        }

        ficheroEscogido = new JLabel("Fichero: ");
        sizeFichero = new JLabel("Tamaño: ");
        estado = new JLabel("Estado: ");

        tFrecuencia = new JLabel("Frecuencia y codigos");
        estadisticas = new JLabel("Estadisticas");

        entropia = new JLabel("--");
        longitudMedia = new JLabel("--");
        rCompresion = new JLabel("--");
        tiempo = new JLabel("--");

        bComprimir = new JButton("Comprimir");
        bDescomprimir = new JButton("Descomprimir");
        bMostrarArbol = new JButton("Mostrar arbol");

        barraProgreso = new JProgressBar(0, 100);

        bComprimir.setEnabled(false);
        bDescomprimir.setEnabled(false);
        bMostrarArbol.setEnabled(false);

        barraProgreso.setValue(0);
        barraProgreso.setStringPainted(true);

        archivo.add(abrir);
        archivo.add(cerrar);
        ayuda.add(acerca);

        menuBar.add(archivo);
        menuBar.add(ayuda);

        fila1.add(ficheroEscogido);
        fila1.add(sizeFichero);

        fila2.add(estado);
        fila2.add(barraProgreso);

        panelSuperior.add(fila1);
        panelSuperior.add(fila2);

        panelLateralIzquierdo.add(bComprimir);
        panelLateralIzquierdo.add(bDescomprimir);
        panelLateralIzquierdo.add(bMostrarArbol);

        panelLateralDerecho.add(tFrecuencia, BorderLayout.NORTH);
        panelLateralDerecho.add(new JScrollPane(tablaSimbolos), BorderLayout.CENTER);

        panelInferior.add(crearTarjeta("Entropía", entropia));
        panelInferior.add(crearTarjeta("Longitud Media", longitudMedia));
        panelInferior.add(crearTarjeta("Compresión", rCompresion));
        panelInferior.add(crearTarjeta("Tiempo", tiempo));

        setJMenuBar(menuBar);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelLateralIzquierdo, BorderLayout.WEST);
        add(panelLateralDerecho, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        abrir.addActionListener(e -> seleccionarArchivo());
        acerca.addActionListener(e -> mostrarAyuda());
        cerrar.addActionListener(e -> cerrarAplicacion());
        bComprimir.addActionListener(e -> comprimir_archivo());
        bDescomprimir.addActionListener(e -> descomprimir_archivo());
        bMostrarArbol.addActionListener(e -> arbolHuffman());
    }

    public void seleccionarArchivo() {
        bComprimir.setEnabled(false);
        bDescomprimir.setEnabled(false);

        int seleccion = filechooser.showOpenDialog(null);

        if (seleccion == JFileChooser.FILES_ONLY) {
            archivoSeleccionado = filechooser.getSelectedFile();
            String nombre = archivoSeleccionado.getName();

            ficheroEscogido.setText("Archivo: " + nombre);

            longitudArchivoOrg = archivoSeleccionado.length();
            sizeFichero.setText("Tamaño: " + pasar_length_a_kb(longitudArchivoOrg));

            if (nombre.endsWith(".ç") || nombre.endsWith(".huff")) {
                bDescomprimir.setEnabled(true);
                estado.setText("Estado: Listo para descomprimir");
            } else {
                bComprimir.setEnabled(true);
                estado.setText("Estado: Listo para comprimir");
            }

            repaint();
        }
    }

    public void deseleccionarArchivo() {
        filechooser.setSelectedFile(null);
        longitudArchivoOrg = 0;

        ficheroEscogido.setText("Archivo: ");
        sizeFichero.setText("Tamaño: ");
        estado.setText("Estado: ");

        bComprimir.setEnabled(false);
        bDescomprimir.setEnabled(false);
        bMostrarArbol.setEnabled(false);

        repaint();
    }

    private void cerrarAplicacion() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar la aplicación?",
                "Cerrar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private String pasar_length_a_kb(long size) {
        double kilobytes = (double) size / 1024;
        return String.format("%.2f KB", kilobytes);
    }

    private void comprimir_archivo() {
        barraProgreso.setValue(0);
        barraProgreso.setMaximum((int) Math.max(1, longitudArchivoOrg * 2));
        estado.setText("Estado: Comprimiendo...");
        bComprimir.setEnabled(false);

        SwingWorker<ResultadoCompresion, Integer> worker = new SwingWorker<ResultadoCompresion, Integer>() {

            @Override
            protected ResultadoCompresion doInBackground() throws Exception {
                return cntrl.comprimir_archivo(archivoSeleccionado, bytesLeidos -> publish(bytesLeidos));
            }

            @Override
            protected void process(List<Integer> chunks) {
               
                barraProgreso.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    ResultadoCompresion resultados = get();

                    long size_comprimido = resultados.size;
                    long tiempo_resultado = resultados.tiempo;
                    double entropia_resultado = resultados.entropia;
                    double longitudMedia_resultado = resultados.longitudMedia;

                    entropia.setText(String.format("%.2f", entropia_resultado) + " bits");
                    longitudMedia.setText(String.format("%.2f", longitudMedia_resultado) + " bits");
                    tiempo.setText(tiempo_resultado + " ms");

                    double division = (double) size_comprimido / longitudArchivoOrg;
                    double porc_compr = (1.0 - division) * 100;
                    rCompresion.setText(String.format("%.2f", porc_compr) + "%");

                    String rutaSalida = archivoSeleccionado.getName()
                            .replaceAll("\\.[^.]+$", "") + ".ç";

                    ficheroEscogido.setText("Fichero resultado: " + rutaSalida);
                    sizeFichero.setText("Tamaño comprimido: " + pasar_length_a_kb(size_comprimido));

                    barraProgreso.setValue(barraProgreso.getMaximum());
                    estado.setText("Estado: Comprimido");

                    actualizarTabla(cntrl.obtenerSimbolos());
                    bMostrarArbol.setEnabled(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    estado.setText("Estado: Error");
                }
            }
        };

        worker.execute();
    }

    private void descomprimir_archivo() {
        cntrl.descomprimir(archivoSeleccionado);
        estado.setText("Estado: Descomprimido");
    }

    private void mostrarAyuda() {
        String url = "https://es.wikipedia.org/wiki/Codificaci%C3%B3n_Huffman";
        try {
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarTabla(List<Simbolo> listaSimbolos) {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaSimbolos.getModel();
        modeloTabla.setRowCount(0);

        for (Simbolo s : listaSimbolos) {
            modeloTabla.addRow(new Object[]{
                    s.getCaracter(),
                    s.getAscii(),
                    s.getFrecuencia(),
                    s.getCodigo()
            });
        }
    }


    private void arbolHuffman() {
        Estructura.Nodo raiz = cntrl.getRaiz();

        if (raiz == null) {
            JOptionPane.showMessageDialog(this, "Primero debes comprimir un archivo");
            return;
        }

        JDialog ventana = new JDialog(this, "Árbol Huffman", false);
        ventana.setSize(1000, 700);
        ventana.setLocationRelativeTo(this);

        ArbolPanel panel = new ArbolPanel(raiz);
        JScrollPane scroll = new JScrollPane(panel);


        JButton zoomMas = new JButton("+");
        JButton zoomMenos = new JButton("-");
        JButton reset = new JButton("Reset");

        zoomMas.addActionListener(e -> panel.zoomIn());
        zoomMenos.addActionListener(e -> panel.zoomOut());
        reset.addActionListener(e -> panel.resetZoom());

        JPanel controles = new JPanel();
        controles.add(zoomMas);
        controles.add(zoomMenos);
        controles.add(reset);

        ventana.add(controles, BorderLayout.NORTH);
        ventana.add(scroll, BorderLayout.CENTER);
        ventana.setVisible(true);
    }


    private class ArbolPanel extends JPanel {

        private static final int RADIO = 18;
        private static final int UNIDAD_X = 46;
        private static final int UNIDAD_Y = 90;
        private static final int MARGEN = 60;

        private Estructura.Nodo raiz;
        private double zoom = 1.0;
        private Map<Estructura.Nodo, Integer> posX = new HashMap<>();
        private Map<Estructura.Nodo, Integer> posY = new HashMap<>();
        private int contadorHojas = 0;
        private int profundidadMax = 0;
        private int anchoBase;
        private int altoBase;

        public ArbolPanel(Estructura.Nodo raiz) {
            this.raiz = raiz;
            calcularProfundidad(raiz, 0);
            calcularPosiciones(raiz, 0);

            anchoBase = Math.max((contadorHojas + 1) * UNIDAD_X + MARGEN * 2, 400);
            altoBase = Math.max((profundidadMax + 1) * UNIDAD_Y + MARGEN * 2, 300);
            setPreferredSize(new Dimension(anchoBase, altoBase));
            setBackground(Color.WHITE);
        }

        private void calcularProfundidad(Estructura.Nodo nodo, int profundidad) {
            if (nodo == null) return;
            profundidadMax = Math.max(profundidadMax, profundidad);
            calcularProfundidad(nodo.getNodoIzq(), profundidad + 1);
            calcularProfundidad(nodo.getNodoDer(), profundidad + 1);
        }

        private int calcularPosiciones(Estructura.Nodo nodo, int profundidad) {
            if (nodo.getNodoIzq() == null && nodo.getNodoDer() == null) {
                int x = contadorHojas * UNIDAD_X;
                contadorHojas++;
                posX.put(nodo, x);
                posY.put(nodo, profundidad * UNIDAD_Y);
                return x;
            }

            int xIzq = calcularPosiciones(nodo.getNodoIzq(), profundidad + 1);
            int xDer = calcularPosiciones(nodo.getNodoDer(), profundidad + 1);
            int x = (xIzq + xDer) / 2;

            posX.put(nodo, x);
            posY.put(nodo, profundidad * UNIDAD_Y);
            return x;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale(zoom, zoom);

            dibujarNodo(g2, raiz, MARGEN, MARGEN);
        }

        private void dibujarNodo(Graphics2D g, Estructura.Nodo nodo, int offsetX, int offsetY) {
            int x = posX.get(nodo) + offsetX;
            int y = posY.get(nodo) + offsetY;

            if (nodo.getNodoIzq() != null) {
                dibujarRama(g, nodo, nodo.getNodoIzq(), "0", x, y, offsetX, offsetY);
            }

            if (nodo.getNodoDer() != null) {
                dibujarRama(g, nodo, nodo.getNodoDer(), "1", x, y, offsetX, offsetY);
            }

            boolean esHoja = nodo.getNodoIzq() == null && nodo.getNodoDer() == null;

            g.setColor(esHoja ? new Color(255, 206, 143) : new Color(173, 216, 230));
            g.fillOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);

            g.setColor(new Color(90, 90, 90));
            g.drawOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);

            String texto = esHoja ? representarCaracter(nodo.getValorByte())
                                   : Integer.toString(nodo.getFrecuencia());

            g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.BLACK);
            g.drawString(texto, x - fm.stringWidth(texto) / 2, y + 4);

            if (esHoja) {
                String frecuenciaTexto = Integer.toString(nodo.getFrecuencia());
                g.setFont(g.getFont().deriveFont(Font.PLAIN, 10f));
                FontMetrics fm2 = g.getFontMetrics();
                g.setColor(new Color(120, 120, 120));
                g.drawString(frecuenciaTexto, x - fm2.stringWidth(frecuenciaTexto) / 2, y + RADIO + 13);
            }
        }

        private void dibujarRama(Graphics2D g, Estructura.Nodo padre, Estructura.Nodo hijo, String etiqueta,
                                  int x, int y, int offsetX, int offsetY) {
            int xHijo = posX.get(hijo) + offsetX;
            int yHijo = posY.get(hijo) + offsetY;

            g.setColor(new Color(190, 190, 190));
            g.drawLine(x, y, xHijo, yHijo);

            int xEtiqueta = (x + xHijo) / 2 + (etiqueta.equals("0") ? -11 : 4);
            int yEtiqueta = (y + yHijo) / 2;

            g.setColor(Color.WHITE);
            g.fillRect(xEtiqueta - 1, yEtiqueta - 10, 12, 13);

            g.setColor(new Color(100, 100, 100));
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 11f));
            g.drawString(etiqueta, xEtiqueta, yEtiqueta);

            dibujarNodo(g, hijo, offsetX, offsetY);
        }

        private String representarCaracter(int valor) {
            if (valor == '\n') return "\\n";
            if (valor == '\t') return "\\t";
            if (valor == '\r') return "\\r";
            if (valor == ' ') return "\u2423";
            if (valor < 32 || valor == 127) return "?" + valor;
            return Character.toString((char) valor);
        }

        public void zoomIn() {
            zoom *= 1.2;
            aplicarZoom();
        }

        public void zoomOut() {
            zoom /= 1.2;
            aplicarZoom();
        }

        public void resetZoom() {
            zoom = 1.0;
            aplicarZoom();
        }

        private void aplicarZoom() {
            setPreferredSize(new Dimension((int) (anchoBase * zoom), (int) (altoBase * zoom)));
            revalidate();
            repaint();
        }
    }

    private JPanel crearTarjeta(String titulo, JLabel valor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(new Color(70, 70, 70));

        valor.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        valor.setForeground(new Color(25, 25, 25));

        card.add(t, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);

        return card;
    }
}