package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.area.RegisterArea;
import com.makethediference.mtdapi.domain.entity.Area;
import org.springframework.stereotype.Component;

@Component
public class AreaMapper {

    public Area toEntity(RegisterArea dto) {
        return new Area(null, dto.name(), dto.color(), null, null);
    }
}
