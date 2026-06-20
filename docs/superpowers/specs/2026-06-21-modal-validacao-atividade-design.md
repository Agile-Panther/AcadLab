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

O DTO `AtividadeComplementarResumo` passará a transportar também a data de realização e o identificador do comprovante, já persistidos no backend. O modal será implementado na rota de atividades complementares, reutilizando `Dialog`, `FormField`, `Input`, `Textarea`, `useDeferir` e `useIndeferir`. As regras puras de validação serão extraídas para um módulo pequeno e cobertas por testes unitários com Vitest.

Nenhum endpoint novo será criado: a consulta da fila terá seu DTO ampliado e as mutations existentes executarão as decisões.

## Testes

- Rejeitar horas inferiores a 1.
- Rejeitar horas superiores às submetidas.
- Aceitar horas dentro do intervalo.
- Exigir justificativa no indeferimento.
- Executar o build do frontend e a suíte Vitest sem Docker.
