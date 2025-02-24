package com.makethediference.mtdapi.service;

import com.makethediference.mtdapi.domain.dto.area.RegisterArea;
import com.makethediference.mtdapi.domain.entity.Area;

public interface AreaService {
    Area registerArea(RegisterArea dto);
}
