# Modal de validação de atividade complementar

## Objetivo

Permitir que a coordenação examine os dados enviados pelo estudante antes de deferir ou indeferir uma atividade complementar.

## Fluxo

- O botão **Validar** da fila abre o modal e não altera o estado da atividade.
- O modal apresenta protocolo, estudante, categoria, descrição, data de realização, carga horária submetida e identificador do comprovante.
- A carga horária aprovada inicia com o valor submetido e pode ser editada.
- O deferimento exige uma carga horária aprovada inteira entre 1 e a carga submetida.
- O indeferimento exige uma justificativa não vazia.
- Após uma decisão bem-sucedida, o modal fecha, a fila é atualizada pelas mutations existentes e uma confirmação é exibida.
- Falhas da API mantêm o modal aberto e exibem a mensagem ao coordenador.

## Arquitetura

O modal será implementado na rota de atividades complementares, reutilizando `Dialog`, `FormField`, `Input`, `Textarea`, `useDeferir` e `useIndeferir`. As regras puras de validação serão extraídas para um módulo pequeno e cobertas por testes unitários com Vitest.

Nenhum endpoint novo será criado: os resumos já retornados pela fila contêm os dados necessários e as mutations existentes executam as decisões.

## Testes

- Rejeitar horas inferiores a 1.
- Rejeitar horas superiores às submetidas.
- Aceitar horas dentro do intervalo.
- Exigir justificativa no indeferimento.
- Executar o build do frontend e a suíte Vitest sem Docker.
