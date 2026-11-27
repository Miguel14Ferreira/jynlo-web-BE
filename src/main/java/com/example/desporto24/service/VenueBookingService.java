package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionCourtBooking;
import com.example.desporto24.model.venue.*;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionCourtBookingRepository;
import com.example.desporto24.repository.venue.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Jynlo court bookings, first version: the person responsible for a venue claims it
 * (an admin approves), lists courts and opening hours, and confirms or declines the
 * requests players send for free slots. Payment happens at the venue for now.
 */
@Service
@RequiredArgsConstructor
public class VenueBookingService {
    static final ZoneId ZONE = ZoneId.of("Europe/Lisbon");
    static final int MAX_COURTS = 30;
    static final int MAX_RANGES_PER_DAY = 3;
    static final int MAX_PENDING_CLAIMS = 5;
    static final int MAX_PENDING_REQUESTS = 5;
    static final int BOOKING_WINDOW_DAYS = 60;
    static final Duration MIN_NOTICE = Duration.ofMinutes(30);
    static final List<VenueBookingRequest.Status> HOLDING =
            List.of(VenueBookingRequest.Status.PENDING, VenueBookingRequest.Status.CONFIRMED);
    private static final Pattern TIME = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");
    private static final Pattern PHONE = Pattern.compile("^[+0-9 ()-]{6,40}$");

    private final VenueRepository venues;
    private final VenueOwnershipClaimRepository claims;
    private final VenueManagerRepository managers;
    private final VenueCourtRepository courts;
    private final VenueOpeningHoursRepository hours;
    private final VenueBookingRequestRepository bookings;
    private final VenuePaymentSetupRepository paymentSetups;
    private final ProfileRepository profiles;
    private final SessionRepository sessions;
    private final SessionCourtBookingRepository sessionCourtBookings;
    private final NotificationsRepository notifications;
    private final NotificationPreferencesService preferences;
    private final ApplicationEventPublisher events;
    private final com.example.desporto24.service.payment.VenueOnlinePaymentService onlinePayments;

    // ---- Views -------------------------------------------------------------------------

    /** adminSetup: a Jynlo administrator who is not a manager may still set up courts, hours and payment for the venue. */
    public record ManagementStatus(boolean manager, String claimStatus, String decisionNote, boolean acceptsBookings,
                                   boolean adminSetup) {}
    public record ClaimView(Long id, Long venueId, String venueName, String venueCity, String username,
                            String roleDescription, String contactPhone, String status, Instant createdAt,
                            Instant decidedAt, String decidedBy, String decisionNote) {}
    public record CourtView(Long id, String name, String sport, Integer priceCents, int slotMinutes, boolean active) {}
    public record HoursView(int dayOfWeek, String openTime, String closeTime) {}
    /** paymentMethod: MBWAY, IN_PERSON or ONLINE (null = not set, pay at the venue); mbwayPhone only for its managers. */
    public record Offer(Long venueId, boolean acceptsBookings, List<CourtView> courts, List<HoursView> hours,
                        String paymentMethod, String mbwayPhone) {}
    public record Slot(Instant start, Instant end, boolean free) {}
    public record CourtSlots(Long courtId, String name, String sport, Integer priceCents, int slotMinutes, List<Slot> slots) {}
    public record DaySlots(Long venueId, String date, List<CourtSlots> courts) {}
    public record BookingView(Long id, Long venueId, String venueName, Long courtId, String courtName,
                              String requesterUsername, Instant startAt, Instant endAt, String status,
                              Integer priceCents, String playerNote, String ownerNote, Long sessionId,
                              Instant createdAt, Instant decidedAt, String paymentMethod, String mbwayPhone,
                              Instant paidClaimedAt, Instant paidConfirmedAt) {}
    public record VenueAvailability(Long venueId, String name, String city, String district, Double latitude,
                                    Double longitude, String paymentMethod, List<CourtSlots> courts) {}
    public record ManagedVenue(Long venueId, String name, String city, long pendingRequests, boolean acceptsBookings) {}

    public record CourtInput(Long id, String name, String sport, Integer priceCents, Integer slotMinutes, Boolean active) {}
    public record HoursInput(Integer dayOfWeek, String openTime, String closeTime) {}

    // ---- Claims ------------------------------------------------------------------------

    @Transactional
    public ClaimView claim(Long venueId, String username, String roleDescription, String contactPhone) {
        Venue venue = venue(venueId);
        if (managers.existsByVenueIdAndUsernameIgnoreCase(venueId, username)) {
            throw conflict("Já geres este recinto.");
        }
        Optional<VenueOwnershipClaim> pending = claims.findFirstByVenueIdAndUsernameIgnoreCaseAndStatus(
                venueId, username, VenueOwnershipClaim.Status.PENDING);
        if (pending.isPresent()) return claimView(pending.get(), venue);
        if (claims.countByUsernameIgnoreCaseAndStatus(username, VenueOwnershipClaim.Status.PENDING) >= MAX_PENDING_CLAIMS) {
            throw bad("Já tens vários pedidos à espera de aprovação. Aguarda a resposta antes de enviar mais.");
        }
        String role = roleDescription == null ? "" : roleDescription.strip();
        if (role.length() < 10 || role.length() > 300) {
            throw bad("Explica em 10 a 300 caracteres qual é a tua ligação a este recinto.");
        }
        String phone = contactPhone == null || contactPhone.isBlank() ? null : contactPhone.strip();
        if (phone != null && !PHONE.matcher(phone).matches()) throw bad("Indica um telefone válido.");
        VenueOwnershipClaim claim = new VenueOwnershipClaim();
        claim.setVenueId(venueId);
        claim.setUsername(username);
        claim.setRoleDescription(role);
        claim.setContactPhone(phone);
        claim.setStatus(VenueOwnershipClaim.Status.PENDING);
        claim.setCreatedAt(Instant.now());
        return claimView(claims.save(claim), venue);
    }

    @Transactional(readOnly = true)
    public ManagementStatus status(Long venueId, String username) {
        venue(venueId);
        boolean manager = username != null && managers.existsByVenueIdAndUsernameIgnoreCase(venueId, username);
        Optional<VenueOwnershipClaim> last = username == null ? Optional.empty()
                : claims.findFirstByVenueIdAndUsernameIgnoreCaseOrderByCreatedAtDesc(venueId, username);
        return new ManagementStatus(manager, last.map(c -> c.getStatus().name()).orElse(null),
                last.map(VenueOwnershipClaim::getDecisionNote).orElse(null), accepts(venueId),
                !manager && admin(username));
    }

    @Transactional(readOnly = true)
    public List<ClaimView> pendingClaims() {
        return claims.findByStatusOrderByCreatedAtAsc(VenueOwnershipClaim.Status.PENDING).stream()
                .map(c -> claimView(c, venues.findById(c.getVenueId()).orElse(null))).toList();
    }

    @Transactional(readOnly = true)
    public List<ClaimView> decidedClaims() {
        return claims.findTop50ByStatusNotOrderByDecidedAtDesc(VenueOwnershipClaim.Status.PENDING).stream()
                .map(c -> claimView(c, venues.findById(c.getVenueId()).orElse(null))).toList();
    }

    @Transactional
    public ClaimView decideClaim(Long claimId, String admin, boolean approve, String note) {
        VenueOwnershipClaim claim = claims.findById(claimId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        if (claim.getStatus() != VenueOwnershipClaim.Status.PENDING) throw conflict("Este pedido já foi decidido.");
        String cleanNote = note == null || note.isBlank() ? null : note.strip();
        if (cleanNote != null && cleanNote.length() > 500) throw bad("A nota pode ter até 500 caracteres.");
        Venue venue = venue(claim.getVenueId());
        claim.setStatus(approve ? VenueOwnershipClaim.Status.APPROVED : VenueOwnershipClaim.Status.REJECTED);
        claim.setDecidedAt(Instant.now());
        claim.setDecidedBy(admin);
        claim.setDecisionNote(cleanNote);
        claims.save(claim);
        if (approve && !managers.existsByVenueIdAndUsernameIgnoreCase(venue.getId(), claim.getUsername())) {
            VenueManager manager = new VenueManager();
            manager.setVenueId(venue.getId());
            manager.setUsername(claim.getUsername());
            manager.setCreatedAt(Instant.now());
            managers.save(manager);
        }
        notify(claim.getUsername(), venue.getId(),
                approve ? "Pedido de gestão aprovado" : "Pedido de gestão recusado",
                approve ? "Já podes gerir \"" + venue.getName() + "\" na Jynlo: adiciona os campos e o horário para receberes reservas."
                        : "O teu pedido para gerir \"" + venue.getName() + "\" não foi aprovado."
                        + (cleanNote == null ? "" : " Nota: " + cleanNote));
        return claimView(claim, venue);
    }

    /** Approving a venue its creator runs makes them its manager at once, with an approved claim for the record. */
    @Transactional
    public void grantManagerFromSuggestion(Long venueId, String username, String role, String phone, String admin) {
        Venue venue = venue(venueId);
        VenueOwnershipClaim claim = new VenueOwnershipClaim();
        claim.setVenueId(venueId);
        claim.setUsername(username);
        claim.setRoleDescription(role);
        claim.setContactPhone(phone);
        claim.setStatus(VenueOwnershipClaim.Status.APPROVED);
        claim.setCreatedAt(Instant.now());
        claim.setDecidedAt(Instant.now());
        claim.setDecidedBy(admin);
        claims.save(claim);
        if (!managers.existsByVenueIdAndUsernameIgnoreCase(venueId, username)) {
            VenueManager manager = new VenueManager();
            manager.setVenueId(venueId);
            manager.setUsername(username);
            manager.setCreatedAt(Instant.now());
            managers.save(manager);
        }
        notify(username, venueId, "O teu recinto está na Jynlo",
                "\"" + venue.getName() + "\" foi aprovado e já o podes gerir: adiciona os campos e o horário para receberes reservas.");
    }

    // ---- Courts and hours (managers) ---------------------------------------------------

    @Transactional(readOnly = true)
    public Offer offer(Long venueId) {
        venue(venueId);
        boolean accepts = accepts(venueId);
        List<CourtView> courtViews = accepts
                ? courts.findByVenueIdAndActiveTrueOrderByIdAsc(venueId).stream().map(VenueBookingService::courtView).toList()
                : List.of();
        List<HoursView> hourViews = accepts ? hoursViews(venueId) : List.of();
        String method = paymentSetups.findById(venueId).map(VenuePaymentSetup::getMethod).orElse(null);
        return new Offer(venueId, accepts, courtViews, hourViews, accepts ? method : null, null);
    }

    @Transactional(readOnly = true)
    public Offer managerOffer(Long venueId, String username) {
        requireSetupAccess(venueId, username);
        Optional<VenuePaymentSetup> setup = paymentSetups.findById(venueId);
        return new Offer(venueId, accepts(venueId),
                courts.findByVenueIdOrderByIdAsc(venueId).stream().map(VenueBookingService::courtView).toList(),
                hoursViews(venueId), setup.map(VenuePaymentSetup::getMethod).orElse(null),
                setup.map(VenuePaymentSetup::getMbwayPhone).orElse(null));
    }

    @Transactional
    public Offer saveCourts(Long venueId, String username, List<CourtInput> input) {
        requireSetupAccess(venueId, username);
        List<CourtInput> list = input == null ? List.of() : input;
        if (list.size() > MAX_COURTS) throw bad("Podes ter até " + MAX_COURTS + " campos.");
        Map<Long, VenueCourt> existing = new HashMap<>();
        for (VenueCourt c : courts.findByVenueIdOrderByIdAsc(venueId)) existing.put(c.getId(), c);
        Set<Long> kept = new HashSet<>();
        for (CourtInput in : list) {
            if (in == null) continue;
            String name = in.name() == null ? "" : in.name().strip();
            if (name.isEmpty() || name.length() > 80) throw bad("Cada campo precisa de um nome (até 80 caracteres).");
            String sport = in.sport() == null || in.sport().isBlank() ? null : in.sport().strip();
            if (sport != null && sport.length() > 80) throw bad("A modalidade pode ter até 80 caracteres.");
            Integer price = in.priceCents();
            if (price != null && (price < 0 || price > 100_000)) throw bad("Indica um preço entre 0 e 1000 €.");
            int minutes = in.slotMinutes() == null ? 60 : in.slotMinutes();
            if (minutes != 30 && minutes != 60 && minutes != 90 && minutes != 120) {
                throw bad("A duração de cada reserva tem de ser 30, 60, 90 ou 120 minutos.");
            }
            VenueCourt court;
            if (in.id() != null) {
                court = existing.get(in.id());
                if (court == null) throw bad("Campo desconhecido.");
            } else {
                court = new VenueCourt();
                court.setVenueId(venueId);
                court.setCreatedAt(Instant.now());
            }
            court.setName(name);
            court.setSport(sport);
            court.setPriceCents(price);
            court.setSlotMinutes(minutes);
            court.setActive(in.active() == null || in.active());
            VenueCourt saved = courts.save(court);
            kept.add(saved.getId());
        }
        // Courts left out are switched off, never deleted: existing bookings keep their court.
        for (VenueCourt c : existing.values()) {
            if (!kept.contains(c.getId()) && c.isActive()) {
                c.setActive(false);
                courts.save(c);
            }
        }
        return managerOffer(venueId, username);
    }

    @Transactional
    public Offer saveHours(Long venueId, String username, List<HoursInput> input) {
        requireSetupAccess(venueId, username);
        List<HoursInput> list = input == null ? List.of() : input;
        Map<Integer, List<LocalTime[]>> byDay = new HashMap<>();
        List<VenueOpeningHours> rows = new ArrayList<>();
        for (HoursInput in : list) {
            if (in == null) continue;
            int day = in.dayOfWeek() == null ? 0 : in.dayOfWeek();
            if (day < 1 || day > 7) throw bad("Dia da semana inválido.");
            LocalTime open = time(in.openTime());
            LocalTime close = time(in.closeTime());
            if (!open.isBefore(close)) throw bad("A hora de abertura tem de ser antes da hora de fecho.");
            List<LocalTime[]> ranges = byDay.computeIfAbsent(day, d -> new ArrayList<>());
            for (LocalTime[] r : ranges) {
                if (open.isBefore(r[1]) && close.isAfter(r[0])) throw bad("Há horários sobrepostos no mesmo dia.");
            }
            if (ranges.size() >= MAX_RANGES_PER_DAY) throw bad("Podes ter até " + MAX_RANGES_PER_DAY + " períodos por dia.");
            ranges.add(new LocalTime[]{open, close});
            VenueOpeningHours row = new VenueOpeningHours();
            row.setVenueId(venueId);
            row.setDayOfWeek((short) day);
            row.setOpenTime(in.openTime().strip());
            row.setCloseTime(in.closeTime().strip());
            rows.add(row);
        }
        hours.deleteByVenueId(venueId);
        hours.flush();
        hours.saveAll(rows);
        return managerOffer(venueId, username);
    }

    /** Manager: how players pay bookings. MBWAY needs a Portuguese mobile number; null clears it (pay at the venue). */
    @Transactional
    public Offer savePayment(Long venueId, String username, String method, String phone) {
        requireSetupAccess(venueId, username);
        if (method == null || method.isBlank()) {
            if (paymentSetups.existsById(venueId)) paymentSetups.deleteById(venueId);
            return managerOffer(venueId, username);
        }
        if (!SessionPaymentService.MBWAY.equals(method) && !SessionPaymentService.IN_PERSON.equals(method)
                && !SessionPaymentService.ONLINE.equals(method)) {
            throw bad("Forma de pagamento inválida");
        }
        if (SessionPaymentService.ONLINE.equals(method) && !onlinePayments.venueCanReceive(venueId)) {
            throw conflict("Ativa primeiro os pagamentos automáticos com a conta Stripe do recinto.");
        }
        VenuePaymentSetup setup = paymentSetups.findById(venueId).orElseGet(() -> {
            VenuePaymentSetup fresh = new VenuePaymentSetup();
            fresh.setVenueId(venueId);
            return fresh;
        });
        setup.setMethod(method);
        setup.setMbwayPhone(SessionPaymentService.MBWAY.equals(method)
                ? SessionPaymentService.normalisePhone(phone == null ? "" : phone) : null);
        setup.setUpdatedAt(Instant.now());
        paymentSetups.save(setup);
        return managerOffer(venueId, username);
    }

    /** Player: "Já paguei" on a confirmed booking (or undo it). */
    @Transactional
    public BookingView markPaid(Long bookingId, String username, boolean paid) {
        VenueBookingRequest b = booking(bookingId);
        if (!b.getRequesterUsername().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só quem fez a reserva a pode marcar como paga.");
        }
        if (b.getStatus() != VenueBookingRequest.Status.CONFIRMED) throw conflict("Só reservas confirmadas se pagam.");
        if (b.getPaidConfirmedAt() != null && !paid) throw conflict("O recinto já confirmou o pagamento.");
        b.setPaidClaimedAt(paid ? Instant.now() : null);
        bookings.save(b);
        if (paid) {
            String court = courts.findById(b.getCourtId()).map(VenueCourt::getName).orElse("campo");
            for (VenueManager m : managers.findByVenueId(b.getVenueId())) {
                notify(m.getUsername(), b.getVenueId(), "Reserva paga",
                        username + " diz que já pagou o " + court + " de " + when(b.getStartAt()) + ". Confirma quando receberes.");
            }
        }
        return bookingView(b);
    }

    /** Manager: confirm the payment arrived (or undo it). */
    @Transactional
    public BookingView confirmPaid(Long bookingId, String username, boolean paid) {
        VenueBookingRequest b = booking(bookingId);
        requireManager(b.getVenueId(), username);
        if (b.getStatus() != VenueBookingRequest.Status.CONFIRMED) throw conflict("Só reservas confirmadas se pagam.");
        b.setPaidConfirmedAt(paid ? Instant.now() : null);
        bookings.save(b);
        return bookingView(b);
    }

    @Transactional(readOnly = true)
    public List<ManagedVenue> managedVenues(String username) {
        List<ManagedVenue> result = new ArrayList<>();
        Instant now = Instant.now();
        for (VenueManager m : managers.findByUsernameIgnoreCase(username)) {
            Venue v = venues.findById(m.getVenueId()).orElse(null);
            if (v == null) continue;
            long pending = bookings.findTop200ByVenueIdAndStartAtAfterOrderByStartAtAsc(v.getId(), now).stream()
                    .filter(b -> b.getStatus() == VenueBookingRequest.Status.PENDING).count();
            result.add(new ManagedVenue(v.getId(), v.getName(), v.getCity(), pending, accepts(v.getId())));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Long> venuesAcceptingBookings() {
        return courts.venuesAcceptingBookings();
    }

    // ---- Slots and requests ------------------------------------------------------------

    @Transactional(readOnly = true)
    public DaySlots slots(Long venueId, String date) {
        venue(venueId);
        LocalDate day = date(date);
        List<CourtSlots> result = new ArrayList<>();
        if (accepts(venueId)) {
            List<VenueCourt> active = courts.findByVenueIdAndActiveTrueOrderByIdAsc(venueId);
            Instant dayStart = day.atStartOfDay(ZONE).toInstant();
            Instant dayEnd = day.plusDays(1).atStartOfDay(ZONE).toInstant();
            List<VenueBookingRequest> held = bookings.holdingInVenue(venueId, dayStart, dayEnd, HOLDING);
            for (VenueCourt court : active) {
                List<Slot> slots = new ArrayList<>();
                for (Instant[] s : candidateSlots(venueId, court, day)) {
                    boolean taken = held.stream().anyMatch(b -> b.getCourtId().equals(court.getId())
                            && b.getStartAt().isBefore(s[1]) && b.getEndAt().isAfter(s[0]));
                    slots.add(new Slot(s[0], s[1], !taken));
                }
                result.add(new CourtSlots(court.getId(), court.getName(), court.getSport(), court.getPriceCents(),
                        court.getSlotMinutes(), slots));
            }
        }
        return new DaySlots(venueId, day.toString(), result);
    }

    /**
     * Free slots across every venue that takes Jynlo bookings, for one day, optionally narrowed to a
     * sport, a city/district and a time window. Only free slots are returned, earliest venues first.
     */
    @Transactional(readOnly = true)
    public List<VenueAvailability> search(String date, String sport, String place, String from, String to) {
        LocalDate day = date(date);
        String sportKey = fold(sport);
        String placeKey = fold(place);
        LocalTime fromTime = from == null || from.isBlank() ? null : time(from);
        LocalTime toTime = to == null || to.isBlank() ? null : time(to);
        List<VenueAvailability> result = new ArrayList<>();
        for (Long venueId : courts.venuesAcceptingBookings()) {
            Venue venue = venues.findById(venueId).orElse(null);
            if (venue == null || !accepts(venueId)) continue;
            if (!placeKey.isEmpty() && !fold(venue.getCity()).contains(placeKey)
                    && !fold(venue.getDistrict()).contains(placeKey) && !fold(venue.getName()).contains(placeKey)) continue;
            List<CourtSlots> open = new ArrayList<>();
            for (CourtSlots court : slots(venueId, day.toString()).courts()) {
                if (!sportKey.isEmpty()) {
                    String courtSport = court.sport() == null ? venue.getSports() : court.sport();
                    if (!fold(courtSport).contains(sportKey)) continue;
                }
                List<Slot> free = court.slots().stream().filter(Slot::free).filter(s -> {
                    LocalTime t = s.start().atZone(ZONE).toLocalTime();
                    return (fromTime == null || !t.isBefore(fromTime)) && (toTime == null || t.isBefore(toTime));
                }).toList();
                if (!free.isEmpty()) {
                    open.add(new CourtSlots(court.courtId(), court.name(), court.sport(), court.priceCents(),
                            court.slotMinutes(), free));
                }
            }
            if (open.isEmpty()) continue;
            String method = paymentSetups.findById(venueId).map(VenuePaymentSetup::getMethod).orElse(null);
            result.add(new VenueAvailability(venueId, venue.getName(), venue.getCity(), venue.getDistrict(),
                    venue.getLatitude(), venue.getLongitude(), method, open));
        }
        result.sort(Comparator.comparing((VenueAvailability v) -> v.courts().stream()
                .map(c -> c.slots().get(0).start()).min(Comparator.naturalOrder()).orElse(Instant.MAX))
                .thenComparing(VenueAvailability::name, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return result;
    }

    /** Links one of my bookings to a game I organise, so the game shows the court as booked once confirmed. */
    @Transactional
    public BookingView linkSession(Long bookingId, String username, Long sessionId) {
        VenueBookingRequest b = booking(bookingId);
        if (username == null || !b.getRequesterUsername().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta reserva não é tua.");
        }
        if (b.getStatus() != VenueBookingRequest.Status.PENDING && b.getStatus() != VenueBookingRequest.Status.CONFIRMED) {
            throw conflict("Esta reserva já não está ativa.");
        }
        if (sessionId == null) throw bad("Escolhe o jogo.");
        b.setSessionId(organisedSession(sessionId, username));
        bookings.save(b);
        if (b.getStatus() == VenueBookingRequest.Status.CONFIRMED) markSessionCourt(sessionId, true);
        return bookingView(b);
    }

    static String fold(String value) {
        if (value == null) return "";
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).strip();
    }

    @Transactional
    public BookingView request(Long venueId, String username, Long courtId, String startAt, Long sessionId, String note) {
        Venue venue = venue(venueId);
        if (!accepts(venueId)) throw conflict("Este recinto ainda não aceita reservas na Jynlo.");
        if (courtId == null) throw bad("Escolhe um campo.");
        VenueCourt court = courts.lockById(courtId)
                .orElseThrow(() -> bad("Campo desconhecido."));
        if (!court.getVenueId().equals(venueId) || !court.isActive()) throw bad("Este campo não está disponível.");
        final Instant start = parseInstant(startAt);
        LocalDate day = start.atZone(ZONE).toLocalDate();
        Instant[] slot = candidateSlots(venueId, court, day).stream()
                .filter(s -> s[0].equals(start)).findFirst()
                .orElseThrow(() -> bad("Esse horário não está disponível neste campo."));
        if (!bookings.overlapping(courtId, slot[0], slot[1], HOLDING).isEmpty()) {
            throw conflict("Esse horário acabou de ser pedido por outra pessoa. Escolhe outro.");
        }
        if (bookings.countByRequesterUsernameIgnoreCaseAndStatusAndStartAtAfter(
                username, VenueBookingRequest.Status.PENDING, Instant.now()) >= MAX_PENDING_REQUESTS) {
            throw bad("Já tens vários pedidos à espera de resposta. Aguarda antes de pedir mais.");
        }
        String cleanNote = note == null || note.isBlank() ? null : note.strip();
        if (cleanNote != null && cleanNote.length() > 300) throw bad("A mensagem pode ter até 300 caracteres.");
        Long linkedSession = sessionId == null ? null : organisedSession(sessionId, username);
        VenueBookingRequest request = new VenueBookingRequest();
        request.setVenueId(venueId);
        request.setCourtId(courtId);
        request.setRequesterUsername(username);
        request.setStartAt(slot[0]);
        request.setEndAt(slot[1]);
        request.setStatus(VenueBookingRequest.Status.PENDING);
        request.setPriceCents(court.getPriceCents());
        request.setPlayerNote(cleanNote);
        request.setSessionId(linkedSession);
        request.setCreatedAt(Instant.now());
        VenueBookingRequest saved = bookings.save(request);
        for (VenueManager m : managers.findByVenueId(venueId)) {
            notify(m.getUsername(), venueId, "Novo pedido de reserva",
                    username + " pediu o " + court.getName() + " em " + when(slot[0]) + ".");
        }
        return bookingView(saved, venue, court);
    }

    @Transactional(readOnly = true)
    public List<BookingView> mine(String username) {
        return bookings.findTop100ByRequesterUsernameIgnoreCaseOrderByStartAtDesc(username).stream()
                .map(this::bookingView).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingView> venueRequests(Long venueId, String username) {
        requireSetupAccess(venueId, username);
        return bookings.findTop200ByVenueIdAndStartAtAfterOrderByStartAtAsc(venueId, Instant.now().minus(Duration.ofDays(1)))
                .stream().map(this::bookingView).toList();
    }

    @Transactional
    public BookingView cancel(Long bookingId, String username) {
        VenueBookingRequest b = booking(bookingId);
        if (!b.getRequesterUsername().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só quem fez o pedido o pode cancelar.");
        }
        if (!HOLDING.contains(b.getStatus())) throw conflict("Este pedido já não está ativo.");
        if (!b.getStartAt().isAfter(Instant.now())) throw conflict("Esta reserva já começou.");
        boolean wasConfirmed = b.getStatus() == VenueBookingRequest.Status.CONFIRMED;
        b.setStatus(VenueBookingRequest.Status.CANCELLED);
        b.setDecidedAt(Instant.now());
        bookings.save(b);
        if (wasConfirmed) markSessionCourt(b.getSessionId(), false);
        String court = courts.findById(b.getCourtId()).map(VenueCourt::getName).orElse("campo");
        for (VenueManager m : managers.findByVenueId(b.getVenueId())) {
            notify(m.getUsername(), b.getVenueId(), "Reserva cancelada",
                    username + " cancelou o " + court + " em " + when(b.getStartAt()) + ".");
        }
        return bookingView(b);
    }

    @Transactional
    public BookingView decide(Long bookingId, String username, boolean confirm, String note) {
        VenueBookingRequest b = booking(bookingId);
        requireManager(b.getVenueId(), username);
        if (b.getStatus() != VenueBookingRequest.Status.PENDING) throw conflict("Este pedido já foi respondido.");
        String cleanNote = note == null || note.isBlank() ? null : note.strip();
        if (cleanNote != null && cleanNote.length() > 300) throw bad("A mensagem pode ter até 300 caracteres.");
        if (confirm) {
            courts.lockById(b.getCourtId());
            boolean clash = bookings.overlapping(b.getCourtId(), b.getStartAt(), b.getEndAt(),
                    List.of(VenueBookingRequest.Status.CONFIRMED)).stream().anyMatch(o -> !o.getId().equals(b.getId()));
            if (clash) throw conflict("Já confirmaste outra reserva para esse horário neste campo.");
        }
        b.setStatus(confirm ? VenueBookingRequest.Status.CONFIRMED : VenueBookingRequest.Status.DECLINED);
        b.setOwnerNote(cleanNote);
        b.setDecidedAt(Instant.now());
        bookings.save(b);
        if (confirm) markSessionCourt(b.getSessionId(), true);
        String venueName = venues.findById(b.getVenueId()).map(Venue::getName).orElse("o recinto");
        notify(b.getRequesterUsername(), b.getVenueId(),
                confirm ? "Reserva confirmada" : "Reserva recusada",
                (confirm ? venueName + " confirmou a tua reserva para " : venueName + " não pode aceitar a tua reserva para ")
                        + when(b.getStartAt()) + "." + (confirm ? paymentHint(b) : "")
                        + (cleanNote == null ? "" : " Nota: " + cleanNote));
        return bookingView(b);
    }

    // ---- Helpers -----------------------------------------------------------------------

    boolean accepts(Long venueId) {
        return !managers.findByVenueId(venueId).isEmpty()
                && !courts.findByVenueIdAndActiveTrueOrderByIdAsc(venueId).isEmpty()
                && !hours.findByVenueIdOrderByDayOfWeekAscOpenTimeAsc(venueId).isEmpty();
    }

    /** Every slot of this court on that day, from the opening hours, still bookable (in the future). */
    List<Instant[]> candidateSlots(Long venueId, VenueCourt court, LocalDate day) {
        List<Instant[]> slots = new ArrayList<>();
        int weekday = day.getDayOfWeek().getValue();
        Instant earliest = Instant.now().plus(MIN_NOTICE);
        Duration length = Duration.ofMinutes(court.getSlotMinutes());
        for (VenueOpeningHours h : hours.findByVenueIdOrderByDayOfWeekAscOpenTimeAsc(venueId)) {
            if (h.getDayOfWeek() != weekday) continue;
            LocalTime open = LocalTime.parse(h.getOpenTime());
            LocalTime close = LocalTime.parse(h.getCloseTime());
            LocalDateTime cursor = day.atTime(open);
            LocalDateTime end = day.atTime(close);
            while (!cursor.plus(length).isAfter(end)) {
                Instant s = cursor.atZone(ZONE).toInstant();
                if (!s.isBefore(earliest)) slots.add(new Instant[]{s, s.plus(length)});
                cursor = cursor.plus(length);
            }
        }
        return slots;
    }

    private Long organisedSession(Long sessionId, String username) {
        Session session = sessions.findById(sessionId).orElseThrow(() -> bad("Jogo desconhecido."));
        Profile me = profiles.findUserByUsername(username).orElseThrow(() -> bad("Conta não encontrada."));
        boolean organiser = session.getProfile() != null && session.getProfile().getId() != null
                && session.getProfile().getId().equals(me.getId());
        if (!organiser) throw bad("Só o organizador pode ligar uma reserva ao jogo.");
        return sessionId;
    }

    private void markSessionCourt(Long sessionId, boolean booked) {
        if (sessionId == null) return;
        SessionCourtBooking row = sessionCourtBookings.findById(sessionId).orElseGet(() -> {
            SessionCourtBooking fresh = new SessionCourtBooking();
            fresh.setSessionId(sessionId);
            return fresh;
        });
        row.setBooked(booked);
        row.setBookedAt(booked ? Instant.now() : null);
        row.setUpdatedAt(Instant.now());
        sessionCourtBookings.save(row);
    }

    private void notify(String username, Long venueId, String subject, String message) {
        if (username == null || username.isBlank()) return;
        Notifications n = new Notifications();
        n.setUsername(username);
        n.setSubject(subject);
        n.setMessage(message);
        n.setGreeting("");
        n.setSignature("");
        n.setCreatedAt(Instant.now().toString());
        n.setNormalMessage(true);
        n.setType(NotificationType.VENUE_BOOKING);
        n.setVenueId(venueId);
        n.setRead(false);
        if (!preferences.allowsInApp(username, n)) return;
        Notifications saved = notifications.save(n);
        if (saved != null && saved.getId() != null) {
            events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), NotificationType.VENUE_BOOKING.name(), null, null, null, true));
        }
    }

    /**
     * Courts, opening hours and payment can also be filled in by a Jynlo administrator, so the team can set a venue
     * up on its behalf. Bookings still only open once the venue has an approved manager (see {@link #accepts}),
     * and only managers confirm or decline requests.
     */
    public void requireSetupAccess(Long venueId, String username) {
        venue(venueId);
        if (username != null && (managers.existsByVenueIdAndUsernameIgnoreCase(venueId, username) || admin(username))) return;
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só quem gere o recinto pode fazer isto.");
    }

    /** Checked in the database on each call, like AdminCurrentRoleGuard. */
    private boolean admin(String username) {
        if (username == null || username.isBlank()) return false;
        return profiles.findUserByUsernameIgnoreCase(username)
                .filter(p -> Boolean.TRUE.equals(p.getEnabled()) && Boolean.TRUE.equals(p.getNotLocked())
                        && p.getModerationSuspendedAt() == null
                        && ("ROLE_ADMIN".equals(p.getRole()) || "ROLE_SUPER_ADMIN".equals(p.getRole())))
                .isPresent();
    }

    public void requireManager(Long venueId, String username) {
        venue(venueId);
        if (username == null || !managers.existsByVenueIdAndUsernameIgnoreCase(venueId, username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só quem gere o recinto pode fazer isto.");
        }
    }

    private Venue venue(Long venueId) {
        if (venueId == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recinto não encontrado");
        return venues.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recinto não encontrado"));
    }

    private VenueBookingRequest booking(Long id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada");
        return bookings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));
    }

    private List<HoursView> hoursViews(Long venueId) {
        return hours.findByVenueIdOrderByDayOfWeekAscOpenTimeAsc(venueId).stream()
                .map(h -> new HoursView(h.getDayOfWeek(), h.getOpenTime(), h.getCloseTime())).toList();
    }

    private static CourtView courtView(VenueCourt c) {
        return new CourtView(c.getId(), c.getName(), c.getSport(), c.getPriceCents(), c.getSlotMinutes(), c.isActive());
    }

    private static ClaimView claimView(VenueOwnershipClaim c, Venue venue) {
        return new ClaimView(c.getId(), c.getVenueId(), venue == null ? null : venue.getName(),
                venue == null ? null : venue.getCity(), c.getUsername(), c.getRoleDescription(), c.getContactPhone(),
                c.getStatus().name(), c.getCreatedAt(), c.getDecidedAt(), c.getDecidedBy(), c.getDecisionNote());
    }

    private BookingView bookingView(VenueBookingRequest b) {
        return bookingView(b, venues.findById(b.getVenueId()).orElse(null), courts.findById(b.getCourtId()).orElse(null));
    }

    private BookingView bookingView(VenueBookingRequest b, Venue venue, VenueCourt court) {
        Optional<VenuePaymentSetup> setup = paymentSetups.findById(b.getVenueId());
        // The MB WAY number is shown once the venue confirmed the booking: only then is there something to pay.
        boolean payable = b.getStatus() == VenueBookingRequest.Status.CONFIRMED;
        return new BookingView(b.getId(), b.getVenueId(), venue == null ? null : venue.getName(), b.getCourtId(),
                court == null ? null : court.getName(), b.getRequesterUsername(), b.getStartAt(), b.getEndAt(),
                b.getStatus().name(), b.getPriceCents(), b.getPlayerNote(), b.getOwnerNote(), b.getSessionId(),
                b.getCreatedAt(), b.getDecidedAt(), setup.map(VenuePaymentSetup::getMethod).orElse(null),
                payable ? setup.map(VenuePaymentSetup::getMbwayPhone).orElse(null) : null,
                b.getPaidClaimedAt(), b.getPaidConfirmedAt());
    }

    private String paymentHint(VenueBookingRequest b) {
        Optional<VenuePaymentSetup> setup = paymentSetups.findById(b.getVenueId());
        if (setup.isPresent() && SessionPaymentService.MBWAY.equals(setup.get().getMethod()) && setup.get().getMbwayPhone() != null) {
            String amount = b.getPriceCents() == null ? "" : " (" + euros(b.getPriceCents()) + ")";
            return " Paga por MB WAY para " + setup.get().getMbwayPhone() + amount + ".";
        }
        return " Pagas no recinto.";
    }

    static String euros(int cents) {
        return String.format(Locale.ROOT, "%d,%02d €", cents / 100, cents % 100);
    }

    static String when(Instant instant) {
        ZonedDateTime t = instant.atZone(ZONE);
        return String.format(Locale.ROOT, "%02d/%02d às %02d:%02d",
                t.getDayOfMonth(), t.getMonthValue(), t.getHour(), t.getMinute());
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) throw bad("Escolhe um horário.");
        try {
            return Instant.parse(value.strip());
        } catch (DateTimeParseException invalid) {
            throw bad("Escolhe um horário.");
        }
    }

    private static LocalTime time(String value) {
        if (value == null || !TIME.matcher(value.strip()).matches()) throw bad("Indica as horas no formato HH:mm.");
        return LocalTime.parse(value.strip());
    }

    private static LocalDate date(String value) {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate day;
        try {
            day = value == null || value.isBlank() ? today : LocalDate.parse(value.strip());
        } catch (DateTimeParseException invalid) {
            throw bad("Data inválida.");
        }
        if (day.isBefore(today) || day.isAfter(today.plusDays(BOOKING_WINDOW_DAYS))) {
            throw bad("Escolhe um dia entre hoje e os próximos " + BOOKING_WINDOW_DAYS + " dias.");
        }
        return day;
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
