package ru.bank.branchatmservice.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.bank.branchatmservice.dto.request.AddressInfo;
import ru.bank.branchatmservice.dto.request.BranchInfo;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchSpecifications {

    public static Specification<Branch> withBranchInfo(BranchInfo branchInfo) {
        return (root, query, criteriaBuilder) -> {
            if (branchInfo == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(branchInfo.getNameOrBankNumber())) {
                String searchText = branchInfo.getNameOrBankNumber();
                String digitsOnly = extractDigits(searchText);


                if (StringUtils.hasText(digitsOnly)) {
                    String searchTerm = digitsOnly + "%";

                    Predicate bankNumberPredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("bankNumber")),
                            searchTerm
                    );
                    predicates.add(bankNumberPredicate);
                } else {
                    String searchTerm = searchText.toLowerCase() + "%";

                    Predicate namePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            searchTerm
                    );


                    predicates.add(namePredicate);
                }
            }

            if (branchInfo.getHasCurrencyExchange() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("hasCurrencyExchange"),
                        branchInfo.getHasCurrencyExchange()
                ));
            }

            if (branchInfo.getHasPandus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("hasPandus"),
                        branchInfo.getHasPandus()
                ));
            }

            if (branchInfo.getIsClosed() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("isClosed"),
                        branchInfo.getIsClosed()
                ));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Branch> withAddressInfo(AddressInfo addressInfo) {
        return (root, query, criteriaBuilder) -> {
            if (addressInfo == null || !StringUtils.hasText(addressInfo.getFullAddress())) {
                return criteriaBuilder.conjunction();
            }

            Join<Branch, Address> addressJoin = root.join("address", JoinType.INNER);

            String searchPhrase = addressInfo.getFullAddress().trim().toLowerCase();
            String[] searchWords = searchPhrase.split("\\s+");

            List<Predicate> wordPredicates = new ArrayList<>();

            for (String word : searchWords) {
                if (!word.trim().isEmpty()) {
                    String searchTerm = "%" + word.trim() + "%";
                    wordPredicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(addressJoin.get("fullAddress")),
                            searchTerm
                    ));
                }
            }

            return wordPredicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(wordPredicates.toArray(new Predicate[0]));
        };
    }

    private static String extractDigits(String str) {
        if (!StringUtils.hasText(str)) {
            return "";
        }
        return str.replaceAll("\\D", "");
    }
}