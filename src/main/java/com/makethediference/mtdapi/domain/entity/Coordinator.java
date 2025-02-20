package com.makethediference.mtdapi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Coordinator")
@Table(name = "coordinators")
@Getter
@Setter
@NoArgsConstructor
public class Coordinator extends User {
}
