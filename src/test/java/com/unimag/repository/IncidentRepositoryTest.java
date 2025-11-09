package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.*;
import com.unimag.entities.Enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Test
    void findByEntityTypeAndEntityId() {
    }

    @Test
    @DisplayName("Buscar incident por tipo")
    void findByIncidentType() {

        var reporter = createAndSaveUser("type@example.com", "3003333333");

        incidentRepository.saveAll(List.of(
                createIncident(EntityType.TRIP, 1L, TypeIncident.VEHICLE, reporter,"Falla en el motor detectada 20km después de la salida; se requirió cambio de unidad."),
                createIncident(EntityType.TRIP, 2L, TypeIncident.VEHICLE, reporter,"Problema de sobrecalentamiento en las llantas traseras. Parada de emergencia en el km 50."),
                createIncident(EntityType.PARCEL, 3L, TypeIncident.DELIVERY_FAIL, reporter,"El destinatario no estaba en casa y el teléfono de contacto no existe. Paquete devuelto a bodega.")
        ));

        List<Incident> vehicleIncidents = incidentRepository.findByIncidentType(TypeIncident.VEHICLE);

        assertThat(vehicleIncidents).hasSize(2);
        assertThat(vehicleIncidents).allMatch(i -> i.getTypeIncident() == TypeIncident.VEHICLE);
    }

    @Test
    @DisplayName("Buscar incident reportado por un id de user")
    void findByReportedById() {

        var reporter1 = createAndSaveUser("r1@example.com", "3004444444");
        var reporter2 = createAndSaveUser("r2@example.com", "3005555555");

        incidentRepository.saveAll(List.of(
                createIncident(EntityType.TRIP, 1L, TypeIncident.SECURITY, reporter1, "Problema de seguridad: pasajero ebrio causando disturbios."),
                createIncident(EntityType.TRIP, 2L, TypeIncident.OVERBOOK, reporter1, "Incidente de sobreventa: 3 tickets adicionales vendidos, no hay asientos disponibles."),
                createIncident(EntityType.PARCEL, 3L, TypeIncident.DELIVERY_FAIL, reporter2, "Falla en la entrega: dirección incorrecta proporcionada por el remitente.")
        ));

        List<Incident> reporter1Incidents = incidentRepository.findByReportedById(reporter1.getId());

        assertThat(reporter1Incidents).hasSize(2);
        assertThat(reporter1Incidents).allMatch(i -> i.getReportedBy().getId().equals(reporter1.getId()));
    }

    @Test
    @DisplayName("Buscar incident y ordenar por fecha ")
    void findByEntityOrderByCreatedAtDesc() throws InterruptedException {

        var reporter = createAndSaveUser("order@example.com", "3006666666");

        var oldest = createIncident(EntityType.TRIP, 100L,
                TypeIncident.VEHICLE, reporter, "Falla menor en la luz trasera.");
        Thread.sleep(10);

        var middle = createIncident(EntityType.TRIP, 100L,
                TypeIncident.SECURITY, reporter, "Pasajero fumando en el baño.");
        Thread.sleep(10);

        var newest = createIncident(EntityType.TRIP, 100L,
                TypeIncident.OVERBOOK, reporter, "Dos pasajeros sin asiento por error de sistema.");

        incidentRepository.saveAll(List.of(oldest, middle, newest));

        List<Incident> incidents = incidentRepository.findByEntityOrderByCreatedAtDesc(
                EntityType.TRIP, 100L);

        assertThat(incidents).hasSize(3);
        assertThat(incidents.get(0).getTypeIncident()).isEqualTo(TypeIncident.OVERBOOK);
        assertThat(incidents.get(2).getTypeIncident()).isEqualTo(TypeIncident.VEHICLE);
    }

    @Test
    @DisplayName("Buscar incident en un rango fecha")
    void findByDateRange() {

        var reporter = createAndSaveUser("range@example.com", "3007777777");

        var now = LocalDateTime.now();
        var hourAgo = now.minusHours(1);
        var twoDaysAgo = now.minusDays(2);

        var recent = createIncident(EntityType.TRIP, 1L,
                TypeIncident.VEHICLE, reporter, "Problema reciente en el aire acondicionado del bus.");
        incidentRepository.save(recent);

        var start = now.minusHours(2);
        var end = now.plusHours(1);

        List<Incident> incidents = incidentRepository.findByDateRange(start, end);

        assertThat(incidents).hasSize(1);
    }

    @Test
    @DisplayName("Buscar incidente por tipo despues de una fecha")
    void countByTypeAndCreatedAtAfter() {

        var reporter = createAndSaveUser("count@example.com", "3008888888");
        var oneDayAgo = LocalDateTime.now().minusDays(1);

        incidentRepository.saveAll(List.of(
                createIncident(EntityType.TRIP, 1L, TypeIncident.SECURITY, reporter, "Problema de seguridad: conductor y pasajero discutiendo acaloradamente."),
                createIncident(EntityType.TRIP, 2L, TypeIncident.SECURITY, reporter, "Pasajero se negó a mostrar su boleto y tuvo que ser escoltado fuera del autobús."),
                createIncident(EntityType.PARCEL, 3L, TypeIncident.DELIVERY_FAIL, reporter, "Falla en la entrega: nadie respondió en la dirección de destino. Intento fallido.")
        ));

        long securityCount = incidentRepository.countByTypeAndCreatedAtAfter(
                TypeIncident.SECURITY, oneDayAgo);
        long deliveryFailCount = incidentRepository.countByTypeAndCreatedAtAfter(
                TypeIncident.DELIVERY_FAIL, oneDayAgo);

        assertThat(securityCount).isEqualTo(2);
        assertThat(deliveryFailCount).isEqualTo(1);
    }


    @Test
    @DisplayName("Buscar incidents asociandos a una entity")
    void shouldFindIncidentsByEntityTypeAndEntityId() {
        var reporter = createAndSaveUser("find@example.com", "3002222222");
        var trip = createAndSaveTrip();

        var incident1 = createIncident(EntityType.TRIP, trip.getId(),
                TypeIncident.VEHICLE, reporter,"Problema con frenos detectado al inicio de la ruta, requiere inspección inmediata.");
        var incident2 = createIncident(EntityType.TRIP, trip.getId(),
                TypeIncident.SECURITY, reporter,"Problema con el manejo de efectivo del conductor, parece haber un faltante.");
        var incident3 = createIncident(EntityType.TICKET, 999L,
                TypeIncident.PASSENGER_COMPLAINT, reporter,"Problemas con el asiento asignado (roto) y el aire acondicionado apagado.");

        incidentRepository.saveAll(List.of(incident1, incident2, incident3));

        List<Incident> tripIncidents = incidentRepository.findByEntityTypeAndEntityId(
                EntityType.TRIP, trip.getId());

        assertThat(tripIncidents).hasSize(2);
        assertThat(tripIncidents).allMatch(i -> i.getEntityType() == EntityType.TRIP);
        assertThat(tripIncidents).allMatch(i -> i.getEntityId().equals(trip.getId()));
    }

    @Test
    @DisplayName("Guarda un Incidente")
    void shouldSaveIncident() {
        var reporter = createAndSaveUser("reporter@example.com", "3001111111");

        var incident = createIncident(EntityType.TRIP,123L,
                TypeIncident.VEHICLE,reporter, "Problema con los neumáticos del autobús"
        );
        var saved = incidentRepository.save(incident);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEntityType()).isEqualTo(EntityType.TRIP);
        assertThat(saved.getTypeIncident()).isEqualTo(TypeIncident.VEHICLE);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Guarda varios incident")
    void shouldHandleDifferentIncidentTypes() {
        var reporter = createAndSaveUser("types@example.com", "3009999999");

        incidentRepository.saveAll(List.of(
                createIncident(EntityType.TRIP, 1L, TypeIncident.SECURITY, reporter, "Pasajero ebrio se niega a usar el cinturón de seguridad."),
                createIncident(EntityType.TICKET, 2L, TypeIncident.PASSENGER_COMPLAINT, reporter, "Queja: Asiento sucio y en malas condiciones."),
                createIncident(EntityType.PARCEL, 3L, TypeIncident.DELIVERY_FAIL, reporter, "Falla en la entrega: Dirección incompleta, no se pudo localizar el destino."),
                createIncident(EntityType.TRIP, 4L, TypeIncident.OVERBOOK, reporter, "Problema de sobreventa: tres pasajeros sin asiento confirmado al abordar."),
                createIncident(EntityType.TRIP, 5L, TypeIncident.VEHICLE, reporter, "Incidente vehicular: Fuga de aceite detectada durante la inspección previa al viaje."),
                createIncident(EntityType.TICKET, 6L, TypeIncident.OTHER, reporter, "Incidente 'Otro': El pasajero perdió su documento de identidad y requiere asistencia.")
        ));

        List<Incident> all = incidentRepository.findAll();

        assertThat(all).hasSize(6);
        assertThat(all).extracting(Incident::getEntityType)
                .contains(EntityType.TRIP, EntityType.TICKET, EntityType.PARCEL);
        assertThat(all).extracting(Incident::getTypeIncident)
                .contains(TypeIncident.SECURITY, TypeIncident.DELIVERY_FAIL,
                        TypeIncident.VEHICLE, TypeIncident.OVERBOOK);
    }

    @Test
    @DisplayName("Guarda un incident cuando el reportante es null")
    void shouldSaveIncidentWithoutReporter() {
        var incident = createIncident(EntityType.TRIP,
                999L, TypeIncident.VEHICLE,
                null, "Fallo en la transmisión a baja velocidad ");

        Incident saved = incidentRepository.save(incident);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getReportedBy()).isNull();
        assertThat(saved.getNote()).isEqualTo("Fallo en la transmisión a baja velocidad ");
    }

    private User createAndSaveUser(String email, String phone) {
        var user = User.builder()
                .name("Test User")
                .email(email)
                .phone(phone)
                .role(Role.DRIVER)
                .passwordHash("hash-de-prueba")
                .build();

        return userRepository.save(user);
    }

    private Trip createAndSaveTrip() {
        var route = Route.builder()
                .code("ROUTE-477")
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        var savedRoute = routeRepository.save(route);

        var bus = Bus.builder()
                .plate("BUS-477")
                .capacity(40)
                .statusBus(StatusBus.ACTIVE)
                .build();
        var savedBus = busRepository.save(bus);

        var trip = Trip.builder()
                .route(savedRoute)
                .bus(savedBus)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now().plusHours(2))
                .arrivalEta(LocalDateTime.now().plusHours(10))
                .statusTrip(StatusTrip.SCHEDULED)
                .build();

        return tripRepository.save(trip);
    }

    private Incident createIncident(EntityType entityType, Long entityId,
                                    TypeIncident type, User reporter, String note) {
        return Incident.builder()
                .entityType(entityType)
                .entityId(entityId)
                .typeIncident(type)
                .note(note)
                .reportedBy(reporter)
                .build();
    }
}