# Testes de Integração - Sistema de Gerenciamento de Academia

## Resumo

Este documento descreve os **60 testes de integração** criados para o sistema de gerenciamento de academia. Todos os testes foram executados com sucesso, integrando múltiplas camadas do sistema (Service + Repository + Persistência CSV).

## Total de Testes no Projeto

- **Testes Totais**: 268
- **Testes de Integração**: 60 (22.4%)
- **Testes Unitários**: 208
- **Taxa de Sucesso**: 100%

## Testes de Integração Criados

### 1. UsuarioIntegrationTest (10 testes)

Testa o fluxo completo de gerenciamento de usuários, desde cadastro até controle de permissões.

**Testes incluem:**
- ✅ Cadastro de usuário e persistência no CSV
- ✅ Fluxo completo de cadastro e autenticação
- ✅ Promoção de usuário comum a admin
- ✅ Rebaixamento de admin a comum (com proteção do admin principal)
- ✅ Validação de email duplicado
- ✅ Validação de formato de email
- ✅ Proteção do administrador principal (ID 1)
- ✅ Busca de usuário por email
- ✅ Validação de campos obrigatórios
- ✅ Validação de autenticação com campos vazios

### 2. ExercicioIntegrationTest (11 testes)

Testa o gerenciamento completo de exercícios personalizados dos usuários.

**Testes incluem:**
- ✅ Cadastro de exercício e persistência
- ✅ Validação de nome duplicado para o mesmo usuário
- ✅ Permissão de mesmo nome para usuários diferentes
- ✅ Listagem de exercícios do usuário (isolamento de dados)
- ✅ Busca de exercício por nome (case insensitive)
- ✅ Deleção de exercício por nome
- ✅ Atualização de exercício existente
- ✅ Validação de nome obrigatório
- ✅ Comportamento ao deletar exercício inexistente
- ✅ Busca de exercício por ID global
- ✅ Trimming automático de nomes

### 3. PlanoTreinoIntegrationTest (12 testes)

Testa a criação e gerenciamento de planos de treino com múltiplos exercícios.

**Testes incluem:**
- ✅ Criação de plano de treino e persistência
- ✅ Validação de nome de plano duplicado
- ✅ Adição de exercício ao plano
- ✅ Criação de plano completo com múltiplos exercícios
- ✅ Validação de exercício duplicado no plano
- ✅ Remoção de exercício do plano
- ✅ Listagem de planos do usuário (isolamento)
- ✅ Edição de nome do plano
- ✅ Validação de exercício de outro usuário
- ✅ Validação de nome obrigatório
- ✅ Remoção de exercício inexistente (erro esperado)
- ✅ Fluxo completo: criar, adicionar, editar e remover

### 4. IndicadorBiomedicoIntegrationTest (12 testes)

Testa o acompanhamento de indicadores biomédicos e evolução do usuário.

**Testes incluem:**
- ✅ Cadastro de indicador com cálculo automático de IMC
- ✅ Uso de data atual quando não informada
- ✅ Validação de valores de peso e altura
- ✅ Validação de percentuais não negativos
- ✅ Listagem de indicadores do usuário (isolamento)
- ✅ Geração de relatório por período
- ✅ Geração de relatório de diferença entre indicadores
- ✅ Validação de datas no relatório
- ✅ Fluxo completo de evolução ao longo do tempo (6 meses)
- ✅ Cálculo correto de IMC para diferentes valores
- ✅ Relatório de período sem indicadores (lista vazia)
- ✅ Múltiplos indicadores na mesma data

### 5. SessaoTreinoIntegrationTest (11 testes)

Testa o registro de sessões de treino e atualização automática de planos.

**Testes incluem:**
- ✅ Iniciar sessão baseada em plano
- ✅ Validação de plano inexistente
- ✅ Validação de plano de outro usuário
- ✅ Registro de execuções de exercícios
- ✅ Salvamento de sessão com execuções
- ✅ Não salvar sessão vazia
- ✅ Geração de sugestões quando execução difere do planejado
- ✅ Não gerar sugestões quando execução é igual ao planejado
- ✅ Aplicação de atualizações no plano baseado na sessão
- ✅ Fluxo completo: criar plano, executar sessão e atualizar
- ✅ Múltiplas sessões do mesmo plano

### 6. SistemaCompletoIntegrationTest (4 testes)

Testes de integração completa simulando cenários reais de uso do sistema.

**Testes incluem:**
- ✅ **Fluxo completo de usuário na academia** (teste mais abrangente):
  - Cadastro de usuário
  - Registro de indicadores biomédicos iniciais
  - Criação de 4 exercícios personalizados
  - Criação de 2 planos de treino (Peito e Pernas)
  - Execução de 3 sessões de treino na primeira semana
  - Evolução e atualização de planos baseada em performance
  - Acompanhamento mensal de indicadores (4 medições ao longo de 3 meses)
  - Análise de resultados (perda de peso, redução de gordura, ganho de massa magra)
  - Verificação de persistência de todos os dados
  
- ✅ **Múltiplos usuários simultâneos**:
  - 3 usuários usando o sistema simultaneamente
  - Isolamento completo de dados entre usuários
  - Exercícios com mesmo nome mas pertencentes a usuários diferentes
  - Planos com mesmo nome mas isolados por usuário
  
- ✅ **Administrador gerenciando usuários**:
  - Criação do admin inicial automático
  - Cadastro de usuários comuns
  - Promoção de usuários a admin
  - Rebaixamento de admins (com proteção do admin principal)
  - Validação de permissões
  
- ✅ **Progressão de treino a longo prazo** (12 semanas):
  - Simulação de 36 sessões de treino (3 por semana)
  - Progressão gradual de carga (2.5kg por semana)
  - Atualização automática do plano a cada semana
  - Evolução de 60kg para 90kg no supino

## Estrutura dos Testes

Todos os testes de integração seguem o padrão:

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class [Nome]IntegrationTest {
    
    // Configuração de repositórios e services reais
    @BeforeEach
    void setUp() throws IOException {
        // Cria diretórios e arquivos CSV de teste
        // Inicializa repositórios com arquivos isolados
        // Injeta dependências reais (não mocks)
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Limpa arquivos CSV de teste
    }
    
    @Test
    @Order(n)
    @DisplayName("Descrição clara do teste")
    void testNomeDescritivo() {
        // Arrange: prepara dados
        // Act: executa ação
        // Assert: verifica resultados
        // Verifica persistência quando aplicável
    }
}
```

## Cobertura de Teste

Os testes de integração cobrem:

### Camadas Testadas
- ✅ **Camada de Serviço** (Business Logic)
- ✅ **Camada de Repositório** (Data Access)
- ✅ **Persistência** (Arquivos CSV)
- ✅ **Integração entre componentes**

### Cenários Testados
- ✅ **Happy Path** (fluxos normais)
- ✅ **Validações** (regras de negócio)
- ✅ **Tratamento de Erros** (casos excepcionais)
- ✅ **Isolamento de Dados** (multi-usuário)
- ✅ **Persistência** (leitura/escrita em CSV)
- ✅ **Fluxos Completos** (cenários reais end-to-end)

### Componentes Integrados
- UsuarioService + UsuarioRepository
- ExercicioService + ExercicioRepository
- PlanoTreinoService + PlanoTreinoRepository + ExercicioRepository
- IndicadorBiomedicoService + IndicadorBiomedicoRepository
- SessaoTreinoService + SessaoTreinoRepository + PlanoTreinoRepository + ExercicioRepository
- Sistema Completo (todos os componentes integrados)

## Execução dos Testes

### Executar apenas testes de integração:
```bash
mvn test -Dtest=br.upe.integration.*IntegrationTest
```

### Executar todos os testes:
```bash
mvn clean test
```

### Gerar relatório de cobertura:
```bash
mvn clean test jacoco:report
```

O relatório será gerado em: `target/site/jacoco/index.html`

## Benefícios dos Testes de Integração

1. **Confiança no Sistema**: Validam que componentes funcionam corretamente juntos
2. **Detecção de Bugs**: Identificam problemas de integração entre camadas
3. **Documentação Viva**: Demonstram como o sistema deve ser usado
4. **Regressão**: Protegem contra quebras acidentais
5. **Persistência**: Validam que dados são corretamente salvos e recuperados
6. **Isolamento**: Garantem que dados de usuários não se misturam

## Padrões e Boas Práticas

Os testes seguem padrões profissionais:

- ✅ **Arrange-Act-Assert** (AAA Pattern)
- ✅ **Testes independentes** (cada teste limpa seus dados)
- ✅ **Nomes descritivos** (DisplayName claro)
- ✅ **Isolamento** (arquivos CSV separados por teste)
- ✅ **Sem mocks** (componentes reais integrados)
- ✅ **Ordenação** (quando relevante para legibilidade)
- ✅ **Verificação de persistência** (dados realmente salvos)

## Conclusão

Os 60 testes de integração criados cobrem os principais fluxos do sistema de gerenciamento de academia, desde operações básicas (CRUD) até cenários complexos de uso real, garantindo que todas as camadas do sistema funcionam corretamente em conjunto.

**Estatísticas Finais:**
- ✅ 268 testes totais executados
- ✅ 100% de taxa de sucesso
- ✅ 60 testes de integração abrangentes
- ✅ Cobertura de todos os componentes principais
- ✅ Validação de fluxos completos end-to-end

