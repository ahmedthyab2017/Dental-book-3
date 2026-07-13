package com.dantal.clinical.archive;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "archives")
public class ArchiveRecord extends ClinicalResourceEntity {
}
