# 🧾 Projeto Final — PDV com Impressora Elgin (Java + JNA)

## 📚 Sobre o Projeto

Este projeto implementa um **PDV (Ponto de Venda) Simplificado**, totalmente funcional e integrado a impressoras térmicas **Elgin**, utilizando:

* Linguagem **Java**
* Biblioteca **JNA (Java Native Access)** para acessar funções nativas da DLL
* **DLL oficial da Elgin** para comunicação direta com a impressora
* Leitura de XML via **JFileChooser**

O sistema é baseado em um **menu interativo**, permitindo operações essenciais de automação comercial, como impressão de texto, QR Code, códigos de barras, abrir gaveta, emitir sinal sonoro, avançar papel, cortar papel e imprimir XML de SAT.

Este README foi preparado para submissão em **GitHub**, seguindo boas práticas de documentação.

---

# 📁 Estrutura do Projeto

```
/Projeto_PDV_Elgin
│
├── libs/
│   ├── jna-5.15.0.jar           # Biblioteca JNA
│   ├── E1_Impressora01.dll      # DLL da impressora Elgin
│
├── src/
│   ├── Main.java                # Código-fonte completo do PDV
│
└── README.md
```

---

# 🔧 Tecnologias Utilizadas

* **Java 8+**
* **JNA 5.15.0**
* **DLL Elgin** (comandos nativos ESC/POS)
* **Swing/JFileChooser** para selecionar XML SAT
* **Scanner** (entrada via terminal)

---

# ▶️ Como Executar o Projeto

## 1️⃣ Instale a JNA no Classpath

Crie o arquivo:

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

## 2️⃣ Coloque a DLL da Elgin na pasta `libs/`

O código carrega a DLL automaticamente pelo caminho configurado no `Main.java`.

## 3️⃣ Compile e execute

Se estiver no terminal:

```
javac Main.java
java Main
```

Ou simplesmente rode pelo VSCode/IntelliJ.

---

# 📟 Menu do Sistema

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

---

# 🖨️ Funções da Impressora Utilizadas

Todas as funções obrigatórias da DLL foram implementadas:

* `AbreConexaoImpressora()`
* `FechaConexaoImpressora()`
* `ImpressaoTexto()`
* `ImpressaoQRCode()`
* `ImpressaoCodigoBarras()`
* `AvancaPapel()`
* `Corte()`
* `SinalSonoro()`
* `AbreGavetaElgin()`
* `AbreGaveta()`
* `ImprimeXMLSAT()`
* `ImprimeXMLCancelamentoSAT()`

Essas funções são chamadas diretamente da DLL via JNA.

---

# 📑 Detalhes de Implementação

## ✔ Organização do Código

O código está dividido em:

* Funções de conexão (`configurarConexao`, `abrirConexao`, `fecharConexao`)
* Funções de impressão
* Funções auxiliares
* Menu principal com laço `while`
* Validação de conexão

## ✔ Estruturas exigidas pela atividade

O projeto utiliza:

* **Laço de repetição `while`** para manter o menu ativo
* **Condicionais `switch`** para tratar as opções
* **Funções próprias** para cada operação
* **Chamada de funções nativas via DLL**
* **Tratamento de exceções** (try/catch)

---

# 🧪 Testando com a Impressora Real

1. Conecte a impressora Elgin via USB/Serial/Ethernet
2. Execute o programa
3. Selecione:

```
1 → Configurar Conexão
2 → Abrir Conexão
```

4. Depois escolha:

```
3 → Imprimir Texto
```

5. Digite qualquer texto e ela imprimirá imediatamente.

Para XML SAT:

```
6 → Impressao XML SAT
```

Será aberta uma janela para selecionar o arquivo `.xml`.

---

# 📦 Requisitos para Impressão Real

✔ Driver da impressora instalado
✔ DLL fornecida pela Elgin
✔ Configuração correta no menu (USB / Serial / IP)
✔ JNA funcionando no projeto

---

# 📄 Licença

Este projeto é acadêmico e livre para uso educacional.

---

# 👤 Autoria

Projeto criado como entrega de atividade prática envolvendo integração com periféricos (impressora térmica) utilizando Java + DLL nativa.
