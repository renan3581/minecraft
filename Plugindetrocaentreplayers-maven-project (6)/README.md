# Plugindetrocaentreplayers - Plugin Minecraft 1.8.8

## â ï¸ IMPORTANTE: Como Compilar

Este Ã© um projeto Maven que precisa ser compilado para gerar o JAR funcional.

### ð MÃTODO RÃPIDO - CompilaÃ§Ã£o Online (Recomendado)

**1. Usando Replit (GRÃTIS):**
1. Acesse: https://replit.com/
2. Crie uma conta gratuita
3. Click em "Create Repl"
4. Selecione "Java" como linguagem
5. FaÃ§a upload do ZIP baixado
6. No terminal, execute: `mvn clean package`
7. O JAR estarÃ¡ em `target/Plugindetrocaentreplayers.jar`

**2. Usando GitHub Codespaces (GRÃTIS):**
1. Suba os arquivos para um repositÃ³rio GitHub
2. Abra o Codespace
3. Execute: `mvn clean package`
4. Baixe o JAR de `target/Plugindetrocaentreplayers.jar`

### ð» CompilaÃ§Ã£o Local

**Windows:**
1. Instale o Java JDK 8: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
2. Instale o Maven: https://maven.apache.org/download.cgi
3. Extraia o ZIP baixado
4. Abra o CMD na pasta do projeto
5. Execute: `mvn clean package`
6. O JAR estarÃ¡ em `target\Plugindetrocaentreplayers.jar`

**Mac/Linux:**
1. Instale via terminal:
   - `sudo apt install maven` (Ubuntu/Debian)
   - `brew install maven` (Mac)
2. Extraia o ZIP
3. Execute: `mvn clean package`
4. O JAR estarÃ¡ em `target/Plugindetrocaentreplayers.jar`

### ð± IDE (Eclipse/IntelliJ)
1. Importe como projeto Maven existente
2. Aguarde sincronizaÃ§Ã£o
3. Execute: Maven > Lifecycle > package
4. Pegue o JAR em `target/`

## ð¦ InstalaÃ§Ã£o no Servidor
1. Copie o JAR compilado de `target/Plugindetrocaentreplayers.jar`
2. Cole na pasta `plugins` do servidor Minecraft
3. Reinicie o servidor
4. Pronto! O plugin estÃ¡ funcionando

## ð® Comandos
- `/trocar <jogador>` - Inicia uma troca
- `/aceitar <jogador>` - Aceita uma solicitaÃ§Ã£o
- `/recusar <jogador>` - Recusa uma solicitaÃ§Ã£o

## ð PermissÃµes
- `troca.usar` - Permite usar os comandos (padrÃ£o: true)

## â Problemas?
- Certifique-se de estar usando Java 8
- Verifique se o Maven estÃ¡ instalado: `mvn --version`
- Compile sempre com: `mvn clean package`
