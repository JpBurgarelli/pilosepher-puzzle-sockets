# Problema dos Filósofos comendo - README

## Visão Geral

Esta aplicação simula o problema dos Filósofos Jantando, onde filósofos alternam entre pensar e comer, tentando evitar deadlock e disputas por recursos compartilhados. Os filósofos precisam compartilhar hashi de forma que garanta que nenhum dois filósofos usem o mesmo hashi ao mesmo tempo, utilizando sockets.

## Classes Principais

### `ApplicationLauncher`
- **Propósito**: Ponto de entrada do programa. Inicializa o serviço de jantar criando uma `DiningTable` e chamando seu método `initializeService` para começar a simulação.
- **Ação-chave**: Inicializa o serviço de jantar.

### `DiningTable`
- **Propósito**: Gerencia o serviço de jantar. Fornece assentos, registra filósofos e monitora o status da mesa.
- **Ação-chave**: Cria e gerencia a mesa de jantar, os assentos, os hashis e os filósofos. Escuta conexões de clientes e atribui assentos aos filósofos.

### `TableSeat`
- **Propósito**: Representa um assento na mesa de jantar. Contém os hashis e gerencia o filósofo atribuído ao assento.
- **Ações-chave**:
  - Atribui filósofos aos assentos.
  - Gerencia os hashis de cada assento.
  - Controla os processos de jantar e meditação dos filósofos.

### `Hashi`
- **Propósito**: Representa um Hashi individual  na mesa de jantar. Cada hashi só pode ser usado por um filósofo de cada vez.
- **Ações-chave**:
  - Adquire e libera hashi para um assento específico.
  - Garante que um hashi não possa ser usado por vários filósofos simultaneamente.

### `SessionManager`
- **Propósito**: Gerencia a comunicação entre o servidor e o cliente (representando um filósofo). Processa comandos como entrar, jantar e sair da mesa.
- **Ações-chave**:
  - Gerencia comandos dos filósofos.
  - Processa os comandos: `JOIN`, `RETURN`, `DINE`, `STATUS`, `INFO`, `LEAVE` e `END`.
  - Monitora o estado do assento, status de jantar e sessões ativas.

### `Philosopher`
- **Propósito**: Representa um filósofo na mesa. Acompanha o ID do filósofo, refeições consumidas e meditações realizadas.
- **Ações-chave**:
  - Monitora o estado do filósofo (refeições e meditações).
  - Associa um filósofo a um assento e hashi específicos.
  - Gerencia o comportamento de jantar e contemplação do filósofo.

## Comandos no Menu

A classe `SessionManager` gerencia diversos comandos que o cliente pode enviar para interagir com a simulação:

- **`JOIN`**:
  - Registra um novo filósofo na mesa.
  - Se um filósofo já estiver conectado, retorna um erro.

- **`RETURN`**:
  - Permite que um filósofo previamente conectado volte à mesa usando seu ID.
  - Se o filósofo já estiver ativo, retorna uma mensagem de erro.

- **`DINE`**:
  - Solicita ocupar um assento disponível e começar a jantar.
  - O filósofo deve atingir o limite de meditação para começar a jantar.
  - Se não houver assentos disponíveis, retorna um erro.

- **`STATUS`**:
  - Retorna o status atual da mesa, incluindo disponibilidade de assentos e os filósofos sentados.

- **`INFO`**:
  - Retorna informações sobre o estado atual do filósofo, como o número de refeições consumidas e meditações realizadas.

- **`LEAVE`**:
  - Desconecta o filósofo atual e libera o assento.

- **`END`**:
  - Termina a sessão atual e fecha a conexão.

## Como Funciona

1. O servidor escuta conexões de filósofos.
2. Um filósofo pode "entrar" na mesa enviando o comando `JOIN`. Uma vez conectado, pode solicitar um assento usando o comando `DINE`.
3. Filósofos alternam entre pensar (contemplar) e comer. Eles adquirem hashi para jantar e os liberam após comer.
4. O servidor monitora continuamente o status da mesa, garantindo que cada filósofo tenha sua vez sem conflitos pelos hashi.
5. Filósofos podem verificar seu status usando o comando `INFO` e sair da mesa com o comando `LEAVE`.
6. A simulação termina quando o comando `END` é enviado.

## Executando o Programa

Para executar o programa, siga os passos abaixo:

1. **Inicie o servidor**:
   - Execute `ApplicationLauncher.java`. Isso iniciará o servidor na porta `12345`.

2. **Abre o terminal**
  - Digite no terminal `telnet localhost 12345`.
  - Estará livre para usar os comando (JOIN, DINE, ETC).

2. **Conecte um cliente**:
   - Um cliente (representando um filósofo) pode se conectar usando um socket para interagir com o servidor.

3. **Envie comandos**:
   - O cliente pode enviar os seguintes comandos ao servidor:
     - `JOIN`: Entrar na mesa como um novo filósofo.
     - `RETURN`: Voltar como um filósofo previamente registrado.
     - `DINE`: Tentar ocupar um assento e começar a jantar.
     - `STATUS`: Obter o status atual da mesa.
     - `INFO`: Obter o status atual do filósofo.
     - `LEAVE`: Desconectar o filósofo.
     - `END`: Finalizar a sessão e desconectar.
