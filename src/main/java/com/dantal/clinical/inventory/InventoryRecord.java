package com.dantal.clinical.inventory;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryRecord extends ClinicalResourceEntity {
}
