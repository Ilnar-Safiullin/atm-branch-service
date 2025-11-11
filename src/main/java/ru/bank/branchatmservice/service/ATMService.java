package ru.bank.branchatmservice.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.branchatmservice.dto.ATMFullDto;
import ru.bank.branchatmservice.dto.InfoAtmDto;
import ru.bank.branchatmservice.dto.request.AtmFilterDto;
import ru.bank.branchatmservice.dto.request.UpdateAtmInfoRequest;
import ru.bank.branchatmservice.dto.response.AtmFilterResponseDto;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.dto.response.InfoDeletionArchivingAtmResponse;
import ru.bank.branchatmservice.dto.request.AtmCreateDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.mapper.ATMMapper;
import ru.bank.branchatmservice.mapper.WorkScheduleMapper;
import ru.bank.branchatmservice.model.ATM;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.WorkSchedule;
import ru.bank.branchatmservice.repository.ATMRepository;
import ru.bank.branchatmservice.repository.WorkScheduleRepository;
import ru.bank.branchatmservice.specification.ATMSpecifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ATMService {
    private final ATMRepository atmRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final ATMMapper atmMapper;
    private final WorkScheduleMapper workScheduleMapper;
    private final BranchService branchService;
    private final CityService cityService;

    public ATMFullDto getATMById(UUID atmId) {
        ATM atm = atmRepository.findById(atmId).orElseThrow(() -> new NotFoundException("Данные не найдены"));
        return atmMapper.ofATMBranchAndWorkSchedule(atm, atm.getBranch(), atm.getWorkSchedule());
    }

    public List<InfoAtmDto> softDeleteATMByIds(List<UUID> atmIds) {
        log.info("softDeleteATMByIds {}", atmIds);
        List<ATM> atms = atmRepository.softDeleteByAtmIds(atmIds);
        if (atms.isEmpty()) {
            throw new NotFoundException("Запрашиваемы данные не найдены");
        }
        log.debug("кол-во архивированных атмов: {}", atms.size());
        return atms.stream()
                .map(atmMapper::toInfoAtmDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AtmFilterResponseDto> getAtmListByFilter(AtmFilterDto filters) {
        List<ATM> atms = atmRepository.findAll(ATMSpecifications.byAtmFilterDto(filters));
        log.debug("Кол-во найденных банкоматов {} по фильтру {}", atms.size(), filters);

        if (atms.isEmpty()) return List.of();
        return atms.stream().map(atmMapper::toAtmFilterResponseDto).toList();
    }

    @Transactional
    public MessageResponseDto createATM(AtmCreateDto atmCreateDto) {
        ATM atm = atmMapper.toATM(atmCreateDto);

        if (atmRepository.existsByNumber(atm.getNumber())) {
            throw new EntityExistsException(
                    String.format("ATM с номером %s уже существует'", atm.getNumber())
            );
        }

        Branch branch = branchService.findBranchByBankNumber(atmCreateDto.branchShortInfo().bankNumber());
        City city = cityService.getCityByName(atmCreateDto.addressFullInfoDto().cityName());


        atm.setBranch(branch);
        atm.getAddress().setCity(city);
        atmRepository.saveAndFlush(atm);

        List<WorkSchedule> workScheduleList = workScheduleMapper.toListWorkSchedules(atmCreateDto.scheduleArray());
        workScheduleList.forEach(ws -> {
            ws.setEntityId(atm.getId());
            ws.setEntityType(EntityType.ATM);
        });

        workScheduleRepository.saveAll(workScheduleList);

        return new MessageResponseDto(
                "Банкомат успешно добавлен",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @Transactional
    public InfoDeletionArchivingAtmResponse archiveAtm(UUID atmId) {

        ATM atm = atmRepository.findByIdAndIsClosedFalse(atmId)
                .orElseThrow(() -> new NotFoundException("ATM не найден или уже закрыт"));

        atm.setClosed(true);
        ATM savedAtm = atmRepository.saveAndFlush(atm);

        return createArchiveResponse(savedAtm);
    }

    private InfoDeletionArchivingAtmResponse createArchiveResponse(ATM entity) {
        return InfoDeletionArchivingAtmResponse.builder()
                        .inventoryNumber(entity.getInventoryNumber())
                        .name(entity.getAddress().getCity().getName())
                        .streetType(entity.getAddress().getStreetType())
                        .street(entity.getAddress().getStreet())
                        .house(entity.getAddress().getHouse())
                .build();
    }

    public List<AtmFilterResponseDto> getAllAtmList() {
        List<ATM> atms = atmRepository.findAll();

        return atms.stream().map(atmMapper::toAtmFilterResponseDto).toList();
    }

    @Transactional
    public void updateATM(UUID atmId, UpdateAtmInfoRequest request) {
        ATM atm = atmRepository.findById(atmId)
                .orElseThrow(() -> new NotFoundException("Данные не найдены."));
        workScheduleRepository.deleteAllByEntityIdAndEntityType(atmId, EntityType.ATM);

        atmMapper.toATM(request, atm);
        atmRepository.save(atm);

        List<WorkSchedule> updatedSchedules = new ArrayList<>();
        workScheduleMapper.ofWorkScheduleDtoList(request.getSchedules(), updatedSchedules);
        for (WorkSchedule workSchedule : updatedSchedules) {
            workSchedule.setEntityId(atmId);
            workSchedule.setEntityType(EntityType.ATM);
        }
        workScheduleRepository.saveAll(updatedSchedules);
    }
}
