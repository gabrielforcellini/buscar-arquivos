import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuscaArquivosThreads extends JFrame {
  private JTextField diretorioTextField;
  private JTextField conteudoTextField;
  private JButton buscarButton;
  private JTextArea resultadosTextArea;
  private JButton selecionarDiretorioButton;
  private File diretorioSelecionado;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        BuscaArquivosThreads gui = new BuscaArquivosThreads();
        gui.setVisible(true);
      }
    });
  }

  public BuscaArquivosThreads() {
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
        buscarArquivosAsync(diretorio, conteudo);

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

  private void buscarArquivosAsync(String diretorio, String nomeBuscado) {
    File pasta = new File(diretorio);
    ArrayList<Thread> threads = new ArrayList<>();

    if (pasta.exists() && pasta.isDirectory()) {
      File[] arquivos = pasta.listFiles();

      for (File arquivo : arquivos) {
        if (arquivo.isFile() && arquivo.getName().endsWith(".txt")) {
          Thread buscaThread = new Thread(new BuscaArquivoRunnable(arquivo, nomeBuscado));
          threads.add(buscaThread);
          buscaThread.start();
        }
      }
    } else {
      resultadosTextArea.setText("O diretório não existe ou não é uma pasta.");
    }

    // Esperar até que todas as threads tenham concluído
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private class BuscaArquivoRunnable implements Runnable {
    private File arquivo;
    private String nomeBuscado;

    public BuscaArquivoRunnable(File arquivo, String nomeBuscado) {
      this.arquivo = arquivo;
      this.nomeBuscado = nomeBuscado;
    }

    @Override
    public void run() {
      buscarNoArquivo(arquivo, nomeBuscado);
    }
  }

  private void buscarNoArquivo(File arquivo, String nomeBuscado) {
    try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
      String linha;
      int numeroLinha = 0;

      while ((linha = br.readLine()) != null) {
        numeroLinha++;

        // Verifica se a linha contém o nome buscado
        if (linha.contains(nomeBuscado)) {
          resultadosTextArea.append("Arquivo: " + arquivo.getName() + ", Linha: " + numeroLinha + "\n");
        }
      }
    } catch (IOException e) {
      resultadosTextArea.setText("Erro ao ler o arquivo " + arquivo.getName() + ": " + e.getMessage());
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