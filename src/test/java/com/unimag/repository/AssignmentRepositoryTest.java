package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.*;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.Status_Bus;
import com.unimag.entities.Enums.Status_Trip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Test
    @DisplayName("Guardar un assignment")
    void shouldSaveAssignment() {
        var trip = createAndSaveTrip("ROUTE-123","ASD 788");
        var driver = createAndSaveUser("driver@example.com", "3001111111", Role.DRIVER);
        var dispatcher = createAndSaveUser("dispatcher@example.com", "3002222222",Role.DISPATCHER);

        var assignment = createAssignment(trip,driver,dispatcher);

        Assignment saved = assignmentRepository.save(assignment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getChecklistOk()).isTrue();
        assertThat(saved.getAssignedAt()).isNotNull();
    }


    @Test
    @DisplayName("Buscar un assignment por id de viaje")
    void findByTripId() {

        Trip trip = createAndSaveTrip("ROUTE-132","ASD 778");
        User driver = createAndSaveUser("find@example.com", "3003333333", Role.DRIVER);
        User dispatcher = createAndSaveUser("disp@example.com", "3004444444", Role.DISPATCHER);

        Assignment assignment = createAssignment(trip, driver, dispatcher);
        assignmentRepository.save(assignment);

        Optional<Assignment> found = assignmentRepository.findByTripId(trip.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getDriver().getId()).isEqualTo(driver.getId());
        assertThat(found.get().getDispatcher().getId()).isEqualTo(dispatcher.getId());
    }

    @Test
    @DisplayName("Buscar assignment que no existe en un viaje")
    void shouldNotFindAssignmentForUnassignedTrip() {

        Trip trip = createAndSaveTrip("ROUTE-321","AST 788");

        Optional<Assignment> found = assignmentRepository.findByTripId(trip.getId());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Buscar si existe assignment por id de viaje")
    void findByDriverId() {

        Trip trip = createAndSaveTrip("ROUTE-897","PLM 587");
        User driver = createAndSaveUser("exists@example.com", "3007070707", Role.DRIVER);
        User dispatcher = createAndSaveUser("dispexists@example.com", "3008080808", Role.DISPATCHER);

        Assignment assignment = createAssignment(trip, driver, dispatcher);
        assignmentRepository.save(assignment);

        boolean exists = assignmentRepository.existsByTripId( trip.getId());
        boolean notExists = assignmentRepository.existsByTripId(99999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Listar assignment por id de dispatcher")
    void findByDispatcherId() {

        User driver1 = createAndSaveUser("d1@example.com", "3007777777", Role.DRIVER);
        User driver2 = createAndSaveUser("d2@example.com", "3008888888", Role.DRIVER);
        User dispatcher = createAndSaveUser("disp2@example.com", "3009999999", Role.DISPATCHER);

        Trip trip1 = createAndSaveTrip("ROUTE-444","ERT 788");
        Trip trip2 = createAndSaveTrip("ROUTE-477","TYR 788");

        assignmentRepository.saveAll(List.of(
                createAssignment(trip1, driver1, dispatcher),
                createAssignment(trip2, driver2, dispatcher)
        ));

        List<Assignment> assignments = assignmentRepository.findByDispatcherId(dispatcher.getId());

        assertThat(assignments).hasSize(2);
        assertThat(assignments).allMatch(a -> a.getDispatcher().getId().equals(dispatcher.getId()));
    }

    @Test
    @DisplayName("Buscar assignment por viaje con detalles")
    void findByTripIdWithDetails() {

        Trip trip = createAndSaveTrip("ROUTE-421","ERW 788");
        User driver = createAndSaveUser("details@example.com", "3001010101", Role.DRIVER);
        User dispatcher = createAndSaveUser("dispdetails@example.com", "3002020202", Role.DISPATCHER);

        Assignment assignment = createAssignment(trip, driver, dispatcher);
        assignmentRepository.save(assignment);

        Optional<Assignment> found = assignmentRepository.findByTripIdWithDetails(trip.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTrip()).isNotNull();
        assertThat(found.get().getDriver()).isNotNull();
        assertThat(found.get().getDriver().getName()).isNotEmpty();
    }

    @Test
    @DisplayName("Listar ssignment po id de driver")
    void findActiveAssignmentsByDriver() {

        User driver = createAndSaveUser("driver1@example.com", "3005555555", Role.DRIVER);
        User dispatcher = createAndSaveUser("disp1@example.com", "3006666666", Role.DISPATCHER);

        Trip trip1 = createAndSaveTrip("ROUTE-324","ASD 121");
        Trip trip2 = createAndSaveTrip("ROUTE-188","ASD 789");

        assignmentRepository.saveAll(List.of(
                createAssignment(trip1, driver, dispatcher),
                createAssignment(trip2, driver, dispatcher)
        ));

        List<Assignment> assignments = assignmentRepository.findByDriverId(driver.getId());

        assertThat(assignments).hasSize(2);
        assertThat(assignments).allMatch(a -> a.getDriver().getId().equals(driver.getId()));
    }

    @Test
    @DisplayName("Listar assignment por rango de fecha/hora de salida")
    void findByDepartureDateRange() {

        User driver = createAndSaveUser("range@example.com", "3005050505", Role.DRIVER);
        User dispatcher = createAndSaveUser("disprange@example.com", "3006060606", Role.DISPATCHER);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(10);

        Trip trip1 = createTripWithDeparture(now.plusHours(2),"ROUTE-999","KLI 178");
        Trip trip2 = createTripWithDeparture(now.plusHours(5), "ROUTE-921","IOP 765");
        Trip trip3 = createTripWithDeparture(now.plusHours(12),"ROUTE-539","297");

        assignmentRepository.saveAll(List.of(
                createAssignment(trip1, driver, dispatcher),
                createAssignment(trip2, driver, dispatcher),
                createAssignment(trip3, driver, dispatcher)
        ));

        List<Assignment> assignments = assignmentRepository.findByDepartureDateRange(start, end);

        assertThat(assignments).hasSize(2);
    }

    @Test
    @DisplayName("Buscar si existe assignment por id de viaje")
    void existsByTripId() {

        Trip trip = createAndSaveTrip("ROUTE-897","PLM 587");
        User driver = createAndSaveUser("exists@example.com", "3007070707", Role.DRIVER);
        User dispatcher = createAndSaveUser("dispexists@example.com", "3008080808", Role.DISPATCHER);

        Assignment assignment = createAssignment(trip, driver, dispatcher);
        assignmentRepository.save(assignment);

        boolean exists = assignmentRepository.existsByTripId(trip.getId());
        boolean notExists = assignmentRepository.existsByTripId(99999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Buscar si existe assignment por id de viaje")
    void shouldCheckIfAssignmentExistsByTripId() {
        Trip trip = createAndSaveTrip("ROUTE-897","PLM 587");
        User driver = createAndSaveUser("exists@example.com", "3007070707", Role.DRIVER);
        User dispatcher = createAndSaveUser("dispexists@example.com", "3008080808", Role.DISPATCHER);

        Assignment assignment = createAssignment(trip, driver, dispatcher);
        assignmentRepository.save(assignment);

        boolean exists = assignmentRepository.existsByTripId( trip.getId());
        boolean notExists = assignmentRepository.existsByTripId(99999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Actualizar el estado del Checklist")
    void shouldUpdateChecklistStatus() {
        Trip trip = createAndSaveTrip("ROUTE-956","NBV 223");
        User driver = createAndSaveUser("update@example.com", "3009090909", Role.DRIVER);
        User dispatcher = createAndSaveUser("dispupdate@example.com", "3001010100", Role.DISPATCHER);

        Assignment assignment = createAssignment(trip, driver, dispatcher);
        assignment.setChecklistOk(false);
        Assignment saved = assignmentRepository.save(assignment);

        saved.setChecklistOk(true);
        assignmentRepository.save(saved);

        Assignment updated = assignmentRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getChecklistOk()).isTrue();
    }

    @Test
    @DisplayName("Verificar que un viaje solo tiene un assignment registrado")
    void shouldEnforceOneTripPerAssignment() {
        Trip trip = createAndSaveTrip("ROUTE-479","ASC 123");
        User driver1 = createAndSaveUser("driver1x@example.com", "3002020200", Role.DRIVER);
        User dispatcher = createAndSaveUser("dispx@example.com", "3004040400", Role.DISPATCHER);

        Assignment assignment1 = createAssignment(trip, driver1, dispatcher);
        assignmentRepository.save(assignment1);

        Optional<Assignment> existing = assignmentRepository.findByTripId(trip.getId());

        assertThat(existing).isPresent();
        assertThat(existing.get().getDriver().getId()).isEqualTo(driver1.getId());
    }

    private Trip createAndSaveTrip(String code, String plateBus) {

        return createTripForDate(LocalDate.now(), code, plateBus);
    }

    private Trip createTripForDate(LocalDate date, String code, String plateBus) {
        Route route = Route.builder()
                .code(code)
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        Route savedRoute = routeRepository.save(route);

        Bus bus = Bus.builder()
                .plate(plateBus)
                .capacity(40)
                .status_bus(Status_Bus.ACTIVE)
                .build();
        Bus savedBus = busRepository.save(bus);

        Trip trip = Trip.builder()
                .route(savedRoute)
                .bus(savedBus)
                .date(date)
                .departureAt(date.atTime(10, 0))
                .arrivalEta(date.atTime(18, 0))
                .status(Status_Trip.SCHEDULED)
                .build();

        return tripRepository.save(trip);
    }

    private Trip createTripWithDeparture(LocalDateTime departureAt, String code, String plateBus) {
        Route route = Route.builder()
                .code(code)
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        Route savedRoute = routeRepository.save(route);

        Bus bus = Bus.builder()
                .plate(plateBus)
                .capacity(40)
                .status_bus(Status_Bus.ACTIVE)
                .build();
        Bus savedBus = busRepository.save(bus);

        Trip trip = Trip.builder()
                .route(savedRoute)
                .bus(savedBus)
                .date(departureAt.toLocalDate())
                .departureAt(departureAt)
                .arrivalEta(departureAt.plusHours(8))
                .status(Status_Trip.SCHEDULED)
                .build();

        return tripRepository.save(trip);
    }

    private User createAndSaveUser(String email, String phone, Role role) {
        var user = User.builder()
                .name("Test User")
                .email(email)
                .phone(phone)
                .role(role)
                .passwordHash("hash-de-prueba")
                .build();
        return userRepository.save(user);
    }

    private Assignment createAssignment(Trip trip, User driver, User dispatcher) {
        return Assignment.builder()
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .checklistOk(true)
                .build();
    }
}