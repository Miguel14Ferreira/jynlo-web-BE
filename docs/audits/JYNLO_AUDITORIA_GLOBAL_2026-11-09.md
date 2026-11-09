# Jynlo — auditoria global (9 de novembro de 2026)

## Âmbito e limites

Auditoria não destrutiva do GitHub Actions, código FE/BE, Render e registos de issues; não inclui teste presencial da aplicação Android, duas câmaras LiveKit, nem auditoria de penetração autenticada em produção. Não assumir que um CI verde substitui validação funcional real.

## Evidência confirmada

| Área | Estado e evidência |
| --- | --- |
| Frontend Web | `0bebde79` publicado no Render `srv-darsq3bbc2fs738de0mg`; CI `42660176933` verde (2656 testes) e auditoria responsiva PT/EN `42660177148` verde |
| Backend produção | `816be512` publicado no Render `srv-darsqqgjo6nc739a10v0` antes desta auditoria; AutoDeploy desligado |
| Backend develop inicial | `6a3a8941`, seis commits à frente da versão publicada, relacionados com alteração de modalidade em sessões; CI de edição `42646293824` verde |
| Correção ObjectMapper | `ded3c6ce` em develop: configuração explícita de Jackson 2 para snapshots do campeonato, testes unitários de round-trip e de bean pré-existente; CI `42667313467` verde |
| Backend HTTP 500 | Logs de 09/11 15:30 UTC: `CommunityChampionshipService` sem bean `com.fasterxml.jackson.databind.ObjectMapper` no contexto Spring. Corrigido em develop, **ainda não publicado** |
| Estabilidade backend | Eventos Render de várias saídas não-zero (13h–14h UTC) e um `oomKilled` no limite 512 MiB; servidor voltou a ficar disponível. Métricas de RAM perto dos 500 MB. Não atribuir todos os crashes ao ObjectMapper |
| Frontend performance | Bundle inicial aproximado de 2,18 MB e vários avisos CSS em CI; build passa mas requer orçamento de performance |
| Android | Últimos workflows automáticos disponíveis com falhas antes das alterações EAS posteriores; commit `d4a3f346` sem execução recente confirmada. Validação Android e Play não concluída |
| Segurança | FE issues #302 (headers HTTP de produção) e #82 (auditoria de dependências) abertas; não há confirmação de exploração nem prova de que advisories antigos persistem |
| Erro funcional conhecido | BE issue #438: conflito entre jogadores com nomes completos iguais ao registar jornada (HTTP 500), ainda sem fix validado |

## Priorização e critérios de fecho

1. **P0 — BE campeonato:** validar que os endpoints autenticados de campeonatos deixam de produzir 500 por ausência de `ObjectMapper`. CI `42667313467` verde; o deploy de produção continua por fazer após revisão de risco.
2. **P0 — Estabilidade BE:** investigar reinícios e tendência de memória em tráfego real, sem subir heap nem aumentar custos sem medir. Manter uma versão de rollback conhecida.
3. **P1 — Release BE:** validar conjuntamente seis commits de edição de sessões e correção Jackson, executar suite/contratos necessários e só então publicar manualmente no Render (AutoDeploy OFF); confirmar `/actuator/health`, logs e jornadas.
4. **P1 — Segurança:** verificar headers do Render Static Site e `npm audit --omit=dev`, classificar dependências expostas; tratar rate limiting e localização com base nos issues existentes.
5. **P1 — Jornadas:** reproduzir e corrigir homónimos da issue BE #438 sem fundir identidades e sem comprometer importações históricas.
6. **P1 — Android:** recuperar a build de APK/AAB, testar login Google e fluxos principais em dispositivo real; só depois concluir declarações Google Play de privacidade e Data Safety.
7. **P2 — Performance e UX:** reduzir bundle e avisos CSS, confirmar sessões, chat, feed, torneios, campeonatos, traduções, dispositivos e chamadas em cenários autenticados.

## Referências

- FE CI: https://github.com/Miguel14Ferreira/jynlo-web-FE/actions/runs/42660176933
- FE visual: https://github.com/Miguel14Ferreira/jynlo-web-FE/actions/runs/42660177148
- BE edição: https://github.com/Miguel14Ferreira/jynlo-web-BE/actions/runs/42646293824
- BE Jackson: https://github.com/Miguel14Ferreira/jynlo-web-BE/actions/runs/42667313467
- BE homónimos: https://github.com/Miguel14Ferreira/jynlo-web-BE/issues/438
- FE headers: https://github.com/Miguel14Ferreira/jynlo-web-FE/issues/302
- FE npm audit: https://github.com/Miguel14Ferreira/jynlo-web-FE/issues/82
- Android Play: https://github.com/Miguel14Ferreira/jynlo-mobile/issues/24

**Regra operacional:** não declarar a auditoria como 100% concluída nem fazer deploy automático apenas porque os testes unitários passaram.
