package com.dantal.clinical.reminder;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "reminders")
public class ReminderRecord extends ClinicalResourceEntity {
}
