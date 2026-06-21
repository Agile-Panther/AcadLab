**AcadLab**

Descrição do Domínio

*Sistema de Gestão Acadêmica*

# **1. O Problema**

A vida acadêmica de uma instituição de ensino superior é feita de dezenas de processos que, sozinhos, parecem simples — matricular um estudante, lançar uma nota, abrir um período letivo — mas que juntos formam uma teia de dependências rígidas. Uma matriz curricular mal configurada compromete a matrícula de centenas de estudantes. Uma janela acadêmica que não é respeitada gera exceções que precisam ser tratadas manualmente. Um histórico desatualizado impede a emissão de um diploma. Uma cobrança gerada sem rastreabilidade vira contestação, e uma contestação mal resolvida vira insatisfação institucional.

Coordenação, secretaria, professores, setor financeiro, assistência estudantil e o próprio estudante operam, hoje, sobre processos fragmentados: planilhas paralelas, e-mails de solicitação, controles manuais de prazo e decisões que não deixam rastro. O resultado é o oposto do que uma instituição de ensino precisa — falta de rastreabilidade nas decisões, prazos descumpridos sem aviso, estudantes em risco acadêmico que não são identificados a tempo, e processos de conclusão de curso travados por pendências que ninguém sabia que existiam.

# **2. O que o AcadLab Resolve**

O AcadLab é uma plataforma de gestão acadêmica integrada, projetada para que toda a trajetória do estudante — da matrícula à colação de grau — seja conduzida sobre uma base única de regras, prazos e registros auditáveis. Seu propósito central é transformar processos que hoje dependem de memória institucional, atendimento presencial e controle manual em fluxos previsíveis, rastreáveis e regidos por regras de negócio explícitas.

A coordenação estrutura o curso — matriz curricular, disciplinas, pré-requisitos — e organiza o calendário de cada período letivo, com suas janelas de matrícula, ajuste, trancamento e revisão. A partir daí, o sistema passa a orquestrar o restante: a oferta de turmas, a montagem da matrícula do estudante, o registro de aulas, frequência e notas pelo professor, e a consolidação de tudo isso no histórico acadêmico oficial.

Além do núcleo letivo, o AcadLab cobre o ciclo de vida acadêmico em sua totalidade: solicitações formais à secretaria sem necessidade de atendimento presencial, validação de integralização curricular e colação de grau, atividades complementares, programas de permanência e bolsas institucionais, apoio psicopedagógico sigiloso, mobilidade acadêmica com instituições externas, gestão financeira de mensalidades e benefícios, e um centro de estágios e oportunidades que conecta estudantes ao mercado. Cada um desses domínios opera com suas próprias regras, mas todos se apoiam na mesma base de verdade sobre quem é o estudante, em que situação ele se encontra e o que já foi oficialmente registrado a seu respeito.

# 

# **3. Linguagem Ubíqua**

A linguagem ubíqua do AcadLab nasce do vocabulário da vida acadêmica institucional. Os termos a seguir têm significado preciso dentro do domínio e são usados de forma consistente em toda a plataforma, tanto na interface quanto nas decisões internas do sistema. Estão organizados pelos subdomínios funcionais aos quais pertencem.

## 3.1 Estrutura Curricular

**Curso** é a unidade acadêmica oferecida pela instituição, à qual se vinculam matriz curricular, período letivo, estudantes e todo o ciclo de formação.

**Matriz Curricular** é a configuração oficial de um Curso, definindo disciplinas obrigatórias e optativas, carga horária mínima, créditos exigidos e limites de trancamento. Existe sempre em estado Ativa ou Inativa, sendo que apenas uma matriz pode estar ativa por Curso simultaneamente. Uma matriz ativa não pode ter sua estrutura de disciplinas alterada nem ser excluída — apenas desativada.

**Disciplina** é a unidade de conhecimento que compõe uma Matriz Curricular, com carga horária e créditos próprios. Pode se relacionar com outras disciplinas por meio de Pré-Requisito, Correquisito e Equivalência.

**Pré-Requisito** é a relação que obriga a aprovação prévia de uma Disciplina para que o estudante possa se matricular em outra. O grafo de pré-requisitos entre disciplinas de uma matriz não pode conter ciclos.

**Correquisito** é a relação que obriga duas Disciplinas a serem cursadas no mesmo Período Letivo, sempre dentro da mesma Matriz Curricular.

**Equivalência** é a relação que permite que uma Disciplina cursada em outro contexto curricular substitua o cumprimento de uma Disciplina da matriz vigente.

## 3.2 Calendário e Oferta

**Período Letivo** é o intervalo de tempo — tipicamente um semestre — que organiza as atividades acadêmicas de um Curso. Não pode haver sobreposição de datas entre dois períodos letivos do mesmo curso, e seu encerramento só ocorre quando não há pendências abertas de notas, frequência ou matrícula em nenhuma de suas turmas.

**Janela Acadêmica** é o intervalo de datas dentro de um Período Letivo durante o qual uma operação específica é permitida — matrícula, ajuste, trancamento, lançamento de notas ou revisão de notas. Fora da janela correspondente, a operação só pode ocorrer por meio de Exceção deferida.

**Turma** é a oferta concreta de uma Disciplina da matriz curricular ativa dentro de um Período Letivo, vinculada a um Professor, uma Sala, um horário, uma capacidade de vagas e, opcionalmente, lista de espera. Possui Situação que reflete seu andamento e não pode ser ofertada fora das datas do período letivo a que pertence.

**Sala** é o espaço físico disponível para alocação de Turmas. Uma sala inativa não pode receber novas turmas, e sua capacidade não pode ser reduzida abaixo da maior turma já vinculada a ela.

**Professor** é o docente responsável pela condução pedagógica de uma ou mais Turmas. Um professor inativo não pode ser vinculado a novas turmas, e não pode haver conflito de horário entre duas turmas do mesmo professor no mesmo período letivo.

## 3.3 Matrícula e Vida do Estudante

**Estudante** é o usuário central do domínio acadêmico — aquele a quem se referem matrícula, histórico, situação discente, solicitações, benefícios, atendimentos, mobilidade e vínculo financeiro.

**Plano de Matrícula** é a seleção provisória de Turmas que o Estudante monta dentro da Janela Acadêmica de matrícula, antes da confirmação. A montagem do plano respeita o cumprimento de Pré-Requisitos, a inclusão de Correquisitos no mesmo período, o limite máximo de créditos da matriz e a ausência de pendências impeditivas.

**Matrícula** é o vínculo confirmado e formal entre o Estudante e uma Turma em um Período Letivo. Só pode ser confirmada havendo vaga disponível e ausência de conflito de horário entre as turmas do plano.

**Trancamento** é a interrupção formal da participação do estudante — seja em uma Disciplina específica, seja no Período Letivo como um todo — sem caracterizar reprovação por falta. Está sujeito à Janela Acadêmica de trancamento e, no caso de período completo, ao limite de trancamentos definido na Matriz Curricular.

**Exceção de Matrícula** é a solicitação pela qual o Estudante pede que a coordenação avalie e libere uma operação de matrícula bloqueada automaticamente pelo sistema. Só produz efeito após deferimento formal da coordenação.

## 3.4 Gestão Pedagógica

**Aula** é o registro de uma sessão de ensino ministrada em uma Turma, incluindo conteúdo abordado. Só pode ser registrada pelo Professor responsável e dentro do intervalo de datas do Período Letivo.

**Frequência** é o registro de presença do Estudante em cada Aula de uma Turma, utilizado para calcular faltas acumuladas e risco de reprovação. Só é registrada para estudantes com Matrícula ativa na turma.

**Avaliação** é o instrumento de aferição de aprendizagem definido pelo Professor para uma Turma, com peso e prazo próprios. A soma dos pesos de todas as avaliações de uma turma deve totalizar 100%, e o prazo de cada avaliação deve respeitar o intervalo do Período Letivo.

**Nota** é o valor atribuído a um Estudante em uma Avaliação específica.

**Resultado Final** é o fechamento oficial da situação do Estudante em uma Turma — aprovação, reprovação por nota, reprovação por falta ou pendência de recuperação — calculado a partir da média ponderada das Notas e do percentual de Frequência. Após o fechamento, alterações só ocorrem por processo formal de revisão.

## 3.5 Histórico e Situação Discente

**Histórico Acadêmico** é o registro oficial e consolidado da trajetória do Estudante, reunindo os Resultados Finais de Turmas encerradas, trancamentos, aproveitamentos e a Situação Discente em cada Período Letivo. Apenas resultados de turmas encerradas são consolidados, e cada lançamento exige uma situação acadêmica final explícita.

**Situação Discente** é o estado oficial do Estudante perante o Curso — ativo, trancado, evadido ou formando. Sua atualização manual exige trilha de auditoria com responsável e justificativa, e um estudante trancado ou evadido não pode ter nova matrícula confirmada sem reativação formal.

**Retificação** é a correção de um Resultado já consolidado no Histórico Acadêmico, realizada preservando o valor anterior e registrando o novo valor com data e responsável, garantindo rastreabilidade da alteração.

**Aproveitamento** é o registro, no Histórico Acadêmico, do cumprimento de uma Disciplina por meio de carga horária cursada externamente (em outra instituição ou em Mobilidade Acadêmica), condicionado à compatibilidade de carga horária com a disciplina equivalente da Matriz Curricular.

## 3.6 Atendimento Administrativo

**Solicitação Acadêmica** é o pedido formal aberto pelo Estudante junto à secretaria ou coordenação, identificado por um Protocolo e podendo exigir Documentos comprobatórios. Cada tipo de solicitação só pode ser aberto dentro do prazo definido no calendário acadêmico, e — com exceção do tipo Revisão de Nota — o estudante não pode manter mais de uma solicitação do mesmo tipo aberta simultaneamente no mesmo Período Letivo.

**Documento** é o comprovante anexado a uma Solicitação Acadêmica para fundamentar o pedido.

**Protocolo** é o identificador único que rastreia o ciclo de vida de uma Solicitação Acadêmica, da abertura à conclusão, incluindo eventuais alterações vinculadas em outros registros do sistema.

## 3.7 Integralização e Conclusão de Curso

**Integralização Curricular** é o estado de cumprimento, pelo Estudante, de todos os requisitos da Matriz Curricular — disciplinas obrigatórias, carga optativa mínima e horas complementares — verificado a partir de registros já consolidados.

**Checklist** é a lista de verificação gerada durante a análise de Integralização Curricular, considerando exclusivamente o Histórico Acadêmico consolidado, Atividades Complementares deferidas e a Situação Discente oficial. Um resultado de análise inapto exige ao menos uma pendência registrada.

**Colação de Grau** é o ato formal de conclusão do Curso, registrado apenas para Estudante com aptidão aprovada pelo Coordenador Acadêmico, com data igual ou posterior à da aprovação da aptidão.

## 3.8 Atividades Complementares

**Atividade Complementar** é uma experiência externa ao currículo regular — curso, evento, monitoria, palestra, projeto, iniciação científica ou extensão — submetida pelo Estudante para aproveitamento de horas acadêmicas, sempre realizada dentro do período de vínculo do estudante com o curso.

**Certificado** é o comprovante vinculado a uma Atividade Complementar, não podendo ser reutilizado em mais de uma submissão.

**Categoria de Horas** é a classificação sob a qual as horas de uma Atividade Complementar são contabilizadas, sujeita a um limite máximo definido na Matriz Curricular. Apenas horas com deferimento formal são contabilizadas no saldo do estudante e na Integralização Curricular.

## 3.9 Permanência Acadêmica e Bolsas

**Edital** é o instrumento que abre formalmente um processo seletivo de permanência acadêmica, definindo vagas, critérios de elegibilidade e prazos. Não pode haver dois editais com inscrições simultaneamente abertas para o mesmo programa.

**Inscrição** é o registro pelo qual o Estudante concorre a um Edital dentro do prazo estabelecido, condicionado ao atendimento de todos os Critérios de Elegibilidade.

**Critério de Elegibilidade** é a condição acadêmica ou socioeconômica que o Estudante deve cumprir para ter sua Inscrição (em um Edital) ou Candidatura (a uma Oportunidade) aceita.

**Classificação** é a ordenação dos candidatos deferidos em um Edital, segundo a pontuação e os critérios de desempate definidos, respeitando o limite de vagas.

**Benefício** é o apoio institucional concedido ao Estudante aprovado em um Edital — bolsa, auxílio transporte, monitoria remunerada ou apoio financeiro — sujeito a renovação periódica e podendo ser suspenso ou cancelado caso o estudante deixe de atender aos critérios mínimos de manutenção.

## 3.10 Apoio Psicopedagógico

**Solicitação de Apoio** é o pedido formal do Estudante por acompanhamento psicopedagógico, ponto de origem de um Caso.

**Caso** é o registro que acompanha a trajetória de uma demanda psicopedagógica do Estudante, da Triagem ao encerramento, podendo ser reaberto mediante nova solicitação formal.

**Triagem** é a classificação obrigatória de prioridade e tipo de acompanhamento, realizada antes de qualquer encaminhamento a Atendimento. As informações de triagem são sigilosas e restritas ao profissional responsável.

**Atendimento** é o registro de uma sessão de acompanhamento psicopedagógico, incluindo observações clínicas, acessível apenas ao profissional responsável e ao próprio Estudante.

**Encaminhamento** é a orientação ou direcionamento definido a partir de um Atendimento ou de uma Candidatura aprovada, podendo, conforme o contexto, indicar a conclusão de um Caso ou o vínculo entre um Estudante e uma Oportunidade.

## 3.11 Mobilidade Acadêmica

**Mobilidade Acadêmica** é o período em que o Estudante cursa disciplinas em outra instituição mantendo o vínculo com o curso de origem, sempre autorizada previamente pela coordenação antes de seu início.

**Plano de Estudos** é a proposta de disciplinas externas submetida pelo Estudante para autorização da Mobilidade Acadêmica; apenas disciplinas aprovadas no plano podem ser aproveitadas após o retorno.

**Comprovante** é o documento de conclusão das disciplinas cursadas externamente, exigido antes que a secretaria registre o resultado da Mobilidade Acadêmica no Histórico Acadêmico.

## 3.12 Gestão Financeira Acadêmica

**Contrato** é o vínculo financeiro formal entre o Estudante e a instituição, ao qual se associam Cobranças e Pagamentos.

**Cobrança** é o valor devido pelo Estudante em um Período Letivo, gerada somente após confirmação de Matrícula. Alterações acadêmicas que impactem o valor geram nova versão da cobrança, preservando o valor original e o motivo do recálculo.

**Bolsa** e **Desconto** são reduções aplicadas ao valor de uma Cobrança, condicionadas a autorização formal previamente registrada.

**Pagamento** é o registro de quitação de uma Cobrança existente, com valor, data e identificador de referência. Seu cancelamento ou estorno reativa automaticamente a Cobrança vinculada, devolvendo-a ao status em aberto.

## 3.13 Estágios e Oportunidades

**Oportunidade** é a vaga de estágio ou posição acadêmica submetida por uma Empresa Parceira, visível aos estudantes somente após validação e publicação pelo Setor de Estágios/Carreiras.

**Empresa Parceira** é a organização externa que submete Oportunidades para avaliação institucional, sem permissão de publicação direta.

**Candidatura** é o registro formal de interesse do Estudante em uma Oportunidade, exigindo atendimento aos Critérios de Elegibilidade e respeito ao prazo de inscrição da vaga.

**Encaminhamento** (neste contexto) é o registro do vínculo formal entre um Estudante com Candidatura deferida e a Empresa Parceira da Oportunidade correspondente.

---

*Este documento estabelece o vocabulário e os conceitos centrais do AcadLab. Ele serve de referência para toda decisão de produto e modelagem, garantindo que todos os envolvidos falem sobre o mesmo domínio com as mesmas palavras.*
