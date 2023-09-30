import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuscaArquivos extends JFrame {
  private JTextField diretorioTextField;
  private JTextField conteudoTextField;
  private JButton buscarButton;
  private JTextArea resultadosTextArea;
  private JButton selecionarDiretorioButton;
  private File diretorioSelecionado;
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
          BuscaArquivos gui = new BuscaArquivos();
          gui.setVisible(true);
      }
  });
  }

  public BuscaArquivos() {
    setTitle("Busca de Arquivos .txt");
    setSize(400, 150);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3, 1));

    JLabel diretorioLabel = new JLabel("Digite o caminho do diretório:");
    diretorioTextField = new JTextField();
    JLabel conteudoLabel = new JLabel("Digite o conteúdo a ser buscado:");
    conteudoTextField = new JTextField();
    resultadosTextArea = new JTextArea(10, 40);
    JScrollPane resultadosScrollPane = new JScrollPane(resultadosTextArea);
    buscarButton = new JButton("Buscar");
    selecionarDiretorioButton = new JButton("Selecionar Diretório");

    selecionarDiretorioButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          selecionarDiretorio();
      }
    });

    buscarButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String diretorio = diretorioTextField.getText();
            String conteudo = conteudoTextField.getText();

            // Medir o tempo de execução
            long startTime = System.currentTimeMillis();
            buscarArquivos(diretorio, conteudo);
            long endTime = System.currentTimeMillis();
            long tempoTotal = endTime - startTime;
            resultadosTextArea.append("\nTempo de Execução: " + tempoTotal + " milissegundos");
        }
    });

    panel.add(diretorioLabel);
    panel.add(diretorioTextField);
    panel.add(conteudoLabel);
    panel.add(conteudoTextField);
    panel.add(buscarButton);
    panel.add(selecionarDiretorioButton);

    add(panel, BorderLayout.NORTH);
    add(resultadosScrollPane, BorderLayout.CENTER);
  }

  private void buscarArquivos(String diretorio, String nomeBuscado) {
    File pasta = new File(diretorio);
    ArrayList<String> resultados = new ArrayList<>();

    // Verifica se o diretório existe e é uma pasta
    if (pasta.exists() && pasta.isDirectory()) {
      // Lista os arquivos no diretório
      File[] arquivos = pasta.listFiles();

      // Itera sobre os arquivos
      for (File arquivo : arquivos) {
        if (arquivo.isFile() && arquivo.getName().endsWith(".txt")) {
          resultados.addAll(buscarNoArquivo(arquivo, nomeBuscado));
        }
      }
      exibirResultados(resultados);
    } else {
      System.out.println("O diretório não existe ou não é uma pasta.");
    }
  }

  private ArrayList<String> buscarNoArquivo(File arquivo, String nomeBuscado) {
    ArrayList<String> resultados = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
      String linha;
      int numeroLinha = 0;

      while ((linha = br.readLine()) != null) {
        numeroLinha++;

        // Verifica se a linha contém o nome buscado
        if (linha.contains(nomeBuscado)) {
          resultados.add("Arquivo: " + arquivo.getName() + ", Linha: " + numeroLinha);
        }
      }
    } catch (IOException e) {
      resultados.add("Erro ao ler o arquivo " + arquivo.getName() + ": " + e.getMessage());
    }
    return resultados;
  }

  private void exibirResultados(ArrayList<String> resultados) {
    if (resultados.isEmpty()) {
        resultadosTextArea.setText("Nenhum resultado encontrado.");
    } else {
        resultadosTextArea.setText(String.join("\n", resultados));
    }
  }

  private void selecionarDiretorio() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int escolha = fileChooser.showOpenDialog(this);

    if (escolha == JFileChooser.APPROVE_OPTION) {
        diretorioSelecionado = fileChooser.getSelectedFile();
        diretorioTextField.setText(diretorioSelecionado.getAbsolutePath());
    } else {
        resultadosTextArea.setText("Nenhum diretório selecionado.");
    }
  }
}