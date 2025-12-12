# Contributing

Regras que envolvem o processo de desenvolvimento e contribuição do projeto, auxiliando a manutenção do fluxo organizado e consistente.

---

## 🔀 Estratégia de Branches
-   **`main`** → Código em produção.

-   **`homologation`** → Branch para testes em ambiente de homologação.

-   **`ci-test`** → Branch para testes de integração contínua.

-   **`tests`** → Branch para testes.

-   **`development`** → Branch principal de integração.
    -   Deve receber solicitações de merge de `feature/*` ou `bugfix/*`.
    -   Integração contínua de features.
    -   Workflow de testes automatizados, garantindo a estabilidade para futuras integrações.

-   **`feature/*`** → Novas funcionalidades.
    -   Exemplo: `feature/login-screen`.

-   **`bugfix/*`** → Correções que não estão em produção ainda.
    -   Exemplo: `bugfix/fix-null-pointer`.

-   **`hotfix/*`** → Correções críticas em produção.
    -   Exemplo: `hotfix/fix-auth-token`.

---

## 💻 Fluxo de Desenvolvimento

**1. Crie uma branch a partir de `development`:**
```
git checkout development
git pull origin development
git checkout -b feature/nome-da-feature
```

**2. Faça commits pequenos e descritivos:**
  ```
  feat: add login validation
  fix: resolve crash when user logs out
  ```

**3. Abra um Pull Request (PR):**
-   **De:** `feature/*` ou `bugfix/*`
-   **Para:** `development`

Regras do PR:
-   Descrição clara da mudança.
-   Issue relacionada (Ex: `Closes #123`).
-   Prints ou logs, se aplicável.

---

## 🚀 Deploy

-   **`development`** → Ambiente de Desenvolvimento.
-   **`tests`** → Ambiente de testes.
-   **`ci-test`** → Ambiente de testes de integração contínua.
-   **`hml`** → Ambiente de Homologação.
-   **`main`** → Produção (merge via PR, depois de validação).
