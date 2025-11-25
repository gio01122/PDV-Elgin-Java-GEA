/*
Projeto Final - PDV Simples com integração Elgin (versão pronta para testes)
Arquivo: Projeto_Final_PDV_Elgin_Complete.java

RESUMO
------
Este arquivo contém um exemplo completo em Java de um PDV (menu interativo)
que utiliza JNA para chamar uma DLL/SDK da Elgin. O objetivo é cumprir os
requisitos do projeto: usar as funções obrigatórias da biblioteca Elgin,
usar laços de repetição, condicionais e organizar a lógica em funções próprias.

IMPORTANTE
----------
1) Antes de executar, revise o caminho da DLL em `ImpressoraDLL.INSTANCE`.
   Em muitos setups a chamada correta é usar apenas o nome da biblioteca
   (sem caminho) e deixá-la no PATH ou no diretório do projeto. No entanto,
   este exemplo mantém a abordagem de usar o caminho absoluto conforme seu
   código inicial.

2) Substitua valores default (baudrate, porta, parâmetros) conforme a sua
   impressora. Consulte a documentação Elgin fornecida pelo professor.

3) O código usa JFileChooser para selecionar arquivos XML (opções 6 e 7).

4) As funções da DLL retornam inteiros. Neste exemplo, assumimos que
   retorno == 0 indica sucesso. Ajuste conforme a especificação da sua DLL.

COMO USAR
--------
1. Abra este arquivo em sua IDE (IntelliJ).
2. Adicione a dependência JNA no classpath (JAR). No Maven: groupId=net.java.dev.jna.
3. Ajuste o caminho da DLL se necessário.
4. Rode a classe Main.


--- README resumido (coloque em README.md no GitHub) ---
# PDV Simples - Integração com Impressora Elgin

Este repositório contém um PDV simplificado em Java que demonstra a
integração com impressoras Elgin via DLL usando JNA. O sistema possui um
menu interativo com funções como imprimir texto, QR code, código de barras,
abrir gaveta, emitir sinal sonoro, avançar papel, cortar papel e imprimir
XML SAT e XML de cancelamento.

Funções Elgin usadas (obrigatórias):
- AbreConexaoImpressora
- FechaConexaoImpressora
- ImpressaoTexto
- Corte
- ImpressaoQRCode
- ImpressaoCodigoBarras
- AvancaPapel
- AbreGavetaElgin
- AbreGaveta
- SinalSonoro
- ImprimeXMLSAT
- ImprimeXMLCancelamentoSAT


FIM DO CABEÇALHO
*/

import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {
        // ATENÇÃO: ajuste o caminho/nome da DLL conforme seu ambiente.
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                // Exemplo de caminho absoluto (substitua se necessário):
                "C:\\Users\\richard.spanhol\\Downloads\\Java-Aluno Graduacao\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );

        // Prototipos das funções (conforme sua assinatura na DLL)
        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);
        int FechaConexaoImpressora();
        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);
        int Corte(int avanco);
        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);
        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);
        int AvancaPapel(int linhas);
        int StatusImpressora(int param);
        int AbreGavetaElgin();
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
    private static int tipo = 1; // 1=USB,2=Serial,3=Ethernet (default USB)
    private static String modelo = "i9";
    private static String conexao = "USB";
    private static int parametro = 0;

    private static final Scanner scanner = new Scanner(System.in);

    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    // ---------------------- FUNÇÃO DE LEITURA DE ARQUIVOS ----------------------
    // Utilitária para ler arquivo inteiro como String (UTF-8)
    private static String lerArquivoComoString(String path) throws IOException {
        try (InputStream is = new FileInputStream(path);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    // ---------------------- CONFIGURAÇÃO E CONEXÃO ----------------------
    public static void configurarConexao() {
        System.out.println("\n--- CONFIGURAÇÃO DA CONEXÃO ---");
        try {
            String tipoStr = capturarEntrada("Tipo de conexão (1=USB, 2=Serial, 3=Ethernet) [padrão 1]: ");
            if (!tipoStr.isBlank()) tipo = Integer.parseInt(tipoStr.trim());
        } catch (Exception e) {
            System.out.println("Entrada inválida para tipo. Mantendo padrão: " + tipo);
        }

        String modeloInput = capturarEntrada("Modelo da impressora (ex: i9) [padrão i9]: ");
        if (!modeloInput.isBlank()) modelo = modeloInput.trim();

        if (tipo == 1) { // USB
            conexao = "USB";
            parametro = 0;
            System.out.println("Configurado para USB.");
        } else if (tipo == 2) { // Serial
            String porta = capturarEntrada("Digite a porta serial (ex: COM3): ");
            if (!porta.isBlank()) conexao = porta.trim();
            // Poderíamos pedir baudrate; usar 115200 como valor comum
            try {
                String p = capturarEntrada("Baudrate (ex: 115200) [padrão 115200]: ");
                parametro = p.isBlank() ? 115200 : Integer.parseInt(p.trim());
            } catch (Exception e) {
                parametro = 115200;
            }
            System.out.println("Configurado para Serial: " + conexao + " @" + parametro);
        } else if (tipo == 3) { // Ethernet
            String ip = capturarEntrada("Digite o IP da impressora (ex: 192.168.0.100): ");
            if (!ip.isBlank()) conexao = ip.trim();
            try {
                String p = capturarEntrada("Porta TCP (ex: 9100) [padrão 9100]: ");
                parametro = p.isBlank() ? 9100 : Integer.parseInt(p.trim());
            } catch (Exception e) {
                parametro = 9100;
            }
            System.out.println("Configurado para Ethernet: " + conexao + ":" + parametro);
        } else {
            System.out.println("Tipo desconhecido. Mantendo configuração anterior.");
        }
    }

    public static void abrirConexao() {
        if (conexaoAberta) {
            System.out.println("Conexão já aberta.");
            return;
        }
        System.out.println("Tentando abrir conexão...\nTipo: " + tipo + " Modelo: " + modelo + " Conexão: " + conexao + " Param: " + parametro);
        int ret = -1;
        try {
            ret = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
        } catch (UnsatisfiedLinkError ule) {
            System.out.println("Erro ao chamar a DLL. Verifique o caminho e a presença da DLL: " + ule.getMessage());
            return;
        } catch (Throwable t) {
            System.out.println("Erro inesperado ao abrir conexão: " + t.getMessage());
            return;
        }

        // Assumimos que 0 = sucesso (verifique sua DLL)
        if (ret == 0) {
            conexaoAberta = true;
            System.out.println("Conexão aberta com sucesso.");
        } else {
            System.out.println("Falha ao abrir conexão. Código de retorno: " + ret);
        }
    }

    public static void fecharConexao() {
        if (!conexaoAberta) {
            System.out.println("Nenhuma conexão aberta.");
            return;
        }
        try {
            int ret = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
            if (ret == 0) {
                System.out.println("Conexão fechada com sucesso.");
            } else {
                System.out.println("Fechamento retornou código: " + ret + " (verificar)");
            }
        } catch (Throwable t) {
            System.out.println("Erro ao fechar conexão: " + t.getMessage());
        } finally {
            conexaoAberta = false;
        }
    }

    // ---------------------- AÇÕES PRINCIPAIS (MENU) ----------------------
    public static void main(String[] args) {
        boolean sair = false;

        while (!sair) {
            printMenu();
            String escolha = capturarEntrada("\nDigite a opção desejada: ");

            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;

                case "2":
                    abrirConexao();
                    break;

                case "3": // Impressao Texto
                    if (!validaConexao()) break;
                    String texto = capturarEntrada("Digite o texto a ser impresso: ");
                    try {
                        int r = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto, 0, 0, 0);
                        System.out.println("Retorno ImpressaoTexto: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro ImpressaoTexto: " + t.getMessage());
                    }
                    break;

                case "4": // Impressao QRCode
                    if (!validaConexao()) break;
                    String conteudoQR = capturarEntrada("Conteúdo do QR Code: ");
                    try {
                        int r = ImpressoraDLL.INSTANCE.ImpressaoQRCode(conteudoQR, 4, 2);
                        System.out.println("Retorno ImpressaoQRCode: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro ImpressaoQRCode: " + t.getMessage());
                    }
                    break;

                case "5": // Impressao Codigo de Barras
                    if (!validaConexao()) break;
                    String codigoBarras = capturarEntrada("Código numérico para barras: ");
                    try {
                        // tipo 4 = CODE128 por exemplo (ajuste conforme sua DLL)
                        int r = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(4, codigoBarras, 120, 2, 0);
                        System.out.println("Retorno ImpressaoCodigoBarras: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro ImpressaoCodigoBarras: " + t.getMessage());
                    }
                    break;

                case "6": // Impressao XML SAT (venda)
                    if (!validaConexao()) break;
                    System.out.println("Selecione o arquivo XML de venda (janela será aberta)...");
                    JFileChooser chooser = new JFileChooser();
                    int escolhaArquivo = chooser.showOpenDialog(null);
                    if (escolhaArquivo == JFileChooser.APPROVE_OPTION) {
                        File arquivo = chooser.getSelectedFile();
                        try {
                            String xml = lerArquivoComoString(arquivo.getAbsolutePath());
                            int r = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);
                            System.out.println("Retorno ImprimeXMLSAT: " + r);
                        } catch (IOException e) {
                            System.out.println("Erro lendo o arquivo: " + e.getMessage());
                        } catch (Throwable t) {
                            System.out.println("Erro ImprimeXMLSAT: " + t.getMessage());
                        }
                    } else {
                        System.out.println("Nenhum arquivo selecionado.");
                    }
                    break;

                case "7": // Impressao XML Cancelamento SAT
                    if (!validaConexao()) break;
                    System.out.println("Selecione o arquivo XML de cancelamento (janela será aberta)...");
                    JFileChooser chooser2 = new JFileChooser();
                    int escolhaArquivo2 = chooser2.showOpenDialog(null);
                    if (escolhaArquivo2 == JFileChooser.APPROVE_OPTION) {
                        File arquivo = chooser2.getSelectedFile();
                        try {
                            String xmlCanc = lerArquivoComoString(arquivo.getAbsolutePath());
                            String assQRCode = capturarEntrada("Digite o texto do QR Code/assinatura (se houver): ");
                            int r = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(xmlCanc, assQRCode, 0);
                            System.out.println("Retorno ImprimeXMLCancelamentoSAT: " + r);
                        } catch (IOException e) {
                            System.out.println("Erro lendo o arquivo: " + e.getMessage());
                        } catch (Throwable t) {
                            System.out.println("Erro ImprimeXMLCancelamentoSAT: " + t.getMessage());
                        }
                    } else {
                        System.out.println("Nenhum arquivo selecionado.");
                    }
                    break;

                case "8": // Abrir Gaveta Elgin
                    if (!validaConexao()) break;
                    try {
                        int r = ImpressoraDLL.INSTANCE.AbreGavetaElgin();
                        System.out.println("Retorno AbreGavetaElgin: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro AbreGavetaElgin: " + t.getMessage());
                    }
                    break;

                case "9": // Abrir Gaveta (generic)
                    if (!validaConexao()) break;
                    try {
                        // pino, ti, tf -> parâmetros típicos (ajuste conforme sua impressora)
                        int r = ImpressoraDLL.INSTANCE.AbreGaveta(0, 100, 100);
                        System.out.println("Retorno AbreGaveta: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro AbreGaveta: " + t.getMessage());
                    }
                    break;

                case "10": // Sinal Sonoro
                    if (!validaConexao()) break;
                    try {
                        int r = ImpressoraDLL.INSTANCE.SinalSonoro(3, 50, 50);
                        System.out.println("Retorno SinalSonoro: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro SinalSonoro: " + t.getMessage());
                    }
                    break;

                case "11": // Avancar papel (opcional: adicione ao menu se desejar)
                    if (!validaConexao()) break;
                    try {
                        String l = capturarEntrada("Quantas linhas avançar? [padrão 3]: ");
                        int linhas = l.isBlank() ? 3 : Integer.parseInt(l.trim());
                        int r = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
                        System.out.println("Retorno AvancaPapel: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro AvancaPapel: " + t.getMessage());
                    }
                    break;

                case "12": // Corte (opcional)
                    if (!validaConexao()) break;
                    try {
                        String a = capturarEntrada("Tipo de corte (0=parcial,1=total) [padrão 1]: ");
                        int avanco = a.isBlank() ? 1 : Integer.parseInt(a.trim());
                        int r = ImpressoraDLL.INSTANCE.Corte(avanco);
                        System.out.println("Retorno Corte: " + r);
                    } catch (Throwable t) {
                        System.out.println("Erro Corte: " + t.getMessage());
                    }
                    break;

                case "0":
                    System.out.println("Fechando conexão (se aberta) e saindo...");
                    fecharConexao();
                    sair = true;
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }

            System.out.println();
        }

        // Fecha scanner antes de encerrar
        scanner.close();
        System.out.println("Aplicação finalizada.");
    }

    private static void printMenu() {
        System.out.println("\n*************************************************");
        System.out.println("**************** MENU IMPRESSORA *******************");
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
        System.out.println("11 - Avancar Papel (opcional)");
        System.out.println("12 - Corte (opcional)");
        System.out.println("0  - Fechar Conexao e Sair");
        System.out.println("--------------------------------------");
    }

    private static boolean validaConexao() {
        if (!conexaoAberta) {
            System.out.println("Impressora não conectada. Abra a conexão primeiro (opção 2).");
            return false;
        }
        return true;
    }
}
