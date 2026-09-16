package com.hosteldesk.backend;

import com.hosteldesk.backend.entity.Department;
import com.hosteldesk.backend.service.RoutingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class RoutingEngineTest {

    @Autowired
    private RoutingService routingService;

    @Test
    @Transactional
    void testCategoryRoutingToDepartments() {
        Department plumbing = routingService.resolveDepartmentForCategory("PLUMBING");
        Assertions.assertNotNull(plumbing);
        Assertions.assertEquals("PLUMBING", plumbing.getName());

        Department electrical = routingService.resolveDepartmentForCategory("ELECTRICAL");
        Assertions.assertNotNull(electrical);
        Assertions.assertEquals("ELECTRICAL", electrical.getName());

        Department internet = routingService.resolveDepartmentForCategory("INTERNET");
        Assertions.assertNotNull(internet);
        Assertions.assertEquals("INTERNET", internet.getName());

        Department unmapped = routingService.resolveDepartmentForCategory("UNKNOWN_CATEGORY_XYZ");
        Assertions.assertNotNull(unmapped);
        Assertions.assertEquals("GENERAL", unmapped.getName());
    }
}
