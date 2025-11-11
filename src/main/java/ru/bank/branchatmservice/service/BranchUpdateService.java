package ru.bank.branchatmservice.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.branchatmservice.dto.request.AddressUpdateDto;
import ru.bank.branchatmservice.dto.request.BranchInfoUpdateDto;
import ru.bank.branchatmservice.dto.request.WorkScheduleUpdateDto;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.enums.SchedualOptional;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.mapper.WorkScheduleMapper;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.WorkSchedule;
import ru.bank.branchatmservice.repository.BranchRepository;
import ru.bank.branchatmservice.repository.WorkScheduleRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchUpdateService {

    private final WorkScheduleRepository workScheduleRepository;
    private final CityService cityService;
    private final WorkScheduleMapper workScheduleMapper;
    private final BranchRepository branchRepository;

    @Transactional
    public void updateBranchInfo(Branch branch, BranchInfoUpdateDto branchInfo) {
        if (branchInfo == null) return;

        if (branchInfo.getName() != null) {
            if (branchRepository.existsByName(branchInfo.getName())) {
                throw new EntityExistsException(
                        String.format("Отделение с таким именем %s уже существует", branchInfo.getName())
                );
            }
            branch.setName(branchInfo.getName());
        }
        if (branchInfo.getBankNumber() != null) {
            if (branchRepository.existsByBankNumber(branchInfo.getBankNumber())) {
                throw new EntityExistsException(
                        String.format("Отделение с таким номером %s уже существует", branchInfo.getBankNumber())
                );
            }
            branch.setBankNumber(branchInfo.getBankNumber());
        }
        if (branchInfo.getPhoneNumber() != null) {
            if (branchRepository.existsByPhoneNumber(branchInfo.getPhoneNumber())) {
                throw new EntityExistsException(
                        String.format("Отделение с таким номером телефона %s уже существует", branchInfo.getPhoneNumber())
                );
            }
            branch.setPhoneNumber(branchInfo.getPhoneNumber());
        }
        if (branchInfo.getHasCurrencyExchange() != null) {
            branch.setHasCurrencyExchange(branchInfo.getHasCurrencyExchange());
        }
        if (branchInfo.getHasPandus() != null) {
            branch.setHasPandus(branchInfo.getHasPandus());
        }
        if (branchInfo.getIsClosed() != null) {
            branch.setClosed(!branchInfo.getIsClosed());
        }
        if (branchInfo.getType() != null) {
            branch.setType(branchInfo.getType());
        }
    }

    @Transactional
    public void updateAddress(Address address, AddressUpdateDto addressInfo) {
        if (addressInfo == null) return;

        if (addressInfo.getCityId() != null) {
            City newCity = cityService.getCityById(addressInfo.getCityId());
            address.setCity(newCity);
        }

        if (addressInfo.getStreetType() != null) {
            address.setStreetType(addressInfo.getStreetType());
        }
        if (addressInfo.getStreet() != null) {
            address.setStreet(addressInfo.getStreet());
        }
        if (addressInfo.getHouse() != null) {
            address.setHouse(addressInfo.getHouse());
        }
        if (addressInfo.getMetroStation() != null) {
            address.setMetroStation(addressInfo.getMetroStation());
        }
        if (addressInfo.getLatitude() != null) {
            address.setLatitude(new BigDecimal(addressInfo.getLatitude()));
        }
        if (addressInfo.getLongitude() != null) {
            address.setLongitude(new BigDecimal(addressInfo.getLongitude()));
        }
    }

    @Transactional
    public void updateWorkSchedule(UUID branchId, List<WorkScheduleUpdateDto> workScheduleDtos) {
        if (workScheduleDtos == null) return;

        List<WorkSchedule> workSchedules = workScheduleRepository.findAllByEntityIdAndEntityType(
                branchId, EntityType.BRANCH);

        workScheduleDtos.stream()
                .filter(x ->
                        x.getOptional() == SchedualOptional.DELETE || x.getOptional() == SchedualOptional.CHANGE)
                .forEach(x -> {
                    workSchedules.stream()
                            .filter(ws -> ws.getWeekDay().getDayNumber() == x.getWeekDay())
                            .findAny()
                            .orElseThrow(() -> new NotFoundException(
                                            String.format("День %s в графике работы отделения не найден", x.getWeekDay())
                                    )
                            );
                });

        workScheduleDtos.stream()
                .filter(x -> x.getOptional() == SchedualOptional.DELETE)
                .forEach(w -> {
                    workSchedules.forEach(workSchedule -> {
                        if(workSchedule.getWeekDay().getDayNumber() == w.getWeekDay()) {
                            workScheduleRepository.delete(workSchedule);
                        }
                    });
                });

        workScheduleDtos.stream()
                .filter( x -> x.getOptional() == SchedualOptional.CHANGE)
                .forEach(w -> workSchedules.forEach(workSchedule -> {
                    if (workSchedule.getWeekDay().getDayNumber() == w.getWeekDay()) {
                        updateWorkSchedule(workSchedule, w);
                        workScheduleRepository.save(workSchedule);
                    }
                }));

        workScheduleDtos.stream()
                .filter(x -> x.getOptional() == SchedualOptional.ADD)
                .map(workScheduleMapper::toWorkSchedule)
                .forEach(w -> {
                    workSchedules.forEach(workSchedule -> {
                        if (workSchedule.getWeekDay() == w.getWeekDay()) {
                            throw new EntityExistsException();
                        }
                    });
                    w.setEntityType(EntityType.BRANCH);
                    w.setEntityId(branchId);
                    workScheduleRepository.save(w);
                });
    }

    public void updateWorkSchedule(WorkSchedule targetWorkSchedule, WorkScheduleUpdateDto workScheduleUpdateDto) {

        if (workScheduleUpdateDto.getOpeningTime() != null) {
            targetWorkSchedule.setOpeningTime(
                    workScheduleMapper.parseTime(workScheduleUpdateDto.getOpeningTime())
            );
        }

        if (workScheduleUpdateDto.getClosingTime() != null){
            targetWorkSchedule.setClosingTime(
                    workScheduleMapper.parseTime(workScheduleUpdateDto.getClosingTime())
            );
        }
    }
}
