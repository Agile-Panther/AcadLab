# AcadLab - Sistema de Gerenciamento Universitário

> [cite_start]**Versão:** 1.0 [cite: 2]
> **Contexto:** Projeto acadêmico para gerenciamento do ecossistema e processos de uma Instituição de Ensino Superior (IES).

---

## 1. Visão Geral do Domínio

[cite_start]O AcadLab é um sistema de gerenciamento universitário desenvolvido para representar e controlar os processos que compõem a vida acadêmica de uma instituição de ensino superior, desde a definição do currículo de um curso até a conclusão formal de um estudante[cite: 5, 6]. 

[cite_start]O domínio encapsula as regras que determinam as permissões e restrições de cada momento da vida acadêmica: quem pode se matricular, quando notas podem ser lançadas, quais disciplinas estão bloqueadas e se um estudante está apto a se formar[cite: 7]. [cite_start]Cada operação do sistema é governada por essas regras e pelo momento em que é realizada[cite: 8].

[cite_start]O sistema se organiza em torno de três eixos principais[cite: 9]:
* [cite_start]**Estrutura do curso:** O currículo define quais disciplinas existem, a ordem em que devem ser cursadas, quais são obrigatórias, optativas e a carga horária que o estudante precisa cumprir[cite: 10]. [cite_start]Esta estrutura é a base para a existência de matrículas, históricos e conclusões[cite: 11].
* [cite_start]**Trajetória do estudante:** Registrada no histórico acadêmico, inclui as disciplinas cursadas, notas, frequência, aproveitamentos, trancamentos e a situação acadêmica atual, servindo como a fonte de verdade do sistema[cite: 12, 13, 14].
* [cite_start]**Operação do período letivo:** Define datas e janelas semestrais específicas para registros de aulas, notas, matrículas e ajustes[cite: 15, 16]. [cite_start]Fora dessas janelas, as operações são bloqueadas[cite: 17].

---

## 2. Atores do Sistema e Papéis

[cite_start]O ecossistema é composto por múltiplos perfis com responsabilidades bem delimitadas[cite: 19]:

| Ator | Papel e Responsabilidades |
| :--- | :--- |
| **Estudante** | Sujeito central da vida acadêmica. [cite_start]Realiza matrículas, recebe avaliações, acumula histórico e progride em direção à conclusão do curso. [cite: 19] |
| **Professor** | Conduz a turma durante o período letivo. [cite_start]Registra aulas, frequência, avaliações e o resultado final dos estudantes. [cite: 19] |
| **Coordenador Acadêmico** | Define e mantém o currículo do curso. [cite_start]Analisa casos excepcionais, autoriza operações que exigem aprovação e libera estudantes para a colação de grau. [cite: 19] |
| **Secretaria Acadêmica** | [cite_start]Opera os processos formais da instituição: matrículas, períodos letivos, protocolos acadêmicos, históricos e integralização. [cite: 19] |
| **Setor Financeiro** | [cite_start]Gerencia cobranças, descontos, bolsas e comprovantes de pagamento do estudante. [cite: 19] |
| **Assistência Estudantil** | [cite_start]Cuida dos programas de permanência, bolsas institucionais e suporte ao estudante em situação de vulnerabilidade. [cite: 19] |
| **Psicopedagogo** | [cite_start]Realiza triagem, agendamento e acompanhamento psicopedagógico dos estudantes. [cite: 19] |
| **Empresa Parceira** | [cite_start]Publica oportunidades de estágio e vagas profissionais para os estudantes da instituição. [cite: 19] |

---

## 3. Linguagem Onipresente (Dicionário de Termos)

[cite_start]Para garantir a coesão entre o time de desenvolvimento e os requisitos de negócio, os termos abaixo são usados com o mesmo significado por todos os envolvidos no projeto, sem variações ou sinônimos informais[cite: 21, 22].

### Termos Proibidos (Evite Ambiguidades)
* [cite_start]**Não usar Aluno:** Use **Estudante** [cite: 176]
* [cite_start]**Não usar Grade curricular:** Use **Matriz Curricular** [cite: 176]
* [cite_start]**Não usar Status (genérico):** Use **Situação** (discente, da disciplina) ou **Estado** (da turma, da solicitação), sempre qualificado [cite: 177]
* [cite_start]**Não usar Histórico (genérico):** Use **Histórico Acadêmico** quando referente à trajetória formal do estudante [cite: 177]
* [cite_start]**Não usar Aprovação (genérico):** Qualificar sempre como aprovado na disciplina, deferimento da solicitação ou aptidão para colação [cite: 177]
* [cite_start]**Não usar Matrícula (genérico):** Use **Matrícula Institucional** para o identificador do estudante e **Matrícula** para o vínculo com uma turma [cite: 177, 178]

### Conceitos por Áreas Temáticas

#### Currículo e Estrutura do Curso
* [cite_start]**Matriz Curricular:** Estrutura formal que define o conjunto de disciplinas, cargas horárias, pré-requisitos, correquisitos e equivalências exigidos para a conclusão de um curso em uma versão específica[cite: 26].
* **Matriz Ativa:** Versão da matriz curricular vigente, utilizada para novas matrículas. [cite_start]Apenas uma versão pode estar ativa por curso em um mesmo momento[cite: 30, 31].
* **Ciclo de Pré-requisito:** Configuração inválida em que disciplinas formam uma cadeia circular de dependência. [cite_start]O sistema rejeita qualquer configuração que gere esse ciclo[cite: 41, 42].

#### Período Letivo e Calendário
* **Janela Acadêmica:** Intervalo de datas dentro do período letivo durante o qual uma operação específica é permitida. [cite_start]Fora da janela, a operação é bloqueada[cite: 46, 47].
* **Período Encerrado:** Estado do período letivo após o encerramento formal. [cite_start]Os registros tornam-se imutáveis sem um processo formal de reabertura autorizado[cite: 54, 55].

#### Matrícula e Gestão Pedagógica
* **Plano de Matrícula:** Seleção provisória de turmas feita pelo estudante antes da confirmação. [cite_start]Não gera vínculo acadêmico[cite: 68].
* [cite_start]**Reprovação por Frequência:** Resultado atribuído ao estudante cujo percentual de presença ficou abaixo da frequência mínima, independentemente da média obtida nas avaliações[cite: 83].
* [cite_start]**Sigilo de Atendimento:** Princípio que proíbe o acesso de qualquer perfil, exceto o psicopedagogo e o próprio estudante, aos dados individuais de atendimento psicopedagógico[cite: 147].

---

## 4. Tecnologias Utilizadas

*(Substitua os itens abaixo pelas tecnologias reais do seu projeto)*

* **Backend:** Node.js (TypeScript) / Java Spring Boot / C# .NET
* **Banco de Dados:** PostgreSQL / MySQL
* **Autenticação:** JWT com controle de acesso baseado em papéis (RBAC) para os Atores
* **Infraestrutura:** Docker / AWS / GitHub Actions

---

## 5. Fluxo de Trabalho e DevOps (Git e Branches)

Este repositório adota políticas de Branch Rulesets para garantir a integridade do código do projeto:

* **Branch `develop` (Integração):** Exige Pull Request obrigatório com pelo menos 1 aprovação pertencente ao time de revisores (`Team Aprovadores Develop`), utilizando a validação do arquivo `.github/CODEOWNERS` na raiz.
* **Branch `main` (Produção):** Pushes diretos são bloqueados por meio da regra `Restrict updates`. A aprovação e merge de Pull Requests nesta branch é permitida exclusivamente para o Tech Lead configurado na lista de exceções (`Bypass list`).
