import com.sun.jna.Library;
import com.sun.jna.Native;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.plaf.PanelUI;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {

        // Caminho completo para a DLL
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "C:\\Users\\gabri\\OneDrive\\Documentos\\FACULDADE\\Java-Aluno Graduacao\\untitled\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );


        private static String lerArquivoComoString(String path) throws IOException {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = fis.readAllBytes();
            fis.close();
            return new String(data, StandardCharsets.UTF_8);
        }


        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin(int pino, int ti, int tf);

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    private static boolean conexaoAberta = false;
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;

    private static final Scanner scanner = new Scanner(System.in);

    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public static void configurarConexao() {
        System.out.println("**** Configuração da impressora ****");
        tipo = Integer.parseInt(capturarEntrada("1 - USB\n" +
                "2 - RS2322\n" +
                "3 - TCP/IP\n" +
                "4 - Bluetooth\n" +
                "5 - Impressoras acopladas(Android)\n " +
                "Escolha o tipo da impressoara: "));
        modelo = capturarEntrada("Escolha o modelo: ");
        conexao = capturarEntrada("Informe o tipo de conexão\n" +
                "USB - USB\n" +
                "RS232 - COM2\n" +
                "TCP/IP - 192.168.0.20\n" +
                "Bluetooth - AA:BB:CC:DD:EE:FF\n" +
                "Escolha: ");
        parametro = Integer.parseInt(capturarEntrada("Informe o parametro: "));
        System.out.println("Configuração salva!");
    }

    public static void abrirConexao() {
        if (conexaoAberta) {
            System.out.println("Conexão já aberta!");
            return;
        }
        int resultado = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
        if (resultado == 0) {
            conexaoAberta = true;
            System.out.println("Conexão aberta!");
        } else {
            System.out.println("Erro ao abrir a conexão. Codigo: " + resultado);
        }
    }

    public static void fecharConexao() {
        if (conexaoAberta == false) {
            System.out.println("Nenhuma conexão aberta!");
            return;
        }
        int resultado = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (resultado == 0) {
            conexaoAberta = false;
            System.out.println("Conexão fechada!");
            break;
        } else {
            System.out.println("Erro ao fechar a conexão! Erro: " + resultado);
        }
    }

    public static void ImpressaoTexto() {

        if (conexaoAberta) {

            System.out.println("Texto a ser impresso: ");
            String dados = scanner.nextLine();
            int resultado = ImpressoraDLL.INSTANCE.ImpressaoTexto(dados, 1, 4, 0);

            if (resultado == 0) {
                System.out.println("Impressão concluida");
            } else {
                System.out.println("Erro na impressão. Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
            return;
        }
    }

    public static void corte() {
        ImpressoraDLL.INSTANCE.Corte(2);
    }

    public static void AvancaPapel() {
        ImpressoraDLL.INSTANCE.AvancaPapel(2);
    }

    public static void ImpressaoQRCode() {
        if (conexaoAberta) {

            System.out.println("Insira oque deseja no QRCode");
            String dados = scanner.nextLine();

            int resultado = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6, 4);

            if (resultado == 0) {
                System.out.printf("Impressão realizada com sucesso");
            } else {
                System.out.printf("Poblema ao imprimir! Erro: " + resultado);
            }

        } else {
            System.out.println("Conexão não iniciada!");
            return;
        }
    }

    public static void ImpressaoCodigodeBarras() {
        if (conexaoAberta) {

            int resultado = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, "{A012345678912", 100, 2, 3);

            if (resultado == 0) {
                System.out.printf("Codigo de barras immpresso com sucesso!");
            } else {
                System.out.printf("Erro ao imprimir codigo de barras! Erro: " + resultado);
            }
        }else {
            System.out.println("Conexão não iniciada!");
            return;
        }
    }

    public static String LerArquivoComoString(File aqrivo) throws IOException {
        FileInputStream fis = new FileInputStream(aqrivo);
        byte[] dados = fis.readAllBytes();
        fis.close();
        return new String(dados, StandardCharsets.UTF_8);
    }

    public static void ImprimeXMLSAT() {
        if (conexaoAberta) {
            JFileChooser chooser = new JFileChooser("C:\\Users\\enzand\\Downloads\\PDV-Elgin-Java-GEA-main\\PDV-Elgin-Java-GEA-main");
            chooser.setDialogTitle("Selecione o arquivo a ser impresso");

            int retorno = chooser.showOpenDialog(null);

            if (retorno == JFileChooser.APPROVE_OPTION) {
                File arquivo = chooser.getSelectedFile();

                try {
                    String xmlconteudo = LerArquivoComoString(arquivo);

                    int resultado = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xmlconteudo, 0);

                    if (resultado == 0) {
                        System.out.println("Arquivo impresso com sucesso!");
                    } else {
                        System.out.println("Erro ao imprimir o arquivo! Erro: " + resultado);
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao ler o arquivo: " + e.getMessage());
                }
            } else {
                System.out.println("Arquivo não selecionado!");
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }


    public static void ImprimeXMLCancelamentoSAT() {
        if (conexaoAberta) {

            JFileChooser chooser = new JFileChooser("C:\\Users\\enzand\\Downloads\\PDV-Elgin-Java-GEA-main\\PDV-Elgin-Java-GEA-main");
            chooser.setDialogTitle("Selecione o arqivo ser impresso");
            int retorno = chooser.showOpenDialog(null);

            if (retorno == JFileChooser.APPROVE_OPTION) {
                File arquivo = chooser.getSelectedFile();
                try {
                    String xmlConteudo = LerArquivoComoString(arquivo);

                    String assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";


                    int resultado = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(xmlConteudo, assQRCode, 0);

                    if (retorno == 0) {
                        System.out.printf("Arquivo impresso!");
                    } else {
                        System.out.printf("Erro ao imprimir arquivo! Erro: " + resultado);
                    }
                } catch (IOException e) {
                    System.out.printf("Erro ao ler arquvio! Erro: " + e.getMessage());
                }
            } else {
                System.out.printf("Arquivo não selecionado!");
            }

        } else {
            System.out.println("Conexão não iniciada!");
            return;
        }
    }

    public static void AbreGavetaElgin() {
        if (conexaoAberta) {
            int resultadao = ImpressoraDLL.INSTANCE.AbreGavetaElgin(1, 50, 50);

            if (resultadao == 0) {
                System.out.printf("Gaveta aberta com sucesso");
            } else {
                System.out.printf("Erro ao abrir a gaveta! Erro: " + resultadao);
            }
        } else {
            System.out.printf("Conexão não iniciada");
            return;
        }

    }

    public static void AbreGaveta() {
        if (conexaoAberta) {

            int resultado = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);

            if (resultado == 0) {
                System.out.printf("Gaveta aberta com sucesso");
            } else {
                System.out.printf("Erro ao abrir a gaveta! Erro: " + resultado);
            }
        } else {
            System.out.printf("Conexão não iniciada!");
            return;
        }
    }

    public static void SinalSonoro() {
        if (conexaoAberta) {
            int resultado = ImpressoraDLL.INSTANCE.SinalSonoro(4, 5, 5);

            if (resultado == 0) {
                System.out.printf("Sinal emitido com sucesso");
            } else {
                System.out.printf("Erro ao emitir o sinal! Erro: " + resultado);
            }

        } else {
            System.out.printf("Conexão não iniciada!");
            return;
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n*************************************************");
            System.out.println("**************** MENU IMPRESSORA ******************");
            System.out.println("*************************************************\n");
            System.out.println("1  - Configurar Conexao");
            System.out.println("2  - Abrir Conexao");
            System.out.println("3  - Impressao Texto");
            System.out.println("4  - Impressao QRCode");
            System.out.println("5  - Impressao Cod Barras");
            System.out.println("6  - Impressao XML SAT");
            System.out.println("7  - Impressao XML Canc SAT");
            System.out.println("8  - Abrir Gaveta Elgin");
            System.out.println("9  - Abrir Gaveta");
            System.out.println("10 - Sinal Sonoro");
            System.out.println("0  - Fechar Conexao e Sair");
            System.out.println("--------------------------------------");

            String escolha = capturarEntrada("\nDigite a opção desejada: ");

            if (escolha.equals("0")) {

            }

            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;
                case "2":
                    abrirConexao();
                    break;
                case "3":
                    ImpressaoTexto();
                    corte();
                    AvancaPapel();
                    break;

                case "4":
                    ImpressaoQRCode();
                    AvancaPapel();
                    corte();
                    break;

                case "5":
                    ImpressaoCodigodeBarras();
                    corte();
                    AvancaPapel();
                    break;

                case "6":
                    // --- IMPORTANTE ---
                    // Este trecho permite ao usuário escolher um arquivo XML no computador.
                    // Para funcionar, será necessário importar as classes de manipulação de arquivos e da interface gráfica:
                    // import java.io.*;                    // Para trabalhar com arquivos (ex: File, IOException)
                    // import javax.swing.*;                // Para usar o JFileChooser (janela de seleção de arquivos)
                    //
                    // A ideia: abrir uma janela para o usuário escolher o XML, ler o conteúdo do arquivo
                    // e enviar para a função que imprime o XML de cancelamento do SAT.
                    //
                    // >>> Os alunos deverão implementar as partes de leitura do arquivo (função lerArquivoComoString)
                    // e o controle de fluxo (switch/case, etc) conforme aprendido em aula.

                    ImprimeXMLSAT();
                    corte();
                    AvancaPapel();
                    break;
                case "7":
                    // --- IMPORTANTE ---
                    // Este trecho permite ao usuário escolher um arquivo XML no computador.
                    // Para funcionar, será necessário importar as classes de manipulação de arquivos e da interface gráfica:
                    // import java.io.*;                    // Para trabalhar com arquivos (ex: File, IOException)
                    // import javax.swing.*;                // Para usar o JFileChooser (janela de seleção de arquivos)
                    //
                    // A ideia: abrir uma janela para o usuário escolher o XML, ler o conteúdo do arquivo
                    // e enviar para a função que imprime o XML de cancelamento do SAT.
                    //
                    // >>> Os alunos deverão implementar as partes de leitura do arquivo (função lerArquivoComoString)
                    // e o controle de fluxo (switch/case, etc) conforme aprendido em aula.


                    ImprimeXMLCancelamentoSAT();
                    AvancaPapel();
                    corte();
                    break;

                case "8":
                    AbreGavetaElgin();
                    break;

                case "9":
                    AbreGaveta();
                    break;

                case "10":
                    SinalSonoro();
                    break;

                case "0":
                    fecharConexao();
                    break;

                default:
                    System.out.println("Escolha invalida, tente novamente!");

            }
        }
    }


}
