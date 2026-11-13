package com.example.desporto24.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Optional server-side Google Places (New) proxy. The API key is never returned
 * to the browser. If not configured, clients use their non-Google fallback. */
@Service
public class GooglePlaceLookupService {
    private final ObjectMapper json = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .followRedirects(HttpClient.Redirect.NEVER).build();
    private static final Pattern PLACE_ID = Pattern.compile("[A-Za-z0-9_-]{4,256}");
    private static final Pattern SESSION = Pattern.compile("[A-Za-z0-9_-]{8,36}");

    @Value("${GOOGLE_PLACES_API_KEY:}") private String apiKey;
    private final java.util.concurrent.ConcurrentHashMap<String,Quota> quotas =
        new java.util.concurrent.ConcurrentHashMap<>();
    private static final class Quota {
        long minuteStart,dayStart;
        int minuteCount,dayCount;
    }
    private void throttle(String viewer) {
        long now=System.currentTimeMillis();
        Quota state=quotas.computeIfAbsent(viewer,k->new Quota());
        synchronized(state) {
            if(now-state.minuteStart>=60000) {
                state.minuteStart=now; state.minuteCount=0;
            }
            if(now-state.dayStart>=86400000) {
                state.dayStart=now; state.dayCount=0;
            }
            if(state.minuteCount>=30 || state.dayCount>=200)
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Limite de pesquisa atingido");
            state.minuteCount++;
            state.dayCount++;
        }
    }

    public record Suggestion(String id,String label,String subtitle) {}
    public record Resolved(String id,String label,String subtitle,double latitude,double longitude) {}
    public record Suggestions(String provider,List<Suggestion> places) {}

    public Suggestions suggest(String value,String session) {
        return suggest(value,session,"anonymous");
    }
    public Suggestions suggest(String value,String session,String viewer) {
        requireEnabled();
        throttle(viewer);
        String query=value==null?"":value.strip();
        if(query.length()<3 || query.length()>100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Pesquisa inválida");
        var request = new java.util.LinkedHashMap<String,Object>();
        request.put("input",query);
        request.put("languageCode","pt");
        // A bias helps with local names, but does not restrict worldwide results.
        request.put("locationBias",Map.of("circle",Map.of(
            "center",Map.of("latitude",38.73,"longitude",-9.16),
            "radius",50000)));
        if(session != null && SESSION.matcher(session).matches()) request.put("sessionToken",session);
        JsonNode response=post("https://places.googleapis.com/v1/places:autocomplete",
            request,"suggestions.placePrediction.placeId,suggestions.placePrediction.structuredFormat");
        List<Suggestion> items=new ArrayList<>();
        for(JsonNode suggestion:response.path("suggestions")) {
            JsonNode item=suggestion.path("placePrediction");
            String id=item.path("placeId").asText("");
            if(id.isEmpty())continue;
            JsonNode fields=item.path("structuredFormat");
            String name=fields.path("mainText").path("text").asText("");
            String secondary=fields.path("secondaryText").path("text").asText("");
            if(!name.isBlank()) items.add(new Suggestion("google:"+id,name,secondary));
        }
        return new Suggestions("google",items);
    }

    public Resolved resolve(String encodedId,String session) {
        return resolve(encodedId,session,"anonymous");
    }
    public Resolved resolve(String encodedId,String session,String viewer) {
        requireEnabled();
        throttle(viewer);
        String id=encodedId==null?"":encodedId;
        if(!PLACE_ID.matcher(id).matches())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Identificador inválido");
        String uri="https://places.googleapis.com/v1/places/"+id;
        if(session != null && SESSION.matcher(session).matches())
            uri += "?sessionToken="+session;
        // Essentials-only field mask: avoid paying for Pro fields just to save a label.
        JsonNode data=get(uri,"id,formattedAddress,location");
        JsonNode loc=data.path("location");
        double lat=loc.path("latitude").asDouble(Double.NaN);
        double lon=loc.path("longitude").asDouble(Double.NaN);
        if(!Double.isFinite(lat)||!Double.isFinite(lon))
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Localização indisponível");
        return new Resolved("google:"+id,"",
            data.path("formattedAddress").asText(""),lat,lon);
    }

    private void requireEnabled() {
        if(apiKey==null||apiKey.isBlank())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Google Places ainda não configurado");
    }
    private JsonNode post(String url,Object body,String fields) {
        try {
            HttpRequest request=HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(7)).header("X-Goog-Api-Key",apiKey)
                .header("X-Goog-FieldMask",fields).header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))).build();
            return parse(http.send(request,HttpResponse.BodyHandlers.ofString()));
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Serviço geográfico indisponível");
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Serviço geográfico indisponível");
        }
    }
    private JsonNode get(String url,String fields) {
        try {
            HttpRequest request=HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(7)).header("X-Goog-Api-Key",apiKey)
                .header("X-Goog-FieldMask",fields).GET().build();
            return parse(http.send(request,HttpResponse.BodyHandlers.ofString()));
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Serviço geográfico indisponível");
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Serviço geográfico indisponível");
        }
    }
    private JsonNode parse(HttpResponse<String> response) throws Exception {
        if(response.statusCode()<200 || response.statusCode()>=300)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Falha no fornecedor de localizações");
        return json.readTree(response.body());
    }
}
