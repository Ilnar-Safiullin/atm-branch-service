package ru.bank.branchatmservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.bank.branchatmservice.enums.BranchType;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.Location;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BranchRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.liquibase.enabled", () -> "true");
    }


    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Address address;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        Location location = new Location();
        location.setCountyName("Location");
        entityManager.getEntityManager().persist(location);

        City city = new City();
        city.setName("Moscow");
        city.setLocation(location);
        entityManager.getEntityManager().persist(city);

        address = new Address();
        address.setStreet("Tverskaya st.");
        address.setStreetType("type");
        address.setHouse("house");
        address.setCity(city);
        address.setFullAddress(city.getName() + " Tverskaya st. type house 1");
        entityManager.getEntityManager().persist(address);
    }

    @Test
    void findByBankNumber_WhenBranchExists_ShouldReturnBranchWithFetchedRelations() {
        Branch branch = new Branch();
        branch.setBankNumber("123");
        branch.setName("Main Branch");
        branch.setPhoneNumber("+37544235654");
        branch.setAddress(address);
        branch.setType(BranchType.BRANCH);
        entityManager.persist(branch);

        entityManager.flush();
        entityManager.clear();

        Optional<Branch> result = branchRepository.findByBankNumber("123");

        assertTrue(result.isPresent());
        assertEquals("123", result.get().getBankNumber());
        assertNotNull(result.get().getAddress());
        assertNotNull(result.get().getAddress().getCity());
        assertEquals("Moscow", result.get().getAddress().getCity().getName());
    }


    @Test
    void findByBankNumber_WithDifferentCase_ShouldNotFind() {
        Branch branch = new Branch();
        branch.setName("Main");
        branch.setBankNumber("ABC");
        branch.setPhoneNumber("+37544235654");
        branch.setAddress(address);
        branch.setType(BranchType.BRANCH);
        entityManager.persist(branch);
        entityManager.flush();
        entityManager.clear();

        Optional<Branch> resultFailed = branchRepository.findByBankNumber("abc");
        Optional<Branch> resultSuccess = branchRepository.findByBankNumber("ABC");

        assertFalse(resultFailed.isPresent());
        assertTrue(resultSuccess.isPresent());
    }
}