# Jynlo — validação do percurso de equipas em torneios e campeonatos

Data: 2026-11-15 | Branches verificadas: `develop` dos três repositórios.

## Percurso funcional pretendido
Equipa permanente Jynlo → membros convidados e aceites → pesquisa/convite/candidatura para competição → consentimento do capitão → finalização pelo organizador → seleção de jogadores elegíveis → convite individual e aceitação → jogos/resultados/histórico. As equipas temporárias de torneio podem ser promovidas explicitamente pelo capitão sem inscrição automática do plantel.

## Evidência implementada
**Backend**
- `SportsTeamController`, `SportsTeamMembershipController`: criação de equipas e gestão de convites/membros.
- `SportsTeamCompetitionInvitationController`, `SportsTeamCompetitionApplicationController`: convite ou candidatura sujeitos a consentimento/decisão.
- `SportsTeamCompetitionEnrollmentService`: finalização exige role OWNER/ADMIN e consentimento; inscrição em torneios apenas no estado DRAFT, verificação de nome/identidade/limite; campeonato não fechado; jogadores selecionados têm de pertencer à equipa permanente e à comunidade. O plantel não entra automaticamente.
- `CommunityTournamentTeamService`: aceitação e primeira administração, transferência confirmada, restrição de múltiplas equipas, conversão opcional em equipa permanente.
- Histórico: `SportsTeamChampionshipHonoursService` e `SportsTeamHonoursService` e testes dedicados.
**Web**
- `community-tournaments.component.html`: `app-competition-team-search` + `app-team-competition-applications`.
- `competition-team-search.component.ts`, `global-team-enrollment.component.ts`: pesquisar equipas, convidar, finalizar e selecionar jogadores.
- `sports-teams.component.ts`: diretório, criação e convites.
**Mobile**
- `app/teams/index.tsx`: criação/pesquisa de equipas e aceitar convites.
- `app/tournaments/[communityId]/[id]/teams.tsx` e `app/communities/[id]/championships/teams.tsx`: criação específica de competição, pesquisa de equipas globais, candidaturas e inscrições.
- `src/components/CompetitionTeamSearch.tsx`, `src/components/GlobalTeamEnrollment.tsx`: convites e seleção individual.
- `app/tournaments/[communityId]/[id]/teams/[teamId].tsx`: proposta para tornar equipa temporária permanente, sem inscrição automática.

## CI realmente executada
- **Backend: sucesso**, run https://github.com/Miguel14Ferreira/jynlo-web-BE/actions/runs/43754705634, Maven com:
  `CommunityTournamentTeamServiceTest`, `CommunityChampionshipTeamsServiceTest`, `SportsTeamCompetitionEnrollmentServiceTest`, `SportsTeamCompetitionInvitationServiceTest`, `SportsTeamCompetitionApplicationServiceTest`, `SportsTeamChampionshipHonoursServiceTest`, `SportsTeamHonoursServiceTest`.
- **Web**: run https://github.com/Miguel14Ferreira/jynlo-web-FE/actions/runs/43754836854 (acompanhar resultado do build).
- **Mobile**: existem contratos `tests/global-team-competition-workflows.test.cjs`, `tests/mobile-tournament-team-permanence.test.cjs`, `tests/mobile-tournament-teams-lifecycle.test.cjs`; nesta passagem **não houve execução integral do runner mobile**. Não confundir existência do teste com PASS.

## Gates ainda por validar antes de declarar E2E
1. Utilizadores reais A e B: responsável da equipa convida jogador B, B aceita; tentativa de inscrição de jogador sem consentimento deve falhar.
2. Organizador C convida equipa global; capitão aceita; C finaliza e convida plantel individualmente; B aceita. Repetir numa segunda comunidade e campeonato com a mesma identidade global, sem misturar participantes.
3. Criar equipa de torneio temporária; primeiro aceite assume administração, transferência a um colega aceite, promover explicitamente, confirmar que o plantel da equipa global não recebe inscrições automáticas.
4. Terminar competição, registar resultado, classificar e verificar histórico/palmarés na web e Android.
5. Validar mensagens de erro, estados sem internet, atualização após voltar ao ecrã e consistência entre sessões.
6. Testes reais em PostgreSQL e dispositivo; não executar operações destrutivas nos dados de utilizadores.

Não houve alteração do modelo de dados, da `master` nem deploy em produção nesta passagem.
