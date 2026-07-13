package com.dantal.clinical.expense;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/expenses")
@Tag(name = "Expense")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseRecordController extends ClinicalResourceController {
    public ExpenseRecordController(ExpenseRecordService service) {
        super(service, "expenses", "expense");
    }
}
