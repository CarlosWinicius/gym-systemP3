# Sistema de Gerenciamento de Musculação e Indicadores Biomédicos

Este projeto é um sistema de software para gerenciamento da evolução de atividades de musculação e dos indicadores de saúde associados.

O objetivo é oferecer uma solução completa para academias ou usuários individuais que desejem acompanhar seus planos de treino e evolução biomédica.

## Funcionalidades principais

- Cadastro de plano de treino
- Cadastro de exercícios físicos (nome, descrição, GIF demonstrativo)
- Cadastro das seções de treino (impressão de cartões ou exibição em tela)
- Cadastro de indicadores biomédicos (peso, altura, percentual de gordura, percentual de massa magra, IMC)
- Importação de indicadores biomédicos via CSV
- Exportação de relatórios de evolução de treino e indicadores
- Atualização automática do plano de treino conforme dados reais informados pelo usuário

## Estrutura do plano de treino

- Composto por um conjunto de exercícios físicos.
- Cada exercício contém:
  - Nome
  - Descrição
  - Arquivo GIF (execução correta)
  - Carga
  - Número de repetições
- Durante a execução, caso o usuário informe cargas ou repetições diferentes, o sistema questiona se deseja atualizar o plano de treino.

## Indicadores biomédicos

- Peso, altura, percentual de gordura, percentual de massa magra e IMC podem ser cadastrados a qualquer momento.
- Geração de relatórios:
  - Listagem completa por data
  - Comparativo de evolução no período selecionado
- Suporte à importação em CSV, no formato especificado pelo sistema.

## Multiusuário

- Sistema multiusuário com autenticação.
- Usuário do tipo administrador pode gerenciar (CRUD) as contas de usuários.

## Arquitetura

- Arquitetura em camadas, separando:
  - Camada de apresentação (UI)
  - Camada de negócio (serviços)
  - Camada de persistência (repositórios)
  - Beans para transporte de dados
- Inversão de dependências, facilitando manutenção e testes.

## Testes

- Testes unitários implementados com JUnit para a camada de persistência.

## Tecnologias utilizadas

- Java
- JUnit
- Maven
- CSV parser (por exemplo: OpenCSV)
