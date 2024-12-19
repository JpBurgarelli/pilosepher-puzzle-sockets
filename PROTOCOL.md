# Protocolo de Comunicação para o Problema dos Filósofos Jantando

Este documento descreve o protocolo de comunicação para interação com o serviço de simulação do problema dos filósofos jantando. O sistema permite que vários filósofos se conectem, juntem-se à mesa, meditem, jantem e saiam, gerenciando os recursos compartilhados (hashis) de forma a evitar deadlock.

## Visão Geral

A comunicação ocorre em um modelo cliente-servidor, onde os clientes (filósofos) interagem com o servidor (DiningTable) por meio de comandos para participar da simulação, ocupar lugares e interagir com outros filósofos. O servidor responde com atualizações de status, mensagens de sucesso ou erro baseadas nos comandos recebidos.

## Comandos

Os seguintes comandos são suportados para interação:

### 1. **JOIN**
- **Descrição:** Um novo filósofo se junta ao serviço de jantar.
- **Resposta:** Se bem-sucedido, o servidor responde com um ID único para o filósofo. Se o filósofo já estiver conectado, o servidor retorna um erro.

### 2. **RETURN**
- **Descrição:** Um filósofo existente reconecta-se ao servidor usando seu ID de filósofo.
- **Resposta:** O servidor verifica se o filósofo já está conectado e retorna um erro caso esteja. Se o filósofo for encontrado e estiver inativo, o servidor o reconecta.

### 3. **DINE**
- **Descrição:** Um filósofo solicita um lugar à mesa de jantar para começar a comer.
- **Resposta:** 
  - Se o filósofo não alcançou o limite de meditação, um erro será retornado.
  - Se houver um lugar disponível, o filósofo será acomodado e o processo de jantar começará.
  - Se não houver lugares disponíveis, o servidor retornará um erro.

### 4. **STATUS**
- **Descrição:** Solicitação do status atual da mesa, incluindo quais filósofos estão sentados e suas atividades.
- **Resposta:** O servidor envia uma lista de todos os lugares e sua disponibilidade atual (livre ou ocupado por um filósofo). Também fornece o status dos filósofos (quantas refeições e meditações eles completaram).

### 5. **INFO**
- **Descrição:** Solicitação de estatísticas de um filósofo conectado (ex.: número de refeições e meditações).
- **Resposta:** O servidor retorna o estado atual do filósofo, incluindo seu ID e estatísticas.

### 6. **LEAVE**
- **Descrição:** Um filósofo sai do serviço de jantar.
- **Resposta:** O servidor confirma a desconexão do filósofo e libera seu lugar.

### 7. **END**
- **Descrição:** Encerra a sessão atual.
- **Resposta:** O servidor fecha a conexão e encerra a sessão.

## Respostas do Servidor

- **SUCCESS:** Indica que a ação solicitada (ex.: juntar-se, sentar ou jantar) foi concluída com sucesso.
- **ERROR:** Uma mensagem de erro retornada quando o comando solicitado não pode ser processado. Erros comuns incluem comandos inválidos, indisponibilidade de assentos e problemas de sessão.
- **DISCONNECTED:** Indica que o filósofo foi desconectado do serviço.

## Gerenciamento de Lugares e Hashis

- Os filósofos só podem jantar quando ambos os hashis (esquerdo e direito) estiverem disponíveis. Os hashis são compartilhados, e os filósofos devem adquirir ambos antes de comer.
- Caso os hashis estejam em uso, os filósofos devem esperar até que fiquem disponíveis.
