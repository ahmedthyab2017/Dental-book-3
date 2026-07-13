package com.dantal.clinical.appointment;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class AppointmentRecord extends ClinicalResourceEntity {
}
