package com.makethediference.mtdapi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Consejo")
@Table(name = "consejeros")
@Getter
@Setter
@NoArgsConstructor
public class Council extends User {
}
