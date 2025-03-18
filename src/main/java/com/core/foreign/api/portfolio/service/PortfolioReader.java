package com.core.foreign.api.portfolio.service;

import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.portfolio.dto.internal.BasicPortfolioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioReader {
    private final EmployeePortfolioRepository employeePortfolioRepository;

    // 기본만 조회한다.
    public BasicPortfolioDTO getBasicPortfolio(Employee employee) {

        EmployeePortfolio employeePortfolio = employeePortfolioRepository.findByEmployeeId(employee.getId())
                .orElseGet(() -> {
                    log.warn("완성된 포트 폴리오가 없음. employeeId= {}", employee.getId());
                    return null;
                });

        BasicPortfolioDTO response = BasicPortfolioDTO.from(employee, employeePortfolio);

        return response;
    }

}
