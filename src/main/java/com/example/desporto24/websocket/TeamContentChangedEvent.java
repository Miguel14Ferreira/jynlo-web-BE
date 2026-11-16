package com.example.desporto24.websocket;

/** Content-free notice: clients re-fetch authorized team history over HTTP. */
public record TeamContentChangedEvent(Long teamId, String kind) {}
