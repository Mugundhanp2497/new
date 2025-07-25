import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Comprehensive Test Suite for Stock Screen in Warehouse Management System
 * Tests cover all functionality for garment industry inventory management
 */
public class StockTest {
    
    private Stock stock;
    
    @BeforeEach
    void setUp() {
        stock = new Stock();
    }
    
    @AfterEach
    void tearDown() {
        stock.clearInventory();
    }
    
    // ================== ADD GARMENT ITEM TESTS ==================
    
    @Test
    @DisplayName("Test successful addition of garment item")
    void testAddGarmentItem_Success() {
        boolean result = stock.addGarmentItem("TSH001", "Cotton T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        assertTrue(result, "Should successfully add garment item");
        
        GarmentItem addedItem = stock.getItem("TSH001");
        assertNotNull(addedItem, "Added item should be retrievable");
        assertEquals("TSH001", addedItem.getItemCode());
        assertEquals("Cotton T-Shirt", addedItem.getItemName());
        assertEquals("Shirts", addedItem.getCategory());
        assertEquals("M", addedItem.getSize());
        assertEquals("Blue", addedItem.getColor());
        assertEquals(50, addedItem.getQuantity());
        assertEquals(25.99, addedItem.getPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test addition with null item code")
    void testAddGarmentItem_NullItemCode() {
        boolean result = stock.addGarmentItem(null, "T-Shirt", "Shirts", "L", "Red", 30, 20.00);
        
        assertFalse(result, "Should fail when item code is null");
        assertTrue(stock.getAllItems().isEmpty(), "No items should be added");
    }
    
    @Test
    @DisplayName("Test addition with empty item code")
    void testAddGarmentItem_EmptyItemCode() {
        boolean result = stock.addGarmentItem("", "T-Shirt", "Shirts", "L", "Red", 30, 20.00);
        
        assertFalse(result, "Should fail when item code is empty");
        assertTrue(stock.getAllItems().isEmpty(), "No items should be added");
    }
    
    @Test
    @DisplayName("Test addition with whitespace-only item code")
    void testAddGarmentItem_WhitespaceItemCode() {
        boolean result = stock.addGarmentItem("   ", "T-Shirt", "Shirts", "L", "Red", 30, 20.00);
        
        assertFalse(result, "Should fail when item code is whitespace only");
        assertTrue(stock.getAllItems().isEmpty(), "No items should be added");
    }
    
    @Test
    @DisplayName("Test addition of multiple different items")
    void testAddGarmentItem_MultipleItems() {
        assertTrue(stock.addGarmentItem("TSH001", "Cotton T-Shirt", "Shirts", "M", "Blue", 50, 25.99));
        assertTrue(stock.addGarmentItem("JNS001", "Denim Jeans", "Pants", "32", "Blue", 30, 59.99));
        assertTrue(stock.addGarmentItem("DRS001", "Summer Dress", "Dresses", "S", "Floral", 20, 39.99));
        
        assertEquals(3, stock.getAllItems().size(), "Should have 3 items in inventory");
    }
    
    // ================== UPDATE STOCK TESTS ==================
    
    @Test
    @DisplayName("Test successful stock update - increase quantity")
    void testUpdateStock_IncreaseQuantity() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        boolean result = stock.updateStock("TSH001", 75);
        
        assertTrue(result, "Should successfully update stock");
        assertEquals(75, stock.getItem("TSH001").getQuantity(), "Quantity should be updated");
    }
    
    @Test
    @DisplayName("Test successful stock update - decrease quantity")
    void testUpdateStock_DecreaseQuantity() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        boolean result = stock.updateStock("TSH001", 25);
        
        assertTrue(result, "Should successfully update stock");
        assertEquals(25, stock.getItem("TSH001").getQuantity(), "Quantity should be updated");
    }
    
    @Test
    @DisplayName("Test stock update with zero quantity")
    void testUpdateStock_ZeroQuantity() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        boolean result = stock.updateStock("TSH001", 0);
        
        assertTrue(result, "Should successfully update to zero quantity");
        assertEquals(0, stock.getItem("TSH001").getQuantity(), "Quantity should be zero");
    }
    
    @Test
    @DisplayName("Test stock update with negative quantity")
    void testUpdateStock_NegativeQuantity() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        boolean result = stock.updateStock("TSH001", -10);
        
        assertFalse(result, "Should fail with negative quantity");
        assertEquals(50, stock.getItem("TSH001").getQuantity(), "Original quantity should remain unchanged");
    }
    
    @Test
    @DisplayName("Test stock update for non-existent item")
    void testUpdateStock_NonExistentItem() {
        boolean result = stock.updateStock("NONEXISTENT", 100);
        
        assertFalse(result, "Should fail for non-existent item");
    }
    
    // ================== SEARCH FUNCTIONALITY TESTS ==================
    
    @Test
    @DisplayName("Test search by item code")
    void testSearchItems_ByCode() {
        setupTestData();
        
        List<GarmentItem> results = stock.searchItems("TSH", "CODE");
        
        assertEquals(2, results.size(), "Should find 2 items with 'TSH' in code");
        assertTrue(results.stream().allMatch(item -> item.getItemCode().contains("TSH")));
    }
    
    @Test
    @DisplayName("Test search by item name")
    void testSearchItems_ByName() {
        setupTestData();
        
        List<GarmentItem> results = stock.searchItems("T-Shirt", "NAME");
        
        assertEquals(2, results.size(), "Should find 2 T-Shirt items");
        assertTrue(results.stream().allMatch(item -> item.getItemName().contains("T-Shirt")));
    }
    
    @Test
    @DisplayName("Test search by category")
    void testSearchItems_ByCategory() {
        setupTestData();
        
        List<GarmentItem> results = stock.searchItems("Shirts", "CATEGORY");
        
        assertEquals(2, results.size(), "Should find 2 shirt items");
        assertTrue(results.stream().allMatch(item -> item.getCategory().equals("Shirts")));
    }
    
    @Test
    @DisplayName("Test search by color")
    void testSearchItems_ByColor() {
        setupTestData();
        
        List<GarmentItem> results = stock.searchItems("Blue", "COLOR");
        
        assertEquals(2, results.size(), "Should find 2 blue items");
        assertTrue(results.stream().allMatch(item -> item.getColor().equals("Blue")));
    }
    
    @Test
    @DisplayName("Test search by size")
    void testSearchItems_BySize() {
        setupTestData();
        
        List<GarmentItem> results = stock.searchItems("M", "SIZE");
        
        assertEquals(1, results.size(), "Should find 1 medium size item");
        assertEquals("M", results.get(0).getSize());
    }
    
    @Test
    @DisplayName("Test case-insensitive search")
    void testSearchItems_CaseInsensitive() {
        setupTestData();
        
        List<GarmentItem> results1 = stock.searchItems("blue", "COLOR");
        List<GarmentItem> results2 = stock.searchItems("BLUE", "COLOR");
        List<GarmentItem> results3 = stock.searchItems("Blue", "COLOR");
        
        assertEquals(2, results1.size());
        assertEquals(2, results2.size());
        assertEquals(2, results3.size());
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
    }
    
    @Test
    @DisplayName("Test search with no results")
    void testSearchItems_NoResults() {
        setupTestData();
        
        List<GarmentItem> results = stock.searchItems("NonExistent", "NAME");
        
        assertTrue(results.isEmpty(), "Should return empty list for no matches");
    }
    
    // ================== LOW STOCK ALERTS TESTS ==================
    
    @Test
    @DisplayName("Test low stock detection")
    void testGetLowStockItems() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 5, 25.99);
        stock.addGarmentItem("JNS001", "Jeans", "Pants", "32", "Blue", 15, 59.99);
        stock.addGarmentItem("DRS001", "Dress", "Dresses", "S", "Red", 3, 39.99);
        
        List<GarmentItem> lowStockItems = stock.getLowStockItems(10);
        
        assertEquals(2, lowStockItems.size(), "Should find 2 items with stock <= 10");
        assertTrue(lowStockItems.stream().anyMatch(item -> item.getItemCode().equals("TSH001")));
        assertTrue(lowStockItems.stream().anyMatch(item -> item.getItemCode().equals("DRS001")));
    }
    
    @Test
    @DisplayName("Test low stock with zero threshold")
    void testGetLowStockItems_ZeroThreshold() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 0, 25.99);
        stock.addGarmentItem("JNS001", "Jeans", "Pants", "32", "Blue", 15, 59.99);
        
        List<GarmentItem> lowStockItems = stock.getLowStockItems(0);
        
        assertEquals(1, lowStockItems.size(), "Should find 1 item with stock = 0");
        assertEquals("TSH001", lowStockItems.get(0).getItemCode());
    }
    
    @Test
    @DisplayName("Test low stock with no items meeting criteria")
    void testGetLowStockItems_NoLowStock() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        stock.addGarmentItem("JNS001", "Jeans", "Pants", "32", "Blue", 30, 59.99);
        
        List<GarmentItem> lowStockItems = stock.getLowStockItems(10);
        
        assertTrue(lowStockItems.isEmpty(), "Should find no low stock items");
    }
    
    // ================== REMOVE ITEM TESTS ==================
    
    @Test
    @DisplayName("Test successful item removal")
    void testRemoveItem_Success() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        
        boolean result = stock.removeItem("TSH001");
        
        assertTrue(result, "Should successfully remove item");
        assertNull(stock.getItem("TSH001"), "Item should no longer exist");
        assertTrue(stock.getAllItems().isEmpty(), "Inventory should be empty");
    }
    
    @Test
    @DisplayName("Test removal of non-existent item")
    void testRemoveItem_NonExistent() {
        boolean result = stock.removeItem("NONEXISTENT");
        
        assertFalse(result, "Should fail to remove non-existent item");
    }
    
    // ================== INVENTORY VALUE TESTS ==================
    
    @Test
    @DisplayName("Test total inventory value calculation")
    void testGetTotalInventoryValue() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 10, 25.99);     // 259.90
        stock.addGarmentItem("JNS001", "Jeans", "Pants", "32", "Blue", 5, 59.99);       // 299.95
        stock.addGarmentItem("DRS001", "Dress", "Dresses", "S", "Red", 3, 39.99);       // 119.97
        
        double totalValue = stock.getTotalInventoryValue();
        
        assertEquals(679.82, totalValue, 0.01, "Total inventory value should be correctly calculated");
    }
    
    @Test
    @DisplayName("Test inventory value with empty stock")
    void testGetTotalInventoryValue_EmptyStock() {
        double totalValue = stock.getTotalInventoryValue();
        
        assertEquals(0.0, totalValue, 0.01, "Empty inventory should have zero value");
    }
    
    @Test
    @DisplayName("Test inventory value after stock updates")
    void testGetTotalInventoryValue_AfterUpdates() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 10, 25.99);
        
        double initialValue = stock.getTotalInventoryValue();
        assertEquals(259.90, initialValue, 0.01);
        
        stock.updateStock("TSH001", 20);
        double updatedValue = stock.getTotalInventoryValue();
        assertEquals(519.80, updatedValue, 0.01, "Value should double after doubling quantity");
    }
    
    // ================== TRANSACTION HISTORY TESTS ==================
    
    @Test
    @DisplayName("Test transaction history recording")
    void testTransactionHistory() {
        stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        stock.updateStock("TSH001", 75);
        stock.updateStock("TSH001", 60);
        stock.removeItem("TSH001");
        
        List<StockTransaction> transactions = stock.getTransactionHistory();
        
        assertEquals(4, transactions.size(), "Should have 4 transactions recorded");
        assertEquals("ADD", transactions.get(0).getTransactionType());
        assertEquals("STOCK_IN", transactions.get(1).getTransactionType());
        assertEquals("STOCK_OUT", transactions.get(2).getTransactionType());
        assertEquals("REMOVE", transactions.get(3).getTransactionType());
    }
    
    // ================== EDGE CASES AND BOUNDARY TESTS ==================
    
    @Test
    @DisplayName("Test handling of special characters in item data")
    void testSpecialCharacters() {
        boolean result = stock.addGarmentItem("TSH-001/A", "T-Shirt (Premium)", "Shirts & Tops", 
                                            "M/L", "Blue & White", 50, 25.99);
        
        assertTrue(result, "Should handle special characters in item data");
        
        GarmentItem item = stock.getItem("TSH-001/A");
        assertNotNull(item, "Item with special characters should be retrievable");
    }
    
    @Test
    @DisplayName("Test very large quantities")
    void testLargeQuantities() {
        boolean result = stock.addGarmentItem("TSH001", "T-Shirt", "Shirts", "M", "Blue", 
                                            Integer.MAX_VALUE, 25.99);
        
        assertTrue(result, "Should handle large quantities");
        assertEquals(Integer.MAX_VALUE, stock.getItem("TSH001").getQuantity());
    }
    
    @Test
    @DisplayName("Test very high prices")
    void testHighPrices() {
        boolean result = stock.addGarmentItem("LUX001", "Luxury Item", "Premium", "M", "Gold", 
                                            1, 99999.99);
        
        assertTrue(result, "Should handle high prices");
        assertEquals(99999.99, stock.getItem("LUX001").getPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test concurrent operations simulation")
    void testConcurrentOperations() {
        // Simulate multiple rapid operations
        for (int i = 0; i < 100; i++) {
            stock.addGarmentItem("ITEM" + i, "Item " + i, "Category", "M", "Color", 10, 10.0);
        }
        
        assertEquals(100, stock.getAllItems().size(), "Should handle rapid additions");
        
        // Update all items
        for (int i = 0; i < 100; i++) {
            stock.updateStock("ITEM" + i, 20);
        }
        
        // Verify all updates
        for (int i = 0; i < 100; i++) {
            assertEquals(20, stock.getItem("ITEM" + i).getQuantity(), 
                        "All items should be updated to quantity 20");
        }
    }
    
    // ================== HELPER METHODS ==================
    
    private void setupTestData() {
        stock.addGarmentItem("TSH001", "Cotton T-Shirt", "Shirts", "M", "Blue", 50, 25.99);
        stock.addGarmentItem("TSH002", "Polo T-Shirt", "Shirts", "L", "Blue", 30, 35.99);
        stock.addGarmentItem("JNS001", "Denim Jeans", "Pants", "32", "Blue", 20, 59.99);
        stock.addGarmentItem("DRS001", "Summer Dress", "Dresses", "S", "Red", 15, 39.99);
    }
    
    // ================== PERFORMANCE TESTS ==================
    
    @Test
    @DisplayName("Test search performance with large dataset")
    void testSearchPerformance() {
        // Add 1000 items
        for (int i = 0; i < 1000; i++) {
            stock.addGarmentItem("ITEM" + String.format("%04d", i), 
                               "Item " + i, "Category" + (i % 10), 
                               "Size" + (i % 5), "Color" + (i % 8), 
                               i % 100, 10.0 + (i % 50));
        }
        
        long startTime = System.currentTimeMillis();
        List<GarmentItem> results = stock.searchItems("Category5", "CATEGORY");
        long endTime = System.currentTimeMillis();
        
        assertTrue(results.size() > 0, "Should find items in large dataset");
        assertTrue((endTime - startTime) < 100, "Search should complete within reasonable time");
    }
    
    @Test
    @DisplayName("Test inventory value calculation performance")
    void testInventoryValuePerformance() {
        // Add 1000 items
        for (int i = 0; i < 1000; i++) {
            stock.addGarmentItem("ITEM" + i, "Item " + i, "Category", "M", "Color", 
                               i + 1, 10.0 + i);
        }
        
        long startTime = System.currentTimeMillis();
        double value = stock.getTotalInventoryValue();
        long endTime = System.currentTimeMillis();
        
        assertTrue(value > 0, "Should calculate non-zero value for 1000 items");
        assertTrue((endTime - startTime) < 50, "Value calculation should be fast");
    }
}