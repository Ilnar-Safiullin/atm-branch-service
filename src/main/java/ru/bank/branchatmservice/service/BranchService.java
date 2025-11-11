package ru.bank.branchatmservice.service;

import jakarta.persistence.EntityExistsException;
import org.apache.coyote.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.BranchCreateDto;
import ru.bank.branchatmservice.dto.BranchDto;
import ru.bank.branchatmservice.dto.BranchFullDto;
import ru.bank.branchatmservice.dto.BranchShortDto;
import ru.bank.branchatmservice.dto.BranchShortDtoProjection;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.dto.request.BranchAndAddressInfoRequest;
import ru.bank.branchatmservice.dto.request.BranchSearchRequest;
import ru.bank.branchatmservice.dto.request.BranchUpdateRequestDto;
import ru.bank.branchatmservice.dto.response.BranchAndAddressInfoResponse;
import ru.bank.branchatmservice.dto.response.ArchiveBranchResponse;
import ru.bank.branchatmservice.dto.response.BranchBankNumberDTO;
import ru.bank.branchatmservice.dto.response.BranchDtoView;
import ru.bank.branchatmservice.dto.response.BranchListResponse;
import ru.bank.branchatmservice.dto.response.BranchNameResponse;
import ru.bank.branchatmservice.dto.response.BranchSearchResponseDto;
import ru.bank.branchatmservice.dto.response.BranchUnionDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.exception.BranchNotFoundException;
import ru.bank.branchatmservice.exception.CityNotFoundException;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.mapper.AddressMapper;
import ru.bank.branchatmservice.mapper.BranchMapper;
import ru.bank.branchatmservice.mapper.WorkScheduleMapper;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.WorkSchedule;
import ru.bank.branchatmservice.repository.BranchRepository;
import ru.bank.branchatmservice.repository.CityRepository;
import ru.bank.branchatmservice.repository.WorkScheduleRepository;
import ru.bank.branchatmservice.specification.BranchSpecifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {
    private final BranchRepository branchRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final BranchUpdateService branchUpdateService;
    private final CityRepository cityRepository;
    private final BranchMapper branchMapper;
    private final AddressMapper addressMapper;
    private final WorkScheduleMapper workScheduleMapper;
    private final WorkScheduleService workScheduleService;

    public List<BranchSearchResponseDto> searchBranchesByFilter(BranchSearchRequest branchSearchRequest) {
        Specification<Branch> spec = Specification.where(
                BranchSpecifications.withBranchInfo(branchSearchRequest.getBranchInfo())
        ).and(
                BranchSpecifications.withAddressInfo(branchSearchRequest.getAddressInfo())
        );
        List<Branch> branches = branchRepository.findAll(spec);

        return branches.stream()
                .map(branch -> {
                    BranchFullDto branchFullDto = branchMapper.toBranchFullDto(branch);
                    branchFullDto.setOpen(isBranchOpen(branch));

                    AddressShortDto addressShortDto = addressMapper.toAddressShortDto(branch.getAddress());
                    return new BranchSearchResponseDto(branchFullDto, addressShortDto);
                })
                .collect(Collectors.toList());
    }

    public BranchAndAddressInfoResponse getBranchAndAddressInfo(BranchAndAddressInfoRequest request) {
        Branch branch = branchRepository.findByBankNumber(request.getBankNumber())
                .orElseThrow(() -> new BranchNotFoundException("Данные не найдены"));

        BranchFullDto branchInfoDto = branchMapper.toBranchFullDto(branch);
        Address address = branch.getAddress();
        AddressShortDto addressInfoDto = addressMapper.toAddressShortDto(address);

        return new BranchAndAddressInfoResponse(branchInfoDto, addressInfoDto);
    }

    public List<BranchShortDto> findBranchesByDepartmentId(UUID departmentId) {
        List<BranchShortDtoProjection> branches = branchRepository.findBranchesByDepartmentId(departmentId);

        return branches.stream()
                .map(branch -> new BranchShortDto(branch.getId(), branch.getName()))
                .toList();
    }

    public BranchNameResponse getBranchNameByBranchId(UUID bankBranchId) {
        return branchRepository.getBranchNameByBranchId(bankBranchId)
                .orElseThrow(() -> new NotFoundException("Запрашиваемые данные не найдены."));
    }

    public Branch findBranchByBankNumber(String bankNumber) {
        return branchRepository.findByBankNumber(bankNumber)
                .orElseThrow(() ->
                        new BranchNotFoundException(
                                String.format("Отделение с номером %s не найдено", bankNumber)
                        )
                );
    }

    public List<BranchBankNumberDTO> getBranchBankNumberByBankNumber(String bankNumber) {
        log.info("Получение списка номер банка по начальному значению номера банка");
        List<BranchBankNumberDTO> numberDTOList = branchRepository.findBankNumberDTOsByPrefix(bankNumber);
        if (numberDTOList.isEmpty()) {
            throw new NotFoundException("Данные не найдены");
        }
        return numberDTOList;
    }

    public BranchUnionDto findBranchById(UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new BranchNotFoundException(
                                String.format("Отделение с id %s не найдено", branchId)
                        )
                );

        BranchDtoView branchDtoView = branchMapper.toBranchDtoView(branch);
        List<ScheduleDto> scheduleDtoList = workScheduleMapper.ofWorkSchedules(branch.getWorkSchedule());

        return new BranchUnionDto(branchDtoView, scheduleDtoList);
    }

    public List<BranchListResponse> getBranches() {
        List<Branch> branches = branchRepository.findAllWithAddress();
        return branches.stream()
                .map(branch -> {
                    BranchDto branchDto = branchMapper.toBranchDto(branch);
                    BranchDto updatedBranchDto = branchDto.withOpen(isBranchOpen(branch));

                    AddressShortDto addressDto = addressMapper.toAddressShortDto(branch.getAddress());
                    return new BranchListResponse(updatedBranchDto, addressDto);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateBranch(UUID branchId, BranchUpdateRequestDto branchUpdateRequestDto) {
        log.info("Starting partial update for branch with id: {}", branchId);

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(
                        String.format("Branch not found with id: {}", branchId))
                );

        branchUpdateService.updateBranchInfo(branch, branchUpdateRequestDto.getBranchInfo());

        branchUpdateService.updateAddress(branch.getAddress(), branchUpdateRequestDto.getAddressInfo());

        branchUpdateService.updateWorkSchedule(branch.getId(), branchUpdateRequestDto.getWorkSchedule());

        branchRepository.save(branch);

        log.info("Successfully updated branch with id: {}", branchId);
    }

    @Transactional
    public MessageResponseDto createBranch(BranchCreateDto newBranch) {
        Branch branch = branchMapper.ofBranchCreateDto(newBranch.branchInfo(), newBranch.addressInfo(), false);

        List<String> conflicts = new ArrayList<>();
        if (branchRepository.existsByBankNumber(branch.getBankNumber())) {
            conflicts.add(String.format("Отделение с таким номером %s уже существует", branch.getBankNumber()));
        }
        if (branchRepository.existsByPhoneNumber(branch.getPhoneNumber())) {
            conflicts.add(String.format("Отделение с таким номером телефона %s уже существует", branch.getPhoneNumber()));
        }
        if (branchRepository.existsByName(branch.getName())) {
            conflicts.add(String.format("Отделение с таким именем %s уже существует", branch.getName()));
        }

        if (!conflicts.isEmpty()) {
            throw new EntityExistsException(String.join("; ", conflicts));
        }

        City city = cityRepository.findByName(newBranch.addressInfo().cityName())
                .orElseThrow(() -> new CityNotFoundException(
                        String.format("Город с наименованием %s не найден", newBranch.addressInfo().cityName())
                ));
        branch.getAddress().setCity(city);

        branchRepository.saveAndFlush(branch);

        for (UUID departmentId : newBranch.departmentIds()) {
            branchRepository.insertBranchDepartmentConnection(branch.getId(), departmentId);
        }

        List<WorkSchedule> workScheduleList = workScheduleMapper.toListWorkSchedules(newBranch.scheduleArray());
        workScheduleList.forEach(ws -> {
            ws.setEntityId(branch.getId());
            ws.setEntityType(EntityType.BRANCH);
        });

        workScheduleRepository.saveAll(workScheduleList);

        return new MessageResponseDto(
                "Отделение успешно добавлено",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    public ArchiveBranchResponse archiveBranchesId(List<UUID> branchListDto) throws BadRequestException {
        if (branchListDto.isEmpty())
            throw new BadRequestException("Страница не найдена");

        List<Branch> branches = branchRepository.findAllByBranchId(branchListDto)
                .filter(list -> list.size() == branchListDto.size())
                .orElseThrow(() -> new NotFoundException("Такого отделения не существует"));


        for (Branch branch : branches) {
            if (branch.isClosed())
                throw new EntityExistsException("Отделение " + branch.getId() + " уже находится в архиве.");
        }

        ArchiveBranchResponse archiveBranchResponse = new ArchiveBranchResponse();

        archiveBranchResponse.setArchived(branches.stream()
                .peek(b -> b.setClosed(true))
                .map(branchMapper::toArchiveBranchResponse)
                .toList());

        branchRepository.saveAll(branches);
        return archiveBranchResponse;
    }

    private boolean isBranchOpen(Branch branch) {
        if (branch.isClosed()) {
            return false;
        }
        return workScheduleService.isBranchOpenNow(branch.getWorkSchedule());
    }
}