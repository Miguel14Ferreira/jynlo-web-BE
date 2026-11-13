# Jynlo — Google Places (New)

As publicações e Stories usam sugestões reais de localizações. O frontend consulta
`GET /api/places/suggestions?q=...&sessionToken=...`; ao escolher um resultado
Google, consulta `GET /api/places/google/{placeId}?sessionToken=...` para
validar as coordenadas antes de o marcar como selecionado.

## Ativar no Render

1. No Google Cloud do projeto autorizado, ativar **Places API (New)** e
   associar uma conta de faturação.
2. Criar uma chave dedicada de servidor, com **restrição de API** para
   **Places API (New)**; não reutilizar a chave do Maps SDK Android ou
   publicar a chave no JavaScript.
3. No serviço backend Jynlo no Render, criar a variável de ambiente
   `GOOGLE_PLACES_API_KEY` com o valor secreto e publicar novamente.
4. Configurar alertas e limites de utilização no Google Cloud Billing.
   Os alertas não são limites rígidos de despesas.
5. Testar localizações como `Agualva-Cacém`, `Lisboa`, `Paris` e
   `New York`. Os resultados Google mostram a atribuição do fornecedor.

Sem chave (ou perante indisponibilidade da API), a Jynlo mantém a pesquisa
Open-Meteo/GeoNames + Photon/OpenStreetMap. Nunca envia chaves Google ao
navegador.

## Controlo de custos e segurança

- Autocomplete começa após 3 caracteres e é sujeito a debounce de 400 ms.
- Uma sessão de pesquisa tem um token separado; Place Details encerra a sessão.
- O proxy impõe limites por utilizador (30 solicitações/minuto,
  200/dia, por instância). Para aplicação em múltiplas instâncias, usar
  um rate limiter centralizado.
- Não são pedidos campos de avaliações, fotografias ou preços.
- A procura usa **viés**, não restrição geográfica; permite resultados mundiais.

Documentação oficial:
- https://developers.google.com/maps/documentation/places/web-service/place-autocomplete
- https://developers.google.com/maps/documentation/places/web-service/place-details
