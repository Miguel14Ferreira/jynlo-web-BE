# Jynlo — auditoria pré-lançamento (web + mobile)
Data: 2026-11-15. Estado: primeira passagem, baseada no código da branch `develop`, sem pentest de produção nem execução de Maven/E2E neste relatório.

## Âmbito e critérios
Repositórios: `jynlo-web-BE`, `jynlo-web-FE`, `jynlo-mobile`.
Prioridade: vulnerabilidades exploráveis, acessos indevidos, privacidade, upload de ficheiros, contas e autenticação, falhas que impeçam publicação, integridade de sessão.
NÃO alterar `master`, não publicar, não mudar infra/secrets sem evidência.

## Achado tratado nesta passagem
### WebSocket /ws/chat — robustez na entrada
**Problema identificado no código:** `ChatRealtimeGateway.handleTextMessage` fazia `objectMapper.readTree(message.getPayload())` sem um limite local explícito de tamanho e sem um tratamento localizado do JSON inválido/estruturas não objeto. Mesmo quando existem limites no container WebSocket, o handler deve rejeitar entrada anómala antes de interpretação.
**Correção implementada:** rejeição antes de Jackson de frames >8192 caracteres (close 1009); JSON malformado, arrays, nulos ou `type` não textual são rejeitados (close 1007); frames válidos continuam no fluxo AUTH/tipagem e verificações de token existentes.
**Testes adicionados:** `ChatRealtimeGatewaySecurityTest` cobre frame excessivo, JSON malformado, estruturas não objeto e `type` inválido, garantindo que não há chamadas ao fornecedor JWT nem ao repositório de perfis nesses casos.
**Limitação:** testes Java ainda precisam de execução num runner Maven para confirmar build e regressões. A correção está apenas em `develop`.

## Segunda passagem — pedidos de convidados de sessões (2026-11-15)
**Achado confirmado em código:** `SessionGuestRequestController` acedia a `body.get("token")` no `guest-preview` e `guest-roster` sem validar `null`. A decisão `guest-requests/{requestId}/decision` também aceitava um mapa sem `approve` e interpretava silenciosamente a omissão como `false`, podendo rejeitar um pedido involuntariamente caso um cliente enviasse um JSON incompleto.
**Correção em `develop`:** corpo `null` rejeitado como HTTP 400; decisão do organizador exige `approve` booleano explícito, preservando `true`/ `false` legítimos; não se alteraram regras de acesso nem os serviços de reserva.
**Testes acrescentados:** `SessionGuestRequestControllerValidationTest`: corpo ausente, `approve` ausente e pass-through das duas decisões. Estes testes precisam de Maven antes de um release.

## Terceira passagem — anexos em conversas privadas e comunidades (2026-11-15)
**Achado P0 (privacidade):** `DirectChatAttachmentService.downloadAt(actor, peer, messageId, index)` consultava `chats.getChatInfo`, mas **não verificava `isChatBlocked()`** para anexos com `index > 0`. O primeiro anexo (`download()`) verificava corretamente. Um utilizador que conhecesse o identificador de mensagem podia continuar a descarregar anexos adicionais após o bloqueio da conversa, desde que a consulta `getChatInfo` retornasse o objeto bloqueado. **Corrigido**: mesma rejeição `AccessDeniedException` antes do acesso a mensagens/armazenamento. O teste já existente `DirectChatAttachmentServiceTest.blockedContactCannotDownloadEvenWhenMessageIdIsKnown` cobre ambos os índices; não foi executado nesta passagem.
**Achado de robustez:** `CommunityTimelineService.downloadChatAttachmentAt` podia desreferenciar um item extra `null`/sem chave ou um serviço de armazenamento não configurado, produzindo 500. **Corrigido**: 404 para metadados em falta e 503 para armazenamento indisponível, depois da verificação de membro. Dois testes adicionados a `CommunityTimelineServiceTest`; execução Java pendente.
**Verificação cruzada:** `SessionChatService.downloadAttachmentAt` já executa autorização de membro, verificação de fecho de chat, validação de índice, chave e armazenamento; `CommunityTimelineService.downloadChatAttachmentAt` já exige ser membro antes de obter conteúdo.

## Controlos inspecionados, não equivalentes a pentest completo
- **BE:** `SecurityConfiguration` exige autenticação por defeito e `/api/admin/**` exige roles; `WebSocketConfiguration` restringe origens com configuração CORS; `ChatRealtimeGateway` valida JWT, estado e tokenVersion, incluindo nas emissões privadas via `send`.
- **BE:** `PrivateProfileMediaController` verifica permissões do seguidor e usa `CacheControl.noStore`; `DirectChatAttachmentController` entrega anexos autenticados como `attachment`, usa `nosniff` e não cache.
- **BE:** `application-prod.properties` desativa a documentação Swagger por defeito, desativa mensagens de erro detalhadas e impede armazenamento de media em filesystem temporário via `desporto.media.provider=s3`.
- **FE:** `AuthInterceptor` restringe JWT a `/api` do origin esperado; refresh através de cookie HttpOnly e tentativas com proteção de corridas; `AdminGuard` consulta acesso no servidor. O JWT da web permanece em `localStorage`, aumentando a necessidade de CSP e prevenção rigorosa de XSS (não migrar armazenamento sem plano de compatibilidade de sessão).
- **Mobile:** `src/lib/api.ts` usa `credentials:'omit'`, `redirect:'error'`, `cache:'no-store'`, timeout e validação da origem; `src/lib/auth.tsx` usa `expo-secure-store` e previne que pedidos antigos reativem sessão após logout.

## Pendente antes de afirmar 'pronto para produção'
1. **Executar os testes Maven** de `ChatRealtimeGatewaySecurityTest` e CI de backend + testes de integração com PostgreSQL, verificando 401/403, suspensão, chat, sessões privadas e permissões de comunidade.
2. Verificar runtime Render real (perfil `prod`, CORS allowlist, segredos, TLS, políticas de headers, logs sem dados sensíveis). O ficheiro global `application.properties` tem fallback `ACTIVE_PROFILE:dev`: confirmar runtime de produção, não presumir.
3. Validar uploads em BE com ficheiros malformados e limites de dimensão/tipo, downloads de anexos sem acesso, privacidade após unfollow/block, media de Story expirada e multi-sessões.
4. Confirmar FE CSP e sinks XSS sobre publicação, Stories, chat e HTML de conteúdos de terceiros. Um JWT em `localStorage` torna XSS especialmente sensível.
5. Auditar dependências com relatórios CVE/SBOM e respetivas versões efetivas no build (Maven, npm, React Native). Não inferir ausência de CVEs de uma leitura do código.
6. Testar fluxos reais em Android Play `0.16.6 (13)` e TestFlight iOS `0.16.6 (1)` após aprovação das lojas: Google login, push, câmera, mapa, Story/publicação, sessão, cancelamento/notificações, chat/call, links profundos, conta privada e remoção da conta.
7. Rever textos públicos de privacidade/termos, retenção, responsáveis pelo tratamento e fornecedores antes do lançamento público.
8. Confirmar revisão/rollout das lojas e track de teste efetivos — AAB aceite ou enviado para review não é aprovação nem instalação validada.

## Política de mudança
Somente correções com evidência, com testes pequenos de regressão como parte do mesmo conjunto funcional; preservar `develop` e não criar branches. Não fazer deploy nem lançar builds só para uma auditoria estática.
