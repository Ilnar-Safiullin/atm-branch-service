package ru.bank.branchatmservice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bank.branchatmservice.dto.BranchShortDtoProjection;
import ru.bank.branchatmservice.dto.response.BranchBankNumberDTO;
import ru.bank.branchatmservice.dto.response.BranchNameResponse;
import ru.bank.branchatmservice.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID>, JpaSpecificationExecutor<Branch> {
    @Query("SELECT b FROM Branch b " +
            "JOIN FETCH b.address a " +
            "JOIN FETCH a.city c " +
            "WHERE b.bankNumber = :bankNumber")
    Optional<Branch> findByBankNumber(@Param("bankNumber") String bankNumber);

    @Query(value = """
            SELECT
                b.id AS id,
                b.name AS name
            FROM branch b
            INNER JOIN branch_department bd ON b.id = bd.branch_id
            WHERE bd.department_id = :departmentId
            """, nativeQuery = true)
    List<BranchShortDtoProjection> findBranchesByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query(value = """
            SELECT new ru.bank.branchatmservice.dto.response.BranchNameResponse(b.name)
            FROM Branch b
            WHERE b.id = :bankBranchId
            """)
    Optional<BranchNameResponse> getBranchNameByBranchId(UUID bankBranchId);

    @Query("SELECT new ru.bank.branchatmservice.dto.response.BranchBankNumberDTO(b.bankNumber) FROM Branch b WHERE b.bankNumber LIKE :bankNumber%")
    List<BranchBankNumberDTO> findBankNumberDTOsByPrefix(@Param("bankNumber") String bankNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByBankNumber(String bankNumber);

    boolean existsByName(String name);

    @Query("""
        SELECT b FROM Branch b
        JOIN FETCH b.address a
        JOIN FETCH a.city c
        JOIN FETCH c.location
        LEFT JOIN FETCH b.workSchedule ws
        """)
    List<Branch> findAllWithAddress();

    @Query(value = """ 
            SELECT b FROM Branch b
            JOIN FETCH b.address a
            JOIN FETCH a.city c
            WHERE b.id IN (:bankBranchIds)
            """)
    Optional<List<Branch>> findAllByBranchId(@Param("bankBranchIds") List<UUID> bankBranchIds);

    @EntityGraph(attributePaths = {"address", "address.city", "workSchedule"})
    List<Branch> findAll(Specification<Branch> spec);

    @Modifying
    @Query(value = """
                INSERT INTO branch_department (branch_id, department_id)
                VALUES (:branchId, :departmentId)
            """, nativeQuery = true)
    void insertBranchDepartmentConnection(@Param("branchId") UUID branchId,
                                          @Param("departmentId") UUID departmentId);
}