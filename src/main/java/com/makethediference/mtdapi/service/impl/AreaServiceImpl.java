package com.makethediference.mtdapi.service.impl;

import com.makethediference.mtdapi.domain.dto.area.RegisterArea;
import com.makethediference.mtdapi.domain.entity.Area;
import com.makethediference.mtdapi.infra.repository.AreaRepository;
import com.makethediference.mtdapi.infra.mapper.AreaMapper;
import com.makethediference.mtdapi.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {
    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    @Override
    public Area registerArea(RegisterArea dto) {
        Area area = areaMapper.toEntity(dto);
        return areaRepository.save(area);
    }
}
