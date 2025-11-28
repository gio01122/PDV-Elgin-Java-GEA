# Projeto Final — PDV com Impressora Elgin (Java + JNA)

## Sobre o Projeto

Este projeto implementa um PDV (Ponto de Venda) simplificado utilizando Java, integrado a impressoras térmicas Elgin por meio da biblioteca nativa disponibilizada pelo fabricante. A comunicação com a impressora é feita através da DLL da Elgin, acessada pelo Java usando a biblioteca JNA.

O sistema possui um menu interativo no console que permite realizar ações como impressão de texto, impressão de QR Code, códigos de barras, abertura de gaveta, corte de papel, emissão de sinal sonoro e impressão de XML SAT e XML de cancelamento.

## Estrutura do Projeto

```
Projeto_PDV_Elgin/
│
├── libs/
│   ├── jna-5.15.0.jar
│   ├── E1_Impressora01.dll
│
├── src/
│   └── Main.java
│
└── README.md
```

## Tecnologias Utilizadas

* Java 8 ou superior
* JNA 5.15.0
* DLL oficial da Elgin
* Swing (JFileChooser) para seleção de arquivos XML
* Scanner para entrada de dados pelo terminal

## Como Executar o Projeto

### 1. Configuração da JNA no VSCode

Crie ou edite o arquivo:

```
.vscode/settings.json
```

E adicione:

```json
{
  "java.project.referencedLibraries": [
    "libs/**/*.jar"
  ]
}
```

### 2. Coloque a DLL da Elgin na pasta `libs/`

A DLL é carregada no código pelo caminho configurado em `Native.load()`.

### 3. Executando o projeto

Pelo terminal:

```
javac Main.java
java Main
```

Ou utilizando o botão de execução da IDE.

## Menu do Sistema

```
1  - Configurar Conexao
2  - Abrir Conexao
3  - Impressao Texto
4  - Impressao QRCode
5  - Impressao Cod Barras
6  - Impressao XML SAT
7  - Impressao XML Canc SAT
8  - Abrir Gaveta Elgin
9  - Abrir Gaveta
10 - Sinal Sonoro
11 - Avancar Papel
12 - Corte
0  - Fechar Conexao e Sair
```

## Funções da Impressora Utilizadas

O projeto utiliza todas as funções exigidas pela atividade:

* AbreConexaoImpressora
* FechaConexaoImpressora
* ImpressaoTexto
* ImpressaoQRCode
* ImpressaoCodigoBarras
* AvancaPapel
* Corte
* SinalSonoro
* AbreGavetaElgin
* AbreGaveta
* ImprimeXMLSAT
* ImprimeXMLCancelamentoSAT

Essas funções são chamadas diretamente da DLL através das interfaces da JNA.

## Organização do Código

* Funções de conexão: configuração, abertura e fechamento
* Funções de impressão e ações da impressora
* Funções auxiliares para leitura de XML e validação
* Uso de `while` para manter o menu ativo
* Tratamento de opções via `switch`
* Tratamento de erros com `try/catch`

## Testando com Impressora Real

1. Conecte a impressora Elgin via USB, Serial ou Ethernet.
2. Abra o programa.
3. Configure a conexão (opção 1).
4. Abra a conexão (opção 2).
5. Teste as funções desejadas, como impressão de texto ou QR Code.
6. Para impressão de XML, selecione um arquivo `.xml` quando solicitado.

## Requisitos para Funcionamento

* Driver da impressora instalado
* DLL da Elgin disponível
* JNA corretamente configurada
* Impressora conectada e reconhecida pelo sistema

## Licença

Projeto desenvolvido para fins acadêmicos.

## Autor

Projeto produzido como entrega de atividade prática envolvendo integração entre Java e periféricos utilizando DLL nativa da Elgin. Feito po Giovanna Dias Ferreira
