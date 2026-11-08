# Jynlo — Modo Campeonato (proposta técnica)

Estado: proposta; não ativa subscrições nem altera regras de torneios existentes.

## Objetivo
Permitir criar e participar em campeonatos para todas as modalidades do catálogo, sem presumir que todas partilham o mesmo motor de resultados.

## Reutilização
O backend já suporta TournamentFormat.LEAGUE, KNOCKOUT e GROUP_KNOCKOUT, épocas, participantes, equipas, resultados e motores especializados. O modo Campeonato é uma experiência de organização de uma época ou série de provas, não um novo formato de pontuação universal.

## Modos de classificação
- Liga por resultados: apenas nas modalidades e tipos de entrada cujo motor de resultados já é compatível.
- Campeonato por provas: reutilizar os motores existentes de corridas, tempos, marcas e outros eventos verificados.
- Campeonato personalizado: regras explícitas do organizador, incluindo unidade de medida, sentido de ordenação, pontuação por posição, empates e critérios de desempate; até estar implementado, não apresentar classificação automática.
- Airsoft: permitir organização e calendário; pontuação de missões/objetivos apenas depois de contrato próprio e validação de resultados, sem converter para futebol.

## Contrato de segurança e compatibilidade
1. Manter os torneios, épocas e classificações existentes sem migrações destrutivas.
2. Não alterar TournamentFormat nem os resultados já publicados para simular compatibilidade.
3. Não disponibilizar criação com pontuação automática quando o motor devolve UNAVAILABLE.
4. Regras são imutáveis depois de publicados resultados, salvo versionamento explícito.
5. Participantes podem consultar calendários, resultados e classificações sem subscrição.
6. Regras de privacidade das sessões e respetivos locais permanecem independentes do campeonato.
7. Testar permissões no backend; ocultar botões no frontend não é controlo de acesso.

## Monetização proposta (não aprovada)
- Free: participação e consulta, mais funcionalidades gratuitas já existentes.
- Pro Organizador: ferramentas avançadas para campeonatos recorrentes, épocas e relatórios.
- Club: gestão multi-campeonato, múltiplos gestores e relatórios.
- Preços exploratórios: 4,99 EUR/mês Pro e 14,99 EUR/mês Club. Não codificar preços nem limites sem decisão de produto.

## Entitlements
Antes de cobrar: tabela de planos e direitos, verificação server-side nas operações premium, estados de trial/cancelamento/expiração, idempotência de webhooks de pagamento, auditoria e gestão de reembolsos. Nunca bloquear leitura de campeonatos existentes por expiração de um plano.

## Entrega incremental
1. Inventariar compatibilidade por modalidade e motor atual; apresentar claramente o modo disponível.
2. Criar um fluxo de Campeonato que reutiliza épocas, equipas, calendário e resultados.
3. Adicionar regras personalizadas versionadas com testes de ordenação e desempate.
4. Testar E2E com modalidades de liga, corrida, tempo e Airsoft sem resultados automáticos.
5. Só depois introduzir entitlements e checkout, sujeitos a aprovação comercial.

## Critérios de aceitação
- Todas as modalidades podem organizar um campeonato e calendário.
- Nenhuma modalidade recebe regras de resultados de outra por fallback.
- Classificações automáticas apenas com motor validado ou regras personalizadas explícitas.
- Não há regressões nos torneios atuais.
- Jogadores não precisam de pagar para participar ou consultar resultados.
