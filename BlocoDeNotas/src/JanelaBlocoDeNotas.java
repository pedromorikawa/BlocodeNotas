import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class JanelaBlocoDeNotas extends JFrame {
    private JTextArea areaDeTexto;
    private LinkedHashMap<String, String> arquivosRecentes;
    private String[] extensoesPermitidas = {"txt", "java", "md"};
    private Set<String> conjuntoExtensoes;

    public JanelaBlocoDeNotas() {
        setTitle("Bloco de Notas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        arquivosRecentes = new LinkedHashMap<String, String>(5, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > 5;
            }
        };

        conjuntoExtensoes = new HashSet<>(Arrays.asList(extensoesPermitidas));

        areaDeTexto = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaDeTexto);
        add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuArquivo = new JMenu("Arquivo");
        menuBar.add(menuArquivo);

        JMenuItem itemNovo = new JMenuItem("Novo");
        itemNovo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaDeTexto.setText("");
            }
        });
        menuArquivo.add(itemNovo);

        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirArquivo();
            }
        });
        menuArquivo.add(itemAbrir);

        JMenuItem itemSalvar = new JMenuItem("Salvar");
        itemSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salvarArquivo();
            }
        });
        menuArquivo.add(itemSalvar);

        JMenu menuRecentes = new JMenu("Recente");
        menuArquivo.add(menuRecentes);
        atualizarMenuRecentes(menuRecentes);

        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuArquivo.add(itemSair);
    }

    private void abrirArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (conjuntoExtensoes.contains(getFileExtension(file))) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    areaDeTexto.read(reader, null);
                    adicionarArquivoRecente(file.getName(), file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Extensão de arquivo não permitida.");
            }
        }
    }

    private void salvarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String ext = getFileExtension(file);
            if (ext.isEmpty() || !conjuntoExtensoes.contains(ext)) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            if (conjuntoExtensoes.contains(getFileExtension(file))) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    areaDeTexto.write(writer);
                    adicionarArquivoRecente(file.getName(), file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Extensão de arquivo não permitida.");
            }
        }
    }

    private void adicionarArquivoRecente(String nome, String caminho) {
        arquivosRecentes.put(nome, caminho);
        atualizarMenuRecentes((JMenu) getJMenuBar().getMenu(0).getItem(3)); // Atualizar o menu recente
    }

    private void atualizarMenuRecentes(JMenu menuRecentes) {
        menuRecentes.removeAll();
        for (String nome : arquivosRecentes.keySet()) {
            JMenuItem itemRecente = new JMenuItem(nome);
            itemRecente.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    abrirArquivoRecente(nome);
                }
            });
            menuRecentes.add(itemRecente);
        }
    }

    private void abrirArquivoRecente(String nome) {
        String caminho = arquivosRecentes.get(nome);
        File file = new File(caminho);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            areaDeTexto.read(reader, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex+1);
    }
}
