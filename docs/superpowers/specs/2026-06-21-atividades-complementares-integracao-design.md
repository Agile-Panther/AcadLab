# Integração de Atividades Complementares — Design

## Objetivo

Tornar funcional o fluxo já existente no frontend para que o estudante selecione uma categoria, informe os dados, submeta uma atividade complementar e veja a confirmação e os saldos atualizados. O comprovante será representado somente pelo nome do arquivo como identificador; armazenamento binário fica fora do escopo.

## Diagnóstico confirmado

O frontend já consome os endpoints sob `/backend/atividades-complementares`, mas as requisições encerram sem resposta porque o Spring não consegue criar `AtividadeComplementarControlador`. A dependência `AtividadeComplementarServico` não está registrada como bean e as quatro portas exigidas pelo construtor não possuem adapters de produção.

O wizard não é a origem do defeito: ele já mantém categoria, descrição, horas, data e identificador do comprovante, chama `POST /submeter`, avança para a confirmação quando a mutação termina e invalida as consultas de atividades.

## Escopo funcional

O fluxo entregue deve permitir:

1. carregar as categorias persistidas;
2. selecionar uma categoria;
3. informar descrição, carga horária, data e comprovante;
4. submeter a atividade do estudante configurado no frontend;
5. persistir a atividade como `PENDENTE`;
6. rejeitar estudante sem matrícula confirmada;
7. rejeitar reutilização do mesmo identificador de certificado pelo mesmo estudante;
8. exibir a confirmação após sucesso;
9. atualizar lista e saldo por invalidação das queries;
10. manter funcionais deferimento, indeferimento, revisão e cancelamento.

## Fora do escopo

- upload, download ou armazenamento do conteúdo do comprovante;
- autenticação e substituição do estudante fixo atual;
- refatoração ampla dos controllers para a camada de aplicação;
- mudança visual do wizard;
- integração assíncrona entre bounded contexts.

## Arquitetura

O contrato HTTP será preservado. A correção ficará concentrada na composição Spring, em adapters JPA das portas de domínio e em ajustes mínimos de estado do wizard para validação, carregamento e erro; não haverá redesenho da tela.

`BackendAplicacao` deve construir `AtividadeComplementarServico` por injeção das seguintes portas:

- `AtividadeComplementarRepositorio`;
- `VerificadorVinculoEstudante`;
- `VerificadorCertificadoDuplicado`;
- `VerificadorLimiteCategoria`;
- `VerificadorContabilizacaoIntegralizacao`;
- `EventoBarramento`.

Os adapters devem residir em infraestrutura e implementar regras consultivas sem introduzir dependência de Spring/JPA no domínio.

## Persistência dos verificadores

### Vínculo do estudante

O vínculo será considerado válido quando existir matrícula do estudante com status `CONFIRMADA`. O modelo persistido atual não associa datas de início e fim à matrícula; portanto, a data recebida pela porta não será usada para inferir uma precisão temporal inexistente no banco.

### Certificado duplicado

A consulta deve verificar a existência de atividade do mesmo estudante com o mesmo `identificadorCertificado`. O nome do arquivo selecionado no frontend é o identificador demonstrativo enviado ao backend.

### Limite da categoria

O adapter deve obter `limiteHoras` da categoria e somar as horas aprovadas de atividades `DEFERIDA` para o mesmo estudante e categoria. O limite é excedido quando `horasDeferidas + horasAdicionais > limiteHoras`.

### Contabilização na integralização

A atividade receberá um campo persistido booleano `contabilizadaIntegralizacao`, com valor padrão `false`, adicionado por migração incremental. O verificador consulta esse campo pelo ID da atividade. Isso evita inferir incorretamente a contabilização individual a partir do checklist global do estudante.

## Contrato HTTP preservado

- `GET /backend/atividades-complementares/categorias`
- `GET /backend/atividades-complementares/estudante/{estudanteId}`
- `GET /backend/atividades-complementares/estudante/{estudanteId}/saldo`
- `POST /backend/atividades-complementares/submeter`
- `POST /backend/atividades-complementares/{id}/deferir`
- `POST /backend/atividades-complementares/{id}/indeferir`
- `POST /backend/atividades-complementares/{id}/solicitar-revisao`
- `DELETE /backend/atividades-complementares/{id}/cancelar`

O payload de submissão continua sendo JSON:

```json
{
  "estudanteId": 1,
  "categoriaId": 3,
  "horas": 20,
  "dataRealizacao": "2026-06-20",
  "identificadorCertificado": "certificado-curso.pdf",
  "descricao": "Curso de arquitetura de software"
}
```

## Tratamento de erros

Violações de regra continuam sendo convertidas pelo `TratadorExcecoes` em respostas JSON com campo `message`. O frontend já converte respostas não bem-sucedidas em `ApiError`; o fluxo deve mostrar a mensagem da mutação e permanecer na etapa de dados, sem apresentar confirmação falsa.

Campos obrigatórios devem impedir o envio vazio. Durante a mutação, o botão de submissão deve permanecer desabilitado para evitar envio duplicado.

## Estratégia de testes

O desenvolvimento seguirá TDD:

1. um teste de composição falha enquanto `AtividadeComplementarServico` não puder ser criado com todas as portas;
2. testes dos adapters comprovam vínculo confirmado, certificado duplicado, limite da categoria e flag de contabilização;
3. teste do endpoint de submissão comprova o payload, persistência em `PENDENTE` e resposta sem erro;
4. testes frontend comprovam validação dos campos e tradução da falha HTTP para mensagem do usuário;
5. a suíte Maven do módulo e do reactor deve permanecer verde;
6. `npm test` e `npm run build` comprovam o fluxo e a compatibilidade do contrato TypeScript;
7. um smoke test contra Docker comprova categorias, submissão, listagem e saldo usando a API real.

## Critérios de aceite

- o endpoint de categorias responde HTTP 200 com as categorias do banco;
- o wizard permite avançar de Categoria para Dados;
- uma submissão válida cria registro `PENDENTE` e alcança a Confirmação;
- a nova atividade aparece na listagem após retornar;
- certificado repetido e estudante sem matrícula confirmada são rejeitados com mensagem de domínio;
- o controller inicia sem `NoSuchBeanDefinitionException`;
- testes Maven, build React e smoke test da API terminam com sucesso.
