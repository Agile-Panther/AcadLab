
---
## Funcionalidade 1 — F-01 · Gestão Curricular do Curso

| | |
|---|---|
| **Título** | F-01 · Gestão Curricular do Curso |
| **Descrição** | Permite que a coordenação acadêmica estruture oficialmente um curso, definindo matriz curricular, disciplinas obrigatórias, disciplinas optativas, carga horária mínima, créditos exigidos, pré-requisitos, correquisitos e equivalências. É a base do sistema, pois as regras curriculares influenciam matrícula, histórico acadêmico, aproveitamento de disciplinas, integralização curricular e conclusão de curso. |
| **Entidades envolvidas** | Curso, MatrizCurricular, Disciplina, PreRequisito, Correquisito, Equivalencia |

### User Stories e Regras de Negócio

#### US01 — Criar matriz curricular
*Como Coordenador Acadêmico, quero criar uma matriz curricular para um curso, para definir quais disciplinas o estudante precisa cumprir para se formar.*

**RN 1 — Unicidade de disciplina na matriz**

| | |
|---|---|
| Título | Unicidade de disciplina na matriz |
| Descrição | A mesma disciplina não pode aparecer mais de uma vez na mesma matriz curricular. |
| Consultas | Verificar se a disciplina já está associada à matriz curricular informada. |
| Complexidade | Média |

**RN 2 — Suficiência de carga horária e créditos para ativação**

| | |
|---|---|
| Título | Suficiência de carga horária e créditos para ativação |
| Descrição | A matriz curricular só pode ser ativada se a soma da carga horária e dos créditos das disciplinas obrigatórias e optativas disponíveis for suficiente para cumprir a carga horária mínima e os créditos exigidos do curso. |
| Consultas | Somar a carga horária total e o total de créditos de todas as disciplinas vinculadas à matriz; consultar a carga horária mínima e os créditos exigidos configurados no curso. |
| Complexidade | Alta |

---

#### US02 — Configurar pré-requisitos e correquisitos
*Como Coordenador Acadêmico, quero configurar pré-requisitos e correquisitos entre disciplinas da matriz, para controlar a progressão acadêmica dos estudantes.*

**RN 3 — Ausência de ciclos entre pré-requisitos**

| | |
|---|---|
| Título | Ausência de ciclos entre pré-requisitos |
| Descrição | Não podem existir pré-requisitos cíclicos entre disciplinas (ex: A é pré-requisito de B e B é pré-requisito de A). |
| Consultas | Percorrer o grafo de pré-requisitos a partir da disciplina informada para verificar se há caminho de volta até ela mesma. |
| Complexidade | Alta |

**RN 4 — Correquisito pertencente à mesma matriz curricular**

| | |
|---|---|
| Título | Correquisito pertencente à mesma matriz curricular |
| Descrição | Um correquisito deve ser uma disciplina pertencente à mesma matriz curricular. |
| Consultas | Verificar se a disciplina indicada como correquisito pertence à mesma matriz curricular da disciplina de origem. |
| Complexidade | Média |

---

#### US03 — Gerenciar status da matriz curricular
*Como Coordenador Acadêmico, quero ativar e desativar uma matriz curricular, para controlar qual matriz está em uso para matrículas e históricos acadêmicos em cada momento.*

> **Nota:** Esta US cobre o ciclo de vida de status da matriz (ativa/inativa). A ativação e a desativação são operações distintas com regras próprias e representam decisões institucionais relevantes, não apenas um clique simples.

**RN 5 — Unicidade de matriz ativa por curso**

| | |
|---|---|
| Título | Unicidade de matriz ativa por curso |
| Descrição | Não pode existir mais de uma matriz curricular ativa por curso simultaneamente. |
| Consultas | Verificar se já existe uma matriz curricular com status ativo vinculada ao mesmo curso. |
| Complexidade | Média |

**RN 6 — Proibição de exclusão de matriz ativa**

| | |
|---|---|
| Título | Proibição de exclusão de matriz ativa |
| Descrição | Uma matriz curricular ativa não pode ser excluída, apenas desativada. |
| Consultas | Verificar o status atual da matriz curricular a ser excluída. |
| Complexidade | Baixa |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 2 — F-02 · Planejamento Acadêmico do Período Letivo

| | |
|---|---|
| **Título** | F-02 · Planejamento Acadêmico do Período Letivo |
| **Descrição** | Permite que secretaria e coordenação organizem um período letivo, definindo suas datas principais, janelas acadêmicas, regras operacionais e condições para abertura e encerramento do semestre. Controla quando operações como matrícula, ajuste, trancamento, lançamento e revisão de notas podem ocorrer. |
| **Entidades envolvidas** | PeriodoLetivo, JanelaAcademica, Curso |

### User Stories e Regras de Negócio

#### US01 — Cadastrar período letivo
*Como Secretaria Acadêmica, quero cadastrar um período letivo com suas datas e configurações, para organizar as atividades acadêmicas de um semestre.*

**RN 1 — Não sobreposição de períodos letivos do mesmo curso**

| | |
|---|---|
| Título | Não sobreposição de períodos letivos do mesmo curso |
| Descrição | Não podem existir dois períodos letivos com datas sobrepostas para o mesmo curso. |
| Consultas | Verificar se já existe um período letivo, para o mesmo curso, cujo intervalo de datas se sobreponha ao intervalo informado. |
| Complexidade | Média |

---

#### US02 — Definir janelas acadêmicas
*Como Secretaria Acadêmica, quero definir as janelas acadêmicas do período letivo (matrícula, ajuste, trancamento, lançamento e revisão de notas), para controlar automaticamente os prazos de cada operação ao longo do semestre.*

**RN 2 — Operações vinculadas a janelas acadêmicas ativas**

| | |
|---|---|
| Título | Operações vinculadas a janelas acadêmicas ativas |
| Descrição | Cada operação acadêmica deve estar vinculada a uma janela específica; após o encerramento da janela, novos registros dessa operação só podem ocorrer por solicitação excepcional deferida. |
| Consultas | Verificar se a janela acadêmica correspondente à operação solicitada está ativa (data atual dentro do intervalo da janela). |
| Complexidade | Média |

---

#### US03 — Encerrar período letivo
*Como Secretaria Acadêmica, quero encerrar um período letivo — de forma automática ao término das datas configuradas ou manualmente quando necessário —, para consolidar os registros acadêmicos daquele semestre.*

> **Nota:** O encerramento deve ser **preferencialmente automático** (disparado pelo sistema ao atingir a data de fim do período), mas a secretaria deve ter controle manual para casos excepcionais. Ambos os modos devem verificar as mesmas regras de negócio.

**RN 3 — Ausência de pendências para encerramento do período**

| | |
|---|---|
| Título | Ausência de pendências para encerramento do período |
| Descrição | Um período letivo só pode ser encerrado se não houver pendências abertas de notas, frequência ou matrícula em nenhuma de suas turmas. |
| Consultas | Verificar se existem turmas do período letivo com notas não lançadas, frequências não registradas ou matrículas em situação pendente. |
| Complexidade | Alta |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 3 — F-03 · Planejamento e Oferta de Turmas

| | |
|---|---|
| **Título** | F-03 · Planejamento e Oferta de Turmas |
| **Descrição** | Permite que a coordenação gerencie salas e professores disponíveis e transforme disciplinas da matriz curricular em turmas reais para um período letivo, definindo professor, horário, modalidade, capacidade, sala e possibilidade de lista de espera. |
| **Entidades envolvidas** | Turma, Sala, Professor, Disciplina, PeriodoLetivo, MatrizCurricular |

### User Stories e Regras de Negócio

#### US01 — Gerenciar cadastro de salas
*Como Secretaria Acadêmica, quero cadastrar, editar e inativar salas, para manter atualizado o inventário de espaços disponíveis para a oferta de turmas.*

**RN 1 — Sala inativa não pode ser vinculada a turma**

| | |
|---|---|
| Título | Sala inativa não pode ser vinculada a turma |
| Descrição | Uma sala inativa não pode ser vinculada a uma turma. |
| Consultas | Verificar o status atual da sala informada antes de vinculá-la à turma. |
| Complexidade | Baixa |

**RN 2 — Capacidade da sala não pode ser reduzida abaixo das turmas vinculadas**

| | |
|---|---|
| Título | Capacidade da sala não pode ser reduzida abaixo das turmas vinculadas |
| Descrição | A capacidade de uma sala não pode ser reduzida para um valor inferior ao de turmas já vinculadas a ela. |
| Consultas | Verificar a capacidade máxima configurada nas turmas atualmente vinculadas à sala. |
| Complexidade | Média |

---

#### US02 — Gerenciar cadastro de professores
*Como Secretaria Acadêmica, quero cadastrar, editar e inativar professores, para manter atualizado o quadro de docentes disponíveis para vinculação às turmas ofertadas.*

**RN 3 — Professor inativo não pode ser vinculado a turma**

| | |
|---|---|
| Título | Professor inativo não pode ser vinculado a turma |
| Descrição | Um professor inativo não pode ser vinculado a uma turma. |
| Consultas | Verificar o status atual do professor informado antes de vinculá-lo à turma. |
| Complexidade | Baixa |

---

#### US03 — Ofertar turma de uma disciplina
*Como Coordenador Acadêmico, quero ofertar uma turma de uma disciplina da matriz curricular, para disponibilizar vagas aos estudantes no período letivo.*

**RN 4 — Disciplina ofertada pertence à matriz curricular ativa**

| | |
|---|---|
| Título | Disciplina ofertada pertence à matriz curricular ativa |
| Descrição | A disciplina ofertada deve pertencer à matriz curricular ativa do curso. |
| Consultas | Verificar se a disciplina está associada à matriz curricular com status ativo do curso. |
| Complexidade | Média |

**RN 5 — Turma ofertada dentro das datas do período letivo**

| | |
|---|---|
| Título | Turma ofertada dentro das datas do período letivo |
| Descrição | Uma turma só pode ser ofertada dentro das datas do período letivo vigente. |
| Consultas | Verificar se as datas da turma estão contidas no intervalo do período letivo vigente. |
| Complexidade | Baixa |

---

#### US04 — Definir professor, horário, sala e capacidade da turma
*Como Coordenador Acadêmico, quero definir o professor, horário, sala e capacidade de uma turma ofertada, para organizar a operação acadêmica do semestre sem gerar conflitos.*

**RN 6 — Sem conflito de horário do professor no mesmo período**

| | |
|---|---|
| Título | Sem conflito de horário do professor no mesmo período |
| Descrição | Um professor não pode ser vinculado a duas turmas com horários conflitantes no mesmo período letivo. |
| Consultas | Verificar todas as turmas do professor no período letivo e comparar seus horários com o horário da turma sendo cadastrada. |
| Complexidade | Alta |

**RN 7 — Sem conflito de horário da sala no mesmo período**

| | |
|---|---|
| Título | Sem conflito de horário da sala no mesmo período |
| Descrição | Uma sala não pode ser alocada para duas turmas com horários conflitantes no mesmo período letivo. |
| Consultas | Verificar todas as turmas alocadas na sala no período letivo e comparar seus horários com o da nova turma. |
| Complexidade | Alta |

**RN 8 — Capacidade da turma não excede a capacidade da sala**

| | |
|---|---|
| Título | Capacidade da turma não excede a capacidade da sala |
| Descrição | A capacidade da turma não pode exceder a capacidade máxima da sala vinculada. |
| Consultas | Verificar a capacidade máxima registrada para a sala vinculada à turma. |
| Complexidade | Média |

---

#### US05 — Alterar dados de turma ofertada
*Como Coordenador Acadêmico, quero alterar dados de uma turma já ofertada, para corrigir informações sem gerar conflitos acadêmicos para estudantes já matriculados.*

**RN 9 — Alterações não geram conflito de horário para estudantes matriculados**

| | |
|---|---|
| Título | Alterações não geram conflito de horário para estudantes matriculados |
| Descrição | Alterações em turmas com estudantes já matriculados não podem gerar conflito de horário para esses estudantes. |
| Consultas | Verificar a grade de horários de todos os estudantes matriculados na turma e comparar com o novo horário proposto. |
| Complexidade | Alta |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 4 — F-04 · Montagem e Ajuste de Matrícula do Estudante

| | |
|---|---|
| **Título** | F-04 · Montagem e Ajuste de Matrícula do Estudante |
| **Descrição** | Permite que o estudante monte seu plano de matrícula, escolha turmas, confirme matrícula e realize ajustes durante o período permitido, como troca de turma, cancelamento, trancamento de disciplina ou solicitação excepcional. |
| **Entidades envolvidas** | PlanoMatricula, Matricula, Estudante, Turma, PeriodoLetivo, JanelaAcademica |

### User Stories e Regras de Negócio

#### US01 — Montar plano de matrícula
*Como Estudante, quero montar meu plano de matrícula selecionando as turmas que desejo cursar, para planejar minha grade antes de confirmar a matrícula no período letivo.*

**RN 1 — Montagem do plano dentro da janela de matrícula**

| | |
|---|---|
| Título | Montagem do plano dentro da janela de matrícula |
| Descrição | O estudante só pode montar o plano dentro da janela de matrícula definida no calendário acadêmico. |
| Consultas | Verificar se a data atual está dentro da janela de matrícula do período letivo. |
| Complexidade | Média |

**RN 2 — Cumprimento de pré-requisitos antes da matrícula**

| | |
|---|---|
| Título | Cumprimento de pré-requisitos antes da matrícula |
| Descrição | O estudante não pode se matricular em disciplina para a qual não cumpriu os pré-requisitos. |
| Consultas | Verificar no histórico acadêmico do estudante se todas as disciplinas pré-requisitas foram aprovadas. |
| Complexidade | Alta |

**RN 3 — Correquisitos cursados no mesmo período letivo**

| | |
|---|---|
| Título | Correquisitos cursados no mesmo período letivo |
| Descrição | O estudante deve estar matriculado nos correquisitos de uma disciplina no mesmo período letivo. |
| Consultas | Verificar se todas as disciplinas correquisitas estão incluídas no plano de matrícula atual do estudante para o período letivo. |
| Complexidade | Média |

**RN 4 — Limite máximo de créditos por período letivo**

| | |
|---|---|
| Título | Limite máximo de créditos por período letivo |
| Descrição | O estudante não pode exceder o limite máximo de créditos permitido por período letivo. |
| Consultas | Somar os créditos de todas as disciplinas no plano de matrícula atual e comparar com o limite configurado na matriz curricular. |
| Complexidade | Média |

**RN 5 — Ausência de pendências acadêmicas impeditivas**

| | |
|---|---|
| Título | Ausência de pendências acadêmicas impeditivas |
| Descrição | Estudante com pendências acadêmicas impeditivas não pode realizar matrícula. |
| Consultas | Verificar se o estudante possui pendências acadêmicas registradas com caráter impeditivo. |
| Complexidade | Média |

---

#### US02 — Confirmar matrícula
*Como Estudante, quero confirmar minha matrícula nas turmas selecionadas no plano, para garantir minha vaga e formalizar meu vínculo com as disciplinas do período letivo.*

**RN 6 — Vaga disponível na turma para confirmação**

| | |
|---|---|
| Título | Vaga disponível na turma para confirmação |
| Descrição | O estudante não pode confirmar matrícula em turma sem vagas disponíveis. |
| Consultas | Verificar o número de matrículas confirmadas na turma e comparar com a capacidade total. |
| Complexidade | Média |

**RN 7 — Sem conflito de horário entre turmas confirmadas**

| | |
|---|---|
| Título | Sem conflito de horário entre turmas confirmadas |
| Descrição | O estudante não pode confirmar matrícula em duas turmas com horários conflitantes. |
| Consultas | Verificar os horários de todas as turmas no plano de matrícula do estudante e detectar sobreposições. |
| Complexidade | Alta |

---

#### US03 — Realizar ajuste de matrícula
*Como Estudante, quero trocar ou cancelar disciplinas da minha matrícula durante o período de ajuste, para adequar minha grade após a confirmação inicial sem necessidade de intervenção da secretaria.*

**RN 8 — Ajuste de matrícula dentro da janela de ajuste**

| | |
|---|---|
| Título | Ajuste de matrícula dentro da janela de ajuste |
| Descrição | Ajustes de matrícula (troca e cancelamento de disciplinas) só podem ser realizados durante a janela de ajuste definida no calendário acadêmico. |
| Consultas | Verificar se a data atual está dentro da janela de ajuste de matrícula do período letivo. |
| Complexidade | Média |

---

#### US04 — Trancar disciplina
*Como Estudante, quero solicitar o trancamento de uma disciplina específica, para interromper minha participação nela sem ser reprovado por falta, dentro do prazo estabelecido.*

**RN 9 — Trancamento de disciplina dentro da janela de trancamento**

| | |
|---|---|
| Título | Trancamento de disciplina dentro da janela de trancamento |
| Descrição | O trancamento de disciplina só pode ser solicitado durante a janela de trancamento definida no calendário acadêmico. |
| Consultas | Verificar se a data atual está dentro da janela de trancamento do período letivo. |
| Complexidade | Média |

---

#### US05 — Solicitar exceção de matrícula
*Como Estudante, quero solicitar uma exceção às regras de matrícula, para que a coordenação avalie e decida sobre casos específicos bloqueados automaticamente pelo sistema.*

**RN 10 — Exceção efetivada somente após deferimento da coordenação**

| | |
|---|---|
| Título | Exceção efetivada somente após deferimento da coordenação |
| Descrição | A solicitação de exceção deve ser analisada e deferida pela coordenação antes de ser efetivada no sistema. |
| Consultas | Verificar o status da solicitação de exceção para garantir que foi deferida antes de efetivar a operação bloqueada. |
| Complexidade | Média |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 5 — F-05 · Gestão Pedagógica da Turma

| | |
|---|---|
| **Título** | F-05 · Gestão Pedagógica da Turma |
| **Descrição** | Permite que o professor conduza a turma durante o período letivo, registrando aulas, conteúdos ministrados, frequência, avaliações, notas, revisão de notas, recuperação e resultado final. |
| **Entidades envolvidas** | Turma, Aula, Frequencia, Avaliacao, Nota, ResultadoFinal, Estudante, Professor |

### User Stories e Regras de Negócio

#### US01 — Registrar aulas e conteúdos
*Como Professor, quero registrar as aulas ministradas e seus conteúdos, para manter o diário da turma atualizado e comprovar o cumprimento do programa.*

**RN 1 — Apenas o professor responsável pode registrar aulas**

| | |
|---|---|
| Título | Apenas o professor responsável pode registrar aulas |
| Descrição | Apenas o professor responsável pela turma pode registrar aulas. |
| Consultas | Verificar se o usuário logado é o professor responsável pela turma informada. |
| Complexidade | Baixa |

**RN 2 — Aula registrada dentro do intervalo do período letivo**

| | |
|---|---|
| Título | Aula registrada dentro do intervalo do período letivo |
| Descrição | Uma aula só pode ser registrada dentro do intervalo de datas do período letivo. |
| Consultas | Verificar se a data da aula está contida no intervalo do período letivo ao qual a turma pertence. |
| Complexidade | Baixa |

---

#### US02 — Registrar frequência dos estudantes
*Como Professor, quero registrar a frequência dos estudantes por aula, para que o sistema calcule automaticamente presença, faltas acumuladas e risco de reprovação por frequência insuficiente.*

**RN 3 — Frequência registrada apenas para estudantes com matrícula ativa**

| | |
|---|---|
| Título | Frequência registrada apenas para estudantes com matrícula ativa |
| Descrição | A frequência só pode ser registrada para estudantes com matrícula ativa na turma. |
| Consultas | Verificar se o estudante possui matrícula ativa na turma para a qual a frequência está sendo registrada. |
| Complexidade | Média |

**RN 4 — Apenas o professor responsável pode registrar frequência**

| | |
|---|---|
| Título | Apenas o professor responsável pode registrar frequência |
| Descrição | Apenas o professor responsável pela turma pode registrar frequência. |
| Consultas | Verificar se o usuário logado é o professor responsável pela turma informada. |
| Complexidade | Baixa |

---

#### US03 — Criar avaliações com pesos e prazos
*Como Professor, quero criar avaliações definindo seus pesos e prazos, para estruturar como a média final da turma será composta e calculada.*

**RN 5 — Soma dos pesos das avaliações igual a 100%**

| | |
|---|---|
| Título | Soma dos pesos das avaliações igual a 100% |
| Descrição | A soma dos pesos de todas as avaliações de uma turma deve ser igual a 100%. |
| Consultas | Somar os pesos de todas as avaliações já cadastradas para a turma e verificar se o total incluindo a nova avaliação não ultrapassa 100%. |
| Complexidade | Média |

**RN 6 — Prazo da avaliação dentro do período letivo**

| | |
|---|---|
| Título | Prazo da avaliação dentro do período letivo |
| Descrição | O prazo de uma avaliação deve estar dentro do intervalo de datas do período letivo. |
| Consultas | Verificar se a data de prazo da avaliação está contida no intervalo do período letivo ao qual a turma pertence. |
| Complexidade | Baixa |

---

#### US04 — Lançar notas e fechar resultado final
*Como Professor, quero lançar notas das avaliações e fechar o resultado final de cada estudante, para consolidar oficialmente a situação de aprovação, recuperação ou reprovação na turma.*

**RN 7 — Resultado final respeita média mínima e frequência mínima**

| | |
|---|---|
| Título | Resultado final respeita média mínima e frequência mínima |
| Descrição | O resultado final deve respeitar a média mínima e o percentual mínimo de frequência configurados para a turma. |
| Consultas | Calcular a média ponderada das notas e o percentual de frequência do estudante na turma. |
| Complexidade | Alta |

**RN 8 — Alterações após fechamento exigem processo formal de revisão**

| | |
|---|---|
| Título | Alterações após fechamento exigem processo formal de revisão |
| Descrição | Após o fechamento do resultado final, alterações só podem ser realizadas mediante processo formal de revisão registrado no sistema. |
| Consultas | Verificar o status do resultado final da turma para o estudante antes de permitir alteração. |
| Complexidade | Baixa |

---

#### US05 — Solicitar revisão de nota
*Como Estudante, quero solicitar formalmente a revisão de uma nota publicada, para contestar um resultado que considero incorreto dentro do prazo estabelecido.*

**RN 9 — Revisão de nota dentro da janela de revisão**

| | |
|---|---|
| Título | Revisão de nota dentro da janela de revisão |
| Descrição | A solicitação de revisão de nota só pode ser feita durante a janela de revisão de notas definida no calendário. |
| Consultas | Verificar se a data atual está dentro da janela de revisão de notas do período letivo. |
| Complexidade | Média |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 6 — F-06 · Gestão do Histórico Acadêmico e Situação Discente

| | |
|---|---|
| **Título** | F-06 · Gestão do Histórico Acadêmico e Situação Discente |
| **Descrição** | Permite que estudante, secretaria e coordenação acompanhem e atualizem a trajetória acadêmica do aluno, consolidando aprovações, reprovações, trancamentos, aproveitamentos, situação acadêmica e risco de atraso no curso. |
| **Entidades envolvidas** | HistoricoAcademico, Estudante, Disciplina, SituacaoDiscente, PeriodoLetivo, Retificacao, Aproveitamento |

### User Stories e Regras de Negócio

#### US01 — Consolidar resultados no histórico
*Como Secretaria Acadêmica, quero consolidar os resultados finais de disciplinas de períodos encerrados no histórico do estudante, para manter a trajetória acadêmica oficial sempre atualizada.*

**RN 1 — Apenas resultados de turmas encerradas são consolidados**

| | |
|---|---|
| Título | Apenas resultados de turmas encerradas são consolidados |
| Descrição | Apenas resultados finais de turmas encerradas podem ser consolidados no histórico acadêmico. |
| Consultas | Verificar o status da turma cujo resultado está sendo consolidado. |
| Complexidade | Baixa |

**RN 2 — Situação acadêmica final obrigatória na consolidação**

| | |
|---|---|
| Título | Situação acadêmica final obrigatória na consolidação |
| Descrição | Cada disciplina consolidada deve registrar a situação acadêmica final do estudante: aprovado, reprovado por nota, reprovado por falta, trancado ou aproveitado. |
| Consultas | Verificar se o resultado final da turma para o estudante possui situação acadêmica definida antes de consolidar no histórico. |
| Complexidade | Baixa |

---

#### US02 — Solicitar correção de lançamento no histórico
*Como Estudante, quero solicitar a correção de um lançamento incorreto no meu histórico acadêmico, para que inconsistências sejam analisadas e corrigidas pela secretaria com rastreabilidade.*

**RN 3 — Correção efetivada somente após deferimento com registro do responsável**

| | |
|---|---|
| Título | Correção efetivada somente após deferimento com registro do responsável |
| Descrição | Correções de histórico só podem ser efetivadas após deferimento da Secretaria Acadêmica ou Coordenação, com registro do responsável e da justificativa. |
| Consultas | Verificar se a solicitação de correção possui status deferido antes de efetivar a alteração no histórico. |
| Complexidade | Média |

---

#### US03 — Registrar acompanhamento de estudante em risco
*Como Coordenador Acadêmico, quero registrar acompanhamentos para estudantes em atraso curricular ou risco acadêmico, para documentar orientações e encaminhamentos de forma rastreável.*

**RN 4 — Acompanhamento apenas para estudante com matrícula ativa ou situação regular**

| | |
|---|---|
| Título | Acompanhamento apenas para estudante com matrícula ativa ou situação regular |
| Descrição | O acompanhamento só pode ser registrado para estudante com matrícula ativa ou situação acadêmica regular no curso. |
| Consultas | Verificar a situação acadêmica atual do estudante no sistema. |
| Complexidade | Média |

---

#### US04 — Atualizar situação acadêmica do estudante
*Como Secretaria Acadêmica, quero atualizar a situação acadêmica de um estudante (ativo, trancado, evadido, formando), para refletir mudanças oficiais no histórico com rastreabilidade completa.*

**RN 5 — Atualização manual registrada com trilha de auditoria**

| | |
|---|---|
| Título | Atualização manual registrada com trilha de auditoria |
| Descrição | Toda atualização manual da situação acadêmica deve gerar um registro de auditoria contendo data, responsável e justificativa. |
| Consultas | Verificar se o usuário logado possui perfil autorizado para atualizar a situação acadêmica; registrar o log de auditoria vinculado à alteração. |
| Complexidade | Média |

**RN 6 — Reativação formal obrigatória para nova matrícula de estudante trancado ou evadido**

| | |
|---|---|
| Título | Reativação formal obrigatória para nova matrícula de estudante trancado ou evadido |
| Descrição | Estudante com situação trancado ou evadido não pode ter nova matrícula confirmada sem reativação formal da situação acadêmica. |
| Consultas | Verificar a situação acadêmica atual do estudante antes de confirmar nova matrícula. |
| Complexidade | Média |

---

#### US05 — Registrar aproveitamento de disciplinas externas
*Como Secretaria Acadêmica, quero registrar o aproveitamento de disciplinas cursadas em outras instituições no histórico do estudante, para refletir sua trajetória acadêmica completa na integralização curricular.*

**RN 7 — Compatibilidade de carga horária para aproveitamento**

| | |
|---|---|
| Título | Compatibilidade de carga horária para aproveitamento |
| Descrição | O aproveitamento só pode ser registrado se houver compatibilidade de carga horária entre a disciplina externa e a equivalente na matriz curricular. |
| Consultas | Consultar a carga horária da disciplina equivalente na matriz curricular e comparar com a carga horária da disciplina externa. |
| Complexidade | Média |

---

#### US06 — Registrar retificação de resultado consolidado
*Como Secretaria Acadêmica, quero registrar a retificação de um resultado já consolidado no histórico, para corrigir decisões oficiais após o encerramento do período sem perder o histórico de alterações.*

**RN 8 — Retificação preserva resultado anterior com rastreabilidade**

| | |
|---|---|
| Título | Retificação preserva resultado anterior com rastreabilidade |
| Descrição | A retificação deve preservar o resultado anterior e registrar o novo resultado com data e responsável identificado, garantindo rastreabilidade da correção. |
| Consultas | Verificar se o usuário logado possui perfil autorizado para retificar; consultar o resultado anterior antes de sobrescrever. |
| Complexidade | Média |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 7 — F-07 · Secretaria Virtual Acadêmica

| | |
|---|---|
| **Título** | F-07 · Secretaria Virtual Acadêmica |
| **Descrição** | Permite que estudantes abram, acompanhem e resolvam solicitações formais junto à secretaria ou coordenação, sem depender de atendimento presencial, com fluxo de protocolo, prazos e impacto em outros registros do sistema. |
| **Entidades envolvidas** | SolicitacaoAcademica, Documento, Protocolo, Estudante |

### User Stories e Regras de Negócio

#### US01 — Abrir e submeter solicitação acadêmica
*Como Estudante, quero abrir uma solicitação acadêmica e anexar os documentos comprobatórios necessários, para resolver formalmente uma demanda com a secretaria sem precisar de atendimento presencial.*

> **Nota:** O anexo de documentos faz parte do fluxo de abertura da solicitação e não constitui uma ação independente — por isso foi incorporado a esta US.

**RN 1 — Abertura de solicitação dentro do prazo do calendário**

| | |
|---|---|
| Título | Abertura de solicitação dentro do prazo do calendário |
| Descrição | Cada tipo de solicitação só pode ser aberto dentro do prazo definido no calendário acadêmico para aquele tipo. |
| Consultas | Verificar se a data atual está dentro do prazo configurado para o tipo de solicitação no calendário acadêmico. |
| Complexidade | Média |

**RN 2 — Uma solicitação do mesmo tipo por período letivo**

| | |
|---|---|
| Título | Uma solicitação do mesmo tipo por período letivo |
| Descrição | O estudante não pode abrir mais de uma solicitação do mesmo tipo para o mesmo período letivo, salvo quando a anterior tiver sido encerrada. |
| Consultas | Verificar se o estudante possui solicitação do mesmo tipo para o mesmo período letivo com status diferente de encerrado. |
| Complexidade | Média |

**RN 3 — Documentos obrigatórios antes da submissão**

| | |
|---|---|
| Título | Documentos obrigatórios antes da submissão |
| Descrição | Solicitações que exigem documentos obrigatórios não podem ser submetidas sem o anexo dos comprovantes requeridos. |
| Consultas | Verificar quais documentos são obrigatórios para o tipo de solicitação e se todos foram anexados. |
| Complexidade | Média |

---

#### US02 — Analisar solicitações acadêmicas
*Como Secretaria Acadêmica, quero analisar solicitações acadêmicas recebidas e registrar o deferimento ou indeferimento com justificativa, para responder formalmente às demandas dos estudantes e efetivar as alterações necessárias.*

**RN 4 — Solicitação deferida com impacto concluída somente após vinculação das alterações**

| | |
|---|---|
| Título | Solicitação deferida com impacto concluída somente após vinculação das alterações |
| Descrição | Uma solicitação deferida que impacta registros acadêmicos deve identificar quais registros serão alterados e só pode ser concluída após a alteração ficar vinculada ao protocolo. |
| Consultas | Verificar se todas as alterações identificadas no deferimento foram realizadas e vinculadas ao protocolo. |
| Complexidade | Alta |

---

#### US03 — Complementar solicitação com pendências
*Como Estudante, quero complementar uma solicitação já aberta com informações ou documentos adicionais solicitados pela secretaria, para evitar o indeferimento por documentação incompleta.*

**RN 5 — Complementação não permitida em solicitações encerradas ou indeferidas**

| | |
|---|---|
| Título | Complementação não permitida em solicitações encerradas ou indeferidas |
| Descrição | Solicitações em status concluída ou indeferida não podem receber complementação. |
| Consultas | Verificar o status atual da solicitação antes de permitir a complementação. |
| Complexidade | Baixa |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 8 — F-08 · Validação de Integralização Curricular e Colação de Grau

| | |
|---|---|
| **Título** | F-08 · Validação de Integralização Curricular e Colação de Grau |
| **Descrição** | Permite que secretaria e coordenação verifiquem se um estudante está apto à colação de grau com base nas informações já consolidadas no sistema, e registrem oficialmente a decisão. |
| **Entidades envolvidas** | IntegralizacaoCurricular, Checklist, ColacaoDeGrau, Estudante, MatrizCurricular |

### User Stories e Regras de Negócio

#### US01 — Solicitar análise de conclusão de curso
*Como Estudante, quero solicitar a análise de conclusão do meu curso e acompanhar seu andamento, para iniciar formalmente o processo de verificação de aptidão para colação de grau.*

**RN 1 — Solicitação após encerramento do último período letivo cursado**

| | |
|---|---|
| Título | Solicitação após encerramento do último período letivo cursado |
| Descrição | A solicitação só pode ser iniciada após o encerramento do último período letivo cursado pelo estudante. |
| Consultas | Verificar se o período letivo mais recente do estudante foi encerrado. |
| Complexidade | Média |

**RN 2 — Ausência de pendências para início da análise**

| | |
|---|---|
| Título | Ausência de pendências para início da análise |
| Descrição | Pendências acadêmicas ou documentais registradas no sistema impedem o início da análise. |
| Consultas | Verificar se existem pendências acadêmicas ou documentais abertas para o estudante. |
| Complexidade | Média |

---

#### US02 — Analisar integralização e registrar resultado
*Como Secretaria Acadêmica, quero gerar o checklist de integralização curricular, verificar o cumprimento de todos os requisitos e registrar o resultado da análise (apto ou inapto), para manter o histórico oficial da decisão de forma rastreável.*

> **Nota:** Gerar o checklist e registrar o resultado são etapas do mesmo fluxo de análise realizado pela secretaria — por isso foram consolidados em uma única US.

**RN 3 — Checklist baseado apenas em registros consolidados**

| | |
|---|---|
| Título | Checklist baseado apenas em registros consolidados |
| Descrição | O checklist de integralização deve considerar apenas registros acadêmicos consolidados, atividades complementares deferidas e situação discente oficial. |
| Consultas | Consultar o histórico acadêmico consolidado, as atividades complementares com status deferido e a situação discente oficial do estudante. |
| Complexidade | Alta |

**RN 4 — Resultado inapto requer ao menos uma pendência registrada**

| | |
|---|---|
| Título | Resultado inapto requer ao menos uma pendência registrada |
| Descrição | Uma análise com resultado inapto deve possuir ao menos uma pendência registrada. |
| Consultas | Verificar se existe ao menos uma pendência vinculada à análise de integralização antes de registrar o resultado inapto. |
| Complexidade | Baixa |

---

#### US03 — Aprovar aptidão para colação de grau
*Como Coordenador Acadêmico, quero aprovar formalmente a aptidão de um estudante para a colação de grau, para liberar oficialmente a conclusão do curso após verificar o cumprimento integral dos requisitos curriculares.*

**RN 5 — Apenas o Coordenador Acadêmico aprova aptidão para colação**

| | |
|---|---|
| Título | Apenas o Coordenador Acadêmico aprova aptidão para colação |
| Descrição | Somente o Coordenador Acadêmico pode aprovar a aptidão para colação de grau. |
| Consultas | Verificar o perfil do usuário logado antes de permitir a aprovação. |
| Complexidade | Baixa |

**RN 6 — Aptidão exige 100% das obrigatórias, carga optativa mínima e horas complementares**

| | |
|---|---|
| Título | Aptidão exige 100% das obrigatórias, carga optativa mínima e horas complementares |
| Descrição | O estudante só é considerado apto se cumpriu 100% das disciplinas obrigatórias, a carga horária optativa mínima e as horas complementares exigidas pela matriz curricular. |
| Consultas | Verificar no histórico consolidado o cumprimento de todas as disciplinas obrigatórias; somar a carga horária optativa aprovada; somar as horas complementares deferidas e comparar com os requisitos da matriz curricular. |
| Complexidade | Alta |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 9 — F-09 · Gestão de Atividades Complementares e Horas Acadêmicas

| | |
|---|---|
| **Título** | F-09 · Gestão de Atividades Complementares e Horas Acadêmicas |
| **Descrição** | Permite que o estudante submeta atividades externas ao currículo regular para aproveitamento de horas acadêmicas, como cursos, eventos, monitorias, palestras, projetos, iniciação científica e extensão. |
| **Entidades envolvidas** | AtividadeComplementar, Certificado, Estudante, MatrizCurricular, CategoriaHoras |

### User Stories e Regras de Negócio

#### US01 — Submeter atividade complementar
*Como Estudante, quero submeter uma atividade complementar com seu comprovante, para que as horas sejam avaliadas e, se aprovadas, contabilizadas no cumprimento da carga complementar exigida pelo meu curso.*

**RN 1 — Atividade realizada durante o período de vínculo do estudante**

| | |
|---|---|
| Título | Atividade realizada durante o período de vínculo do estudante |
| Descrição | A atividade complementar deve ter sido realizada dentro do período de vínculo do estudante com o curso. |
| Consultas | Verificar as datas de início e fim do vínculo do estudante com o curso e comparar com a data de realização da atividade. |
| Complexidade | Média |

**RN 2 — Mesmo comprovante não pode ser usado em duas atividades**

| | |
|---|---|
| Título | Mesmo comprovante não pode ser usado em duas atividades |
| Descrição | O mesmo certificado ou comprovante não pode ser utilizado para registrar duas atividades complementares diferentes. |
| Consultas | Verificar se o comprovante informado já foi associado a outra atividade complementar do estudante. |
| Complexidade | Média |

---

#### US02 — Analisar atividade complementar
*Como Coordenador Acadêmico, quero analisar as atividades complementares submetidas pelos estudantes e deferir ou indeferir as horas correspondentes, para garantir que apenas horas válidas conforme as regras do curso sejam contabilizadas.*

**RN 3 — Limite máximo de horas por categoria não pode ser excedido**

| | |
|---|---|
| Título | Limite máximo de horas por categoria não pode ser excedido |
| Descrição | O total de horas aproveitadas por categoria não pode exceder o limite máximo definido para aquela categoria na matriz curricular. |
| Consultas | Somar as horas já aproveitadas pelo estudante na mesma categoria e verificar se o total, incluindo a nova atividade, ultrapassa o limite da categoria na matriz curricular. |
| Complexidade | Alta |

---

#### US03 — Solicitar revisão de atividade complementar
*Como Estudante, quero solicitar a revisão de uma atividade complementar indeferida ou parcialmente aproveitada, para contestar a decisão quando tiver justificativa e documentação complementar.*

**RN 4 — Revisão apenas para atividade analisada e não contabilizada na integralização**

| | |
|---|---|
| Título | Revisão apenas para atividade analisada e não contabilizada na integralização |
| Descrição | A revisão só pode ser solicitada para atividade já analisada e ainda não contabilizada na integralização curricular. |
| Consultas | Verificar o status da atividade e se suas horas já foram contabilizadas na integralização curricular do estudante. |
| Complexidade | Média |

**RN 5 — Horas contabilizadas somente após deferimento formal**

| | |
|---|---|
| Título | Horas contabilizadas somente após deferimento formal |
| Descrição | As horas deferidas só são contabilizadas na integralização curricular após o deferimento formal. |
| Consultas | Verificar o status de deferimento da atividade complementar antes de contabilizar as horas. |
| Complexidade | Baixa |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 10 — F-10 · Gestão de Permanência Acadêmica e Bolsas Institucionais

| | |
|---|---|
| **Título** | F-10 · Gestão de Permanência Acadêmica e Bolsas Institucionais |
| **Descrição** | Permite que estudantes se inscrevam em programas de permanência acadêmica, como bolsas institucionais, auxílio transporte, monitoria remunerada e apoio financeiro, com processo seletivo e manutenção do benefício controlados pelo sistema. |
| **Entidades envolvidas** | Edital, Inscricao, Beneficio, Estudante, CriterioElegibilidade, Classificacao |

### User Stories e Regras de Negócio

#### US01 — Criar edital de permanência acadêmica
*Como Secretaria Acadêmica, quero criar um edital de permanência acadêmica definindo regras, vagas e critérios de seleção, para abrir formalmente o processo seletivo de um programa de apoio estudantil.*

**RN 1 — Não pode existir mais de um edital com inscrições abertas para o mesmo programa**

| | |
|---|---|
| Título | Não pode existir mais de um edital com inscrições abertas para o mesmo programa |
| Descrição | Não pode ser criado um edital com prazo de inscrição ativo para um programa que já possui outro edital com inscrições em aberto. |
| Consultas | Verificar se já existe um edital com status de inscrições abertas para o mesmo programa. |
| Complexidade | Média |

---

#### US02 — Inscrever-se em programa de permanência
*Como Estudante, quero me inscrever em um programa de permanência acadêmica dentro do prazo do edital, para concorrer a um benefício institucional que apoia minha continuidade nos estudos.*

**RN 2 — Inscrição dentro do prazo do edital**

| | |
|---|---|
| Título | Inscrição dentro do prazo do edital |
| Descrição | O estudante só pode se inscrever dentro do prazo definido no edital. |
| Consultas | Verificar se a data atual está dentro do prazo de inscrição do edital. |
| Complexidade | Baixa |

**RN 3 — Estudante atende a todos os critérios de elegibilidade**

| | |
|---|---|
| Título | Estudante atende a todos os critérios de elegibilidade |
| Descrição | O estudante deve atender a todos os critérios de elegibilidade do edital para ter a inscrição aceita. |
| Consultas | Verificar os dados acadêmicos e socioeconômicos do estudante em relação a cada critério de elegibilidade definido no edital. |
| Complexidade | Alta |

---

#### US03 — Analisar inscrições e documentos
*Como Assistência Estudantil, quero analisar as inscrições e documentos submetidos pelos candidatos, para deferir apenas os estudantes que comprovadamente atendem aos critérios do edital.*

**RN 4 — Apenas Assistência Estudantil pode deferir ou indeferir inscrições**

| | |
|---|---|
| Título | Apenas Assistência Estudantil pode deferir ou indeferir inscrições |
| Descrição | Somente usuários com perfil de Assistência Estudantil podem registrar o deferimento ou indeferimento de inscrições em editais de permanência. |
| Consultas | Verificar o perfil do usuário logado antes de permitir o deferimento ou indeferimento. |
| Complexidade | Baixa |

---

#### US04 — Gerar classificação preliminar dos candidatos
*Como Assistência Estudantil, quero gerar a lista de classificação preliminar dos candidatos deferidos por pontuação, para apoiar a distribuição justa dos benefícios conforme as regras do edital.*

**RN 5 — Classificação segue pontuação e critérios de desempate do edital**

| | |
|---|---|
| Título | Classificação segue pontuação e critérios de desempate do edital |
| Descrição | A classificação deve seguir a pontuação e os critérios de desempate definidos no edital. |
| Consultas | Consultar todas as inscrições deferidas do edital, calcular a pontuação de cada candidato conforme os critérios e ordenar pelo desempate definido. |
| Complexidade | Alta |

**RN 6 — Número de beneficiários não excede o limite de vagas do edital**

| | |
|---|---|
| Título | Número de beneficiários não excede o limite de vagas do edital |
| Descrição | O número de beneficiários deferidos não pode exceder o limite de vagas definido no edital. |
| Consultas | Contar o número de beneficiários já deferidos no edital e comparar com o número de vagas disponíveis. |
| Complexidade | Média |

---

#### US05 — Solicitar renovação do benefício
*Como Estudante, quero solicitar a renovação do meu benefício de permanência dentro do prazo, para manter o apoio institucional enquanto continuar atendendo aos critérios estabelecidos.*

**RN 7 — Renovação dentro do prazo definido**

| | |
|---|---|
| Título | Renovação dentro do prazo definido |
| Descrição | A renovação só pode ser solicitada dentro do prazo definido para esse fim. |
| Consultas | Verificar se a data atual está dentro do prazo de renovação do benefício. |
| Complexidade | Baixa |

**RN 8 — Benefício suspenso ou cancelado por não cumprimento dos critérios mínimos**

| | |
|---|---|
| Título | Benefício suspenso ou cancelado por não cumprimento dos critérios mínimos |
| Descrição | O benefício pode ser suspenso ou cancelado caso o estudante deixe de atender aos critérios mínimos de manutenção. |
| Consultas | Verificar se o estudante continua atendendo os critérios mínimos de manutenção definidos para o benefício. |
| Complexidade | Alta |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 11 — F-11 · Apoio Psicopedagógico e Acompanhamento Discente

| | |
|---|---|
| **Título** | F-11 · Apoio Psicopedagógico e Acompanhamento Discente |
| **Descrição** | Permite que estudantes solicitem apoio psicopedagógico quando enfrentarem dificuldades que impactem seu desempenho, com triagem, registro de atendimentos e encerramento formal dos casos. |
| **Entidades envolvidas** | SolicitacaoApoio, Caso, Triagem, Atendimento, Encaminhamento, Estudante |

### User Stories e Regras de Negócio

#### US01 — Solicitar apoio psicopedagógico e acompanhar o andamento do caso
*Como Estudante, quero solicitar apoio psicopedagógico e acompanhar o status e os encaminhamentos do meu caso, para receber orientação quando dificuldades estiverem afetando minha vida acadêmica e saber como está evoluindo meu atendimento.*

**RN 1 — Caso encerrado pode ser reaberto via nova solicitação formal**

| | |
|---|---|
| Título | Caso encerrado pode ser reaberto via nova solicitação formal |
| Descrição | Um caso encerrado pode ser reaberto mediante nova solicitação formal do estudante. |
| Consultas | Verificar se o estudante possui caso psicopedagógico com status encerrado para tratar a nova solicitação como reabertura. |
| Complexidade | Média |

---

#### US02 — Realizar triagem das solicitações
*Como Psicopedagogo, quero realizar a triagem das solicitações de apoio recebidas, para classificar a prioridade e definir o tipo de acompanhamento mais adequado a cada caso antes do atendimento.*

**RN 2 — Triagem obrigatória antes do encaminhamento para atendimento**

| | |
|---|---|
| Título | Triagem obrigatória antes do encaminhamento para atendimento |
| Descrição | Cada solicitação deve obrigatoriamente passar por triagem antes de ser encaminhada para atendimento. |
| Consultas | Verificar se a solicitação possui triagem registrada antes de permitir o encaminhamento. |
| Complexidade | Baixa |

**RN 3 — Informações de triagem acessíveis apenas ao profissional responsável**

| | |
|---|---|
| Título | Informações de triagem acessíveis apenas ao profissional responsável |
| Descrição | As informações de triagem são sigilosas e só podem ser acessadas pelo profissional responsável. |
| Consultas | Verificar se o usuário logado é o profissional responsável pela triagem antes de exibir as informações. |
| Complexidade | Baixa |

---

#### US03 — Registrar atendimentos e encaminhamentos
*Como Psicopedagogo, quero registrar os atendimentos realizados, as observações clínicas e os encaminhamentos definidos, para acompanhar a evolução do caso e manter histórico sigiloso do estudante.*

**RN 4 — Informações de atendimento acessíveis apenas ao responsável e ao estudante**

| | |
|---|---|
| Título | Informações de atendimento acessíveis apenas ao responsável e ao estudante |
| Descrição | As informações de atendimento são sigilosas e só podem ser acessadas pelo profissional responsável e pelo próprio estudante. |
| Consultas | Verificar se o usuário logado é o profissional responsável ou o próprio estudante antes de exibir as informações de atendimento. |
| Complexidade | Baixa |

---

#### US04 — Registrar ações de permanência baseadas em indicadores agregados
*Como Coordenação, quero registrar ações institucionais de permanência acadêmica com base em indicadores agregados dos atendimentos, para atuar sobre problemas recorrentes sem acessar dados sigilosos individuais dos estudantes.*

**RN 5 — Ação de permanência sem identificação de estudantes individuais**

| | |
|---|---|
| Título | Ação de permanência sem identificação de estudantes individuais |
| Descrição | A ação de permanência não pode conter identificação de estudantes ou dados individuais de atendimento; deve basear-se apenas em indicadores agregados. |
| Consultas | Verificar se os indicadores utilizados são dados agregados, sem referência a identificadores individuais de estudantes ou atendimentos. |
| Complexidade | Média |

**RN 6 — Ação de permanência registrada apenas pela Coordenação**

| | |
|---|---|
| Título | Ação de permanência registrada apenas pela Coordenação |
| Descrição | A ação de permanência só pode ser registrada por usuário com perfil de Coordenação. |
| Consultas | Verificar o perfil do usuário logado antes de permitir o registro da ação de permanência. |
| Complexidade | Baixa |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 12 — F-12 · Gestão de Mobilidade Acadêmica e Aproveitamento Externo

| | |
|---|---|
| **Título** | F-12 · Gestão de Mobilidade Acadêmica e Aproveitamento Externo |
| **Descrição** | Permite que estudantes solicitem mobilidade acadêmica com análise prévia da coordenação e aproveitamento posterior dos resultados, controlando plano de estudos, autorização e registro no histórico. |
| **Entidades envolvidas** | MobilidadeAcademica, PlanoEstudos, Comprovante, Estudante, HistoricoAcademico |

### User Stories e Regras de Negócio

#### US01 — Solicitar mobilidade acadêmica
*Como Estudante, quero solicitar mobilidade acadêmica para cursar disciplinas em outra instituição, para ampliar minha formação sem perder o vínculo com o meu curso.*

**RN 1 — Mobilidade autorizada previamente pela coordenação**

| | |
|---|---|
| Título | Mobilidade autorizada previamente pela coordenação |
| Descrição | A mobilidade acadêmica deve ser autorizada previamente pela coordenação antes do início do período externo. |
| Consultas | Verificar se a solicitação de mobilidade possui status autorizado antes de registrar o início do período externo. |
| Complexidade | Baixa |

---

#### US02 — Analisar plano de estudos da mobilidade
*Como Coordenador Acadêmico, quero analisar o plano de estudos proposto pelo estudante para a mobilidade, para verificar quais disciplinas externas poderão ser aproveitadas e autorizar formalmente o plano.*

**RN 2 — Apenas disciplinas do plano autorizado podem ser aproveitadas**

| | |
|---|---|
| Título | Apenas disciplinas do plano autorizado podem ser aproveitadas |
| Descrição | Apenas disciplinas aprovadas no plano de estudos autorizado podem ser aproveitadas após o retorno. |
| Consultas | Verificar se a disciplina a ser aproveitada está listada no plano de estudos com status aprovado. |
| Complexidade | Média |

**RN 3 — Carga horária da disciplina externa igual ou superior à equivalente**

| | |
|---|---|
| Título | Carga horária da disciplina externa igual ou superior à equivalente |
| Descrição | O aproveitamento exige que a disciplina cursada externamente possua carga horária igual ou superior à equivalente na matriz curricular. |
| Consultas | Consultar a carga horária da disciplina equivalente na matriz curricular e comparar com a da disciplina cursada externamente. |
| Complexidade | Média |

---

#### US03 — Registrar resultado da mobilidade no histórico
*Como Secretaria Acadêmica, quero registrar os resultados das disciplinas cursadas na mobilidade no histórico acadêmico do estudante, para que o aproveitamento externo reflita corretamente na sua trajetória e integralização curricular.*

> **Nota:** O envio de comprovantes pelo estudante é pré-condição para esta US e está refletido na RN 4. Essa etapa não constitui uma US independente, pois não entrega valor sem o subsequente registro pela secretaria.

**RN 4 — Comprovantes de conclusão obrigatórios antes do registro**

| | |
|---|---|
| Título | Comprovantes de conclusão obrigatórios antes do registro |
| Descrição | Os comprovantes de conclusão das disciplinas cursadas externamente devem estar anexados à mobilidade antes de a secretaria registrar o resultado no histórico. |
| Consultas | Verificar se existem comprovantes vinculados à mobilidade para as disciplinas a serem registradas. |
| Complexidade | Baixa |

**RN 5 — Registro de mobilidade realizado apenas pela secretaria**

| | |
|---|---|
| Título | Registro de mobilidade realizado apenas pela secretaria |
| Descrição | O registro do resultado da mobilidade no histórico só pode ser realizado pela secretaria acadêmica. |
| Consultas | Verificar o perfil do usuário logado antes de permitir o registro. |
| Complexidade | Baixa |

**RN 6 — Resultado registrado apenas para disciplinas com comprovantes e plano autorizado**

| | |
|---|---|
| Título | Resultado registrado apenas para disciplinas com comprovantes e plano autorizado |
| Descrição | Apenas disciplinas com comprovantes anexados e plano previamente autorizado podem ter resultado registrado no histórico. |
| Consultas | Verificar se a disciplina possui comprovante anexado e se está no plano de estudos com status autorizado. |
| Complexidade | Média |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 13 — F-13 · Gestão Financeira Acadêmica

| | |
|---|---|
| **Título** | F-13 · Gestão Financeira Acadêmica |
| **Descrição** | Permite que estudante e setor financeiro acompanhem a vida financeira acadêmica, incluindo mensalidades, contratos, bolsas, descontos, pagamentos, comprovantes e recálculos decorrentes de alterações acadêmicas. |
| **Entidades envolvidas** | Cobranca, Pagamento, Contrato, Bolsa, Desconto, Estudante |

### User Stories e Regras de Negócio

#### US01 — Contestar cobrança acadêmica
*Como Estudante, quero contestar uma cobrança acadêmica que identifiquei como incorreta, para solicitar a revisão formal do valor cobrado referente a mensalidade, bolsa ou desconto.*

**RN 1 — Estudante contesta apenas cobranças do próprio contrato**

| | |
|---|---|
| Título | Estudante contesta apenas cobranças do próprio contrato |
| Descrição | O estudante só pode contestar cobranças vinculadas ao seu próprio contrato acadêmico. |
| Consultas | Verificar se a cobrança informada pertence ao contrato acadêmico do estudante logado. |
| Complexidade | Baixa |

**RN 2 — Contestação permanece pendente até análise do Setor Financeiro**

| | |
|---|---|
| Título | Contestação permanece pendente até análise do Setor Financeiro |
| Descrição | Toda contestação de cobrança, ao ser criada, tem seu status definido como pendente e só pode ser resolvida após análise e parecer do Setor Financeiro. |
| Consultas | Verificar se já existe contestação aberta para a mesma cobrança antes de registrar uma nova. |
| Complexidade | Média |

---

#### US02 — Gerar cobranças acadêmicas
*Como Setor Financeiro, quero gerar cobranças acadêmicas por período letivo, para registrar formalmente os valores devidos e vincular alterações acadêmicas a eventuais recálculos rastreáveis.*

**RN 3 — Geração de cobranças após confirmação de matrícula**

| | |
|---|---|
| Título | Geração de cobranças após confirmação de matrícula |
| Descrição | A geração de cobranças só pode ocorrer após a confirmação de matrícula do estudante no período letivo. |
| Consultas | Verificar se o estudante possui matrícula com status confirmado no período letivo antes de gerar a cobrança. |
| Complexidade | Média |

**RN 4 — Alterações acadêmicas geram nova versão da cobrança com rastreabilidade**

| | |
|---|---|
| Título | Alterações acadêmicas geram nova versão da cobrança com rastreabilidade |
| Descrição | Alterações acadêmicas que impactem cobrança devem gerar uma nova versão da cobrança, preservando o valor original, o motivo do recálculo e a data da alteração. |
| Consultas | Verificar se existe cobrança ativa para o estudante no período letivo antes de gerar a nova versão. |
| Complexidade | Alta |

---

#### US03 — Aplicar bolsa ou desconto autorizado
*Como Setor Financeiro, quero aplicar bolsas e descontos autorizados ao valor da mensalidade do estudante, para recalcular corretamente o montante devido com base nos benefícios vigentes.*

**RN 5 — Desconto ou bolsa aplicados apenas com autorização formal**

| | |
|---|---|
| Título | Desconto ou bolsa aplicados apenas com autorização formal |
| Descrição | Descontos e bolsas só podem ser aplicados mediante autorização formal registrada no sistema. |
| Consultas | Verificar se existe autorização formal registrada para o desconto ou bolsa a ser aplicado. |
| Complexidade | Média |

---

#### US04 — Registrar pagamento de mensalidade
*Como Setor Financeiro, quero registrar o pagamento de mensalidades vinculados às cobranças existentes, para comprovar a regularidade financeira do estudante e liberar eventuais restrições acadêmicas.*

**RN 6 — Pagamento vinculado a cobrança existente**

| | |
|---|---|
| Título | Pagamento vinculado a cobrança existente |
| Descrição | Um pagamento só pode ser registrado quando estiver associado a uma cobrança existente, com valor, data de pagamento e identificador de referência. |
| Consultas | Verificar se a cobrança informada existe no sistema antes de registrar o pagamento. |
| Complexidade | Média |

**RN 7 — Mensalidade em atraso pode gerar restrições em operações acadêmicas**

| | |
|---|---|
| Título | Mensalidade em atraso pode gerar restrições em operações acadêmicas |
| Descrição | Estudante com mensalidade em atraso pode ter restrições aplicadas em operações acadêmicas, conforme regra institucional vigente. |
| Consultas | Verificar se o estudante possui cobranças com data de vencimento ultrapassada e sem pagamento registrado. |
| Complexidade | Média |

### Protótipos da Interface com o Usuário

*(A preencher)*

---

## Funcionalidade 14 — F-14 · Centro de Estágios e Oportunidades

| | |
|---|---|
| **Título** | F-14 · Centro de Estágios e Oportunidades |
| **Descrição** | Permite que empresas e o setor de carreiras publiquem oportunidades de estágio e vagas acadêmicas, que estudantes se candidatem e que a instituição acompanhe encaminhamentos e valide compatibilidade entre vaga e perfil do estudante. |
| **Entidades envolvidas** | Oportunidade, Candidatura, Encaminhamento, EmpresaParceira, Estudante, CriterioElegibilidade |

### User Stories e Regras de Negócio

#### US01 — Cadastrar oportunidade de estágio para avaliação
*Como Empresa Parceira, quero cadastrar uma oportunidade de estágio ou vaga acadêmica no sistema, para que o Setor de Estágios/Carreiras a avalie e, se aprovada, a disponibilize aos estudantes elegíveis.*

> **Nota:** A empresa não publica diretamente — ela submete a oportunidade para avaliação. A publicação efetiva ocorre na US04, realizada pelo Setor de Estágios.

**RN 1 — Oportunidade visível apenas após validação pelo setor de estágios**

| | |
|---|---|
| Título | Oportunidade visível apenas após validação pelo setor de estágios |
| Descrição | A oportunidade só pode ser visualizada pelos estudantes após validação e aprovação pelo Setor de Estágios/Carreiras. |
| Consultas | Verificar o status de aprovação da oportunidade antes de exibi-la para estudantes. |
| Complexidade | Baixa |

---

#### US02 — Definir critérios de elegibilidade da oportunidade
*Como Setor de Estágios/Carreiras, quero definir os critérios de elegibilidade e compatibilidade de cada oportunidade, para garantir que apenas estudantes com o perfil adequado visualizem e se candidatem à vaga.*

**RN 2 — Critérios de elegibilidade não podem ser alterados após publicação**

| | |
|---|---|
| Título | Critérios de elegibilidade não podem ser alterados após publicação |
| Descrição | Uma vez publicada, os critérios de elegibilidade de uma oportunidade não podem ser alterados, para garantir isonomia entre os candidatos. |
| Consultas | Verificar o status atual da oportunidade antes de permitir alteração nos critérios de elegibilidade. |
| Complexidade | Baixa |

---

#### US03 — Candidatar-se a uma oportunidade
*Como Estudante, quero me candidatar a uma oportunidade de estágio ou vaga acadêmica, para registrar formalmente meu interesse e acompanhar o andamento da minha candidatura.*

**RN 3 — Candidatura dentro do prazo de inscrição**

| | |
|---|---|
| Título | Candidatura dentro do prazo de inscrição |
| Descrição | Candidaturas só podem ser registradas dentro do prazo de inscrição definido na oportunidade. |
| Consultas | Verificar se a data atual está dentro do prazo de inscrição da oportunidade. |
| Complexidade | Baixa |

**RN 4 — Estudante atende aos requisitos da oportunidade**

| | |
|---|---|
| Título | Estudante atende aos requisitos da oportunidade |
| Descrição | O estudante só pode se candidatar a oportunidades cujos requisitos (curso, período mínimo, situação acadêmica) ele atenda. |
| Consultas | Verificar os dados acadêmicos do estudante (curso, período atual, situação acadêmica) em relação aos requisitos definidos na oportunidade. |
| Complexidade | Alta |

**RN 5 — Estudante sem pendências impeditivas para vagas com regularidade exigida**

| | |
|---|---|
| Título | Estudante sem pendências impeditivas para vagas com regularidade exigida |
| Descrição | Estudante com pendências acadêmicas impeditivas não pode se candidatar a oportunidades que exijam regularidade. |
| Consultas | Verificar se o estudante possui pendências acadêmicas impeditivas registradas no sistema. |
| Complexidade | Média |

**RN 6 — Oportunidade encerrada não recebe novas candidaturas**

| | |
|---|---|
| Título | Oportunidade encerrada não recebe novas candidaturas |
| Descrição | Uma oportunidade encerrada não pode receber novas candidaturas. |
| Consultas | Verificar o status atual da oportunidade antes de registrar a candidatura. |
| Complexidade | Baixa |

---

#### US04 — Validar e publicar oportunidades cadastradas
*Como Setor de Estágios/Carreiras, quero analisar, validar e publicar as oportunidades cadastradas pelas empresas parceiras, para garantir que apenas vagas adequadas e com critérios bem definidos sejam divulgadas aos estudantes.*

**RN 7 — Publicação exclusiva pelo Setor de Estágios; empresa não tem permissão direta**

| | |
|---|---|
| Título | Publicação exclusiva pelo Setor de Estágios; empresa não tem permissão direta |
| Descrição | A publicação de uma oportunidade só pode ser realizada pelo Setor de Estágios/Carreiras após análise; a empresa parceira não possui permissão de publicação direta. |
| Consultas | Verificar o perfil do usuário logado antes de permitir a publicação da oportunidade. |
| Complexidade | Baixa |

---

#### US05 — Registrar encaminhamento de estudante para oportunidade
*Como Setor de Estágios/Carreiras, quero registrar o encaminhamento formal de um estudante para uma oportunidade, para acompanhar o vínculo entre a formação acadêmica e a inserção profissional dos estudantes da instituição.*

**RN 8 — Encaminhamento apenas para estudante com candidatura deferida**

| | |
|---|---|
| Título | Encaminhamento apenas para estudante com candidatura deferida |
| Descrição | O encaminhamento só pode ser registrado para estudante com candidatura deferida na oportunidade informada. |
| Consultas | Verificar se o estudante possui candidatura com status deferido para a oportunidade informada. |
| Complexidade | Média |

### Protótipos da Interface com o Usuário

*(A preencher)*
