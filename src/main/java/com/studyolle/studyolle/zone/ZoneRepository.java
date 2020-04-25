package com.studyolle.studyolle.zone;

import com.studyolle.studyolle.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String cityName, String provinceName);
}
