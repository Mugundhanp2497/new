/**
 * Simple Test Runner for Stock Screen
 * Demonstrates key functionality without requiring JUnit framework
 */
public class SimpleTestRunner {
    
    private static int testCount = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("Stock Screen Test Suite");
        System.out.println("Garment Warehouse Management System");
        System.out.println("=================================\n");
        
        // Run all test categories
        testAddGarmentItem();
        testUpdateStock();
        testSearchFunctionality();
        testLowStockAlerts();
        testInventoryValue();
        testRemoveItem();
        testTransactionHistory();
        testEdgeCases();
        testWorkflowScenarios();
        
        // Print summary
        System.out.println("\n=================================");
        System.out.println("TEST SUMMARY");
        System.out.println("=================================");
        System.out.println("Total Tests: " + testCount);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success Rate: " + (passedTests * 100 / testCount) + "%");
        
        if (failedTests == 0) {
            System.out.println("\n✅ All tests PASSED! Stock screen is working correctly.");
        } else {
            System.out.println("\n❌ Some tests FAILED. Please review implementation.");
        }
    }
    
    // Test Add Garment Item functionality
    private static void testAddGarmentItem() {
        System.out.println("Testing Add Garment Item Functionality:");
        System.out.println("---------------------------------------");
        
        Stock stock = new Stock();
        
        // Test 1: Successful addition
        runTest("Add valid garment item", () -> {
            boolean result = stock.addGarmentItem("TSH001", "Cotton T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
            return result && stock.getItem("TSH001") != null;
        });
        
        // Test 2: Null item code should fail
        runTest("Reject null item code", () -> {
            return !stock.addGarmentItem(null, "T-Shirt", "Shirts", "L", "Red", 30, 20.00);
        });
        
        // Test 3: Empty item code should fail
        runTest("Reject empty item code", () -> {
            return !stock.addGarmentItem("", "T-Shirt", "Shirts", "L", "Red", 30, 20.00);
        });
        
        // Test 4: Multiple items
        runTest("Add multiple different items", () -> {
            return stock.addGarmentItem("JNS001", "Denim Jeans", "Pants", "32", "Blue", 30, 59.99) &&
                   stock.addGarmentItem("DRS001", "Summer Dress", "Dresses", "S", "Floral", 20, 39.99) &&
                   stock.getAllItems().size() == 3; // Including TSH001 from first test
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Update Stock functionality
    private static void testUpdateStock() {
        System.out.println("Testing Update Stock Functionality:");
        System.out.println("-----------------------------------");
        
        Stock stock = new Stock();
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        // Test 1: Increase quantity
        runTest("Increase stock quantity", () -> {
            boolean result = stock.updateStock("TSH001", 75);
            return result && stock.getItem("TSH001").getQuantity() == 75;
        });
        
        // Test 2: Decrease quantity
        runTest("Decrease stock quantity", () -> {
            boolean result = stock.updateStock("TSH001", 25);
            return result && stock.getItem("TSH001").getQuantity() == 25;
        });
        
        // Test 3: Negative quantity should fail
        runTest("Reject negative quantity", () -> {
            boolean result = stock.updateStock("TSH001", -10);
            return !result && stock.getItem("TSH001").getQuantity() == 25; // Should remain unchanged
        });
        
        // Test 4: Non-existent item should fail
        runTest("Reject update for non-existent item", () -> {
            return !stock.updateStock("NONEXISTENT", 100);
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Search functionality
    private static void testSearchFunctionality() {
        System.out.println("Testing Search Functionality:");
        System.out.println("-----------------------------");
        
        Stock stock = new Stock();
        // Setup test data
        stock.addGarmentItem("TSH001", "Cotton T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        stock.addGarmentItem("TSH002", "Polo T-Shirt", "Shirts", "L", "Blue", 30, 35.99);
        stock.addGarmentItem("JNS001", "Denim Jeans", "Pants", "32", "Blue", 20, 59.99);
        stock.addGarmentItem("DRS001", "Summer Dress", "Dresses", "S", "Red", 15, 39.99);
        
        // Test 1: Search by code
        runTest("Search by item code", () -> {
            return stock.searchItems("TSH", "CODE").size() == 2;
        });
        
        // Test 2: Search by name
        runTest("Search by item name", () -> {
            return stock.searchItems("T-Shirt", "NAME").size() == 2;
        });
        
        // Test 3: Search by category
        runTest("Search by category", () -> {
            return stock.searchItems("Shirts", "CATEGORY").size() == 2;
        });
        
        // Test 4: Search by color
        runTest("Search by color", () -> {
            return stock.searchItems("Blue", "COLOR").size() == 3;
        });
        
        // Test 5: Case insensitive search
        runTest("Case insensitive search", () -> {
            return stock.searchItems("blue", "COLOR").size() == 3 &&
                   stock.searchItems("BLUE", "COLOR").size() == 3;
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Low Stock Alerts
    private static void testLowStockAlerts() {
        System.out.println("Testing Low Stock Alerts:");
        System.out.println("-------------------------");
        
        Stock stock = new Stock();
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 5, 25.99);   // Low stock
        stock.addGarmentItem("JNS001", "Jeans", "Pants", "32", "Blue", 15, 59.99);    // Normal stock
        stock.addGarmentItem("DRS001", "Dress", "Dresses", "S", "Red", 3, 39.99);     // Very low
        
        // Test 1: Low stock detection
        runTest("Detect low stock items", () -> {
            return stock.getLowStockItems(10).size() == 2; // TSH001 and DRS001
        });
        
        // Test 2: No low stock when all items are well-stocked
        runTest("No low stock when well-stocked", () -> {
            return stock.getLowStockItems(2).size() == 0; // All items > 2
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Inventory Value calculation
    private static void testInventoryValue() {
        System.out.println("Testing Inventory Value Calculation:");
        System.out.println("------------------------------------");
        
        Stock stock = new Stock();
        
        // Test 1: Empty inventory value
        runTest("Empty inventory has zero value", () -> {
            return stock.getTotalInventoryValue() == 0.0;
        });
        
        // Test 2: Single item value
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 10, 25.99);
        runTest("Single item value calculation", () -> {
            double expected = 10 * 25.99;
            return Math.abs(stock.getTotalInventoryValue() - expected) < 0.01;
        });
        
        // Test 3: Multiple items value
        stock.addGarmentItem("JNS001", "Jeans", "Pants", "32", "Blue", 5, 59.99);
        runTest("Multiple items value calculation", () -> {
            double expected = (10 * 25.99) + (5 * 59.99);
            return Math.abs(stock.getTotalInventoryValue() - expected) < 0.01;
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Remove Item functionality
    private static void testRemoveItem() {
        System.out.println("Testing Remove Item Functionality:");
        System.out.println("----------------------------------");
        
        Stock stock = new Stock();
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        // Test 1: Successful removal
        runTest("Remove existing item", () -> {
            boolean result = stock.removeItem("TSH001");
            return result && stock.getItem("TSH001") == null;
        });
        
        // Test 2: Remove non-existent item should fail
        runTest("Reject removal of non-existent item", () -> {
            return !stock.removeItem("NONEXISTENT");
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Transaction History
    private static void testTransactionHistory() {
        System.out.println("Testing Transaction History:");
        System.out.println("----------------------------");
        
        Stock stock = new Stock();
        
        // Test 1: Transaction recording
        runTest("Record transactions correctly", () -> {
            stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
            stock.updateStock("TSH001", 75);
            stock.updateStock("TSH001", 60);
            stock.removeItem("TSH001");
            
            return stock.getTransactionHistory().size() == 4;
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Edge Cases
    private static void testEdgeCases() {
        System.out.println("Testing Edge Cases:");
        System.out.println("------------------");
        
        Stock stock = new Stock();
        
        // Test 1: Special characters in item data
        runTest("Handle special characters", () -> {
            boolean result = stock.addGarmentItem("TSH-001/A", "T-Shirt (Premium)", 
                                                "Shirts & Tops", "M/L", "Blue & White", 50, 25.99);
            return result && stock.getItem("TSH-001/A") != null;
        });
        
        // Test 2: Large quantities
        runTest("Handle large quantities", () -> {
            boolean result = stock.addGarmentItem("TSH002", "T-Shirt", "Shirts", "M", "Blue", 
                                                Integer.MAX_VALUE, 25.99);
            return result && stock.getItem("TSH002").getQuantity() == Integer.MAX_VALUE;
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Test Workflow Scenarios
    private static void testWorkflowScenarios() {
        System.out.println("Testing Workflow Scenarios:");
        System.out.println("---------------------------");
        
        Stock stock = new Stock();
        
        // Test 1: Complete receiving workflow
        runTest("Garment receiving workflow", () -> {
            // Receive multiple size variants
            stock.addGarmentItem("TSH001-S-BLU", "Cotton T-Shirt", "Shirts", "S", "Blue", 100, 19.99);
            stock.addGarmentItem("TSH001-M-BLU", "Cotton T-Shirt", "Shirts", "M", "Blue", 150, 19.99);
            stock.addGarmentItem("TSH001-L-BLU", "Cotton T-Shirt", "Shirts", "L", "Blue", 120, 19.99);
            
            // Verify inventory count and value
            double expectedValue = (100 + 150 + 120) * 19.99;
            return stock.getAllItems().size() == 3 && 
                   Math.abs(stock.getTotalInventoryValue() - expectedValue) < 0.01;
        });
        
        // Test 2: Order fulfillment workflow
        runTest("Order fulfillment workflow", () -> {
            // Process customer order (sell 50 medium shirts)
            GarmentItem mediumShirt = stock.getItem("TSH001-M-BLU");
            stock.updateStock("TSH001-M-BLU", mediumShirt.getQuantity() - 50);
            
            // Verify updated quantity
            return stock.getItem("TSH001-M-BLU").getQuantity() == 100;
        });
        
        // Test 3: Size variant management
        runTest("Size variant management", () -> {
            // Search for all blue items
            int blueItemCount = stock.searchItems("Blue", "COLOR").size();
            
            // Search for medium size items
            int mediumItemCount = stock.searchItems("M", "SIZE").size();
            
            return blueItemCount == 3 && mediumItemCount == 1;
        });
        
        stock.clearInventory();
        System.out.println();
    }
    
    // Helper method to run individual tests
    private static void runTest(String testName, TestCase test) {
        testCount++;
        try {
            boolean result = test.run();
            if (result) {
                System.out.println("✅ " + testName);
                passedTests++;
            } else {
                System.out.println("❌ " + testName);
                failedTests++;
            }
        } catch (Exception e) {
            System.out.println("❌ " + testName + " (Exception: " + e.getMessage() + ")");
            failedTests++;
        }
    }
    
    // Functional interface for test cases
    @FunctionalInterface
    private interface TestCase {
        boolean run() throws Exception;
    }
}