package com.dantal.clinical.service;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_catalog")
public class ServiceRecord extends ClinicalResourceEntity {
}
