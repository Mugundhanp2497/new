import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Integration Test Suite for Stock Screen
 * Tests complete workflows and user scenarios for garment warehouse operations
 */
public class StockIntegrationTest {
    
    private Stock stock;
    
    @BeforeEach
    void setUp() {
        stock = new Stock();
    }
    
    @AfterEach
    void tearDown() {
        stock.clearInventory();
    }
    
    // ================== COMPLETE WORKFLOW TESTS ==================
    
    @Test
    @DisplayName("Test complete garment receiving workflow")
    void testGarmentReceivingWorkflow() {
        // Scenario: New shipment of garments arrives at warehouse
        
        // Step 1: Receive new garment items
        assertTrue(stock.addGarmentItem("TSH001-S-BLU", "Cotton T-Shirt", "Shirts", "S", "Blue", 100, 19.99));
        assertTrue(stock.addGarmentItem("TSH001-M-BLU", "Cotton T-Shirt", "Shirts", "M", "Blue", 150, 19.99));
        assertTrue(stock.addGarmentItem("TSH001-L-BLU", "Cotton T-Shirt", "Shirts", "L", "Blue", 120, 19.99));
        assertTrue(stock.addGarmentItem("TSH001-XL-BLU", "Cotton T-Shirt", "Shirts", "XL", "Blue", 80, 19.99));
        
        // Step 2: Verify inventory count
        assertEquals(4, stock.getAllItems().size(), "Should have 4 different SKUs");
        
        // Step 3: Check total value of received goods
        double expectedValue = (100 + 150 + 120 + 80) * 19.99;
        assertEquals(expectedValue, stock.getTotalInventoryValue(), 0.01, 
                    "Total value should match expected calculation");
        
        // Step 4: Verify transaction history
        List<StockTransaction> transactions = stock.getTransactionHistory();
        assertEquals(4, transactions.size(), "Should have 4 ADD transactions");
        assertTrue(transactions.stream().allMatch(t -> t.getTransactionType().equals("ADD")));
    }
    
    @Test
    @DisplayName("Test order fulfillment workflow")
    void testOrderFulfillmentWorkflow() {
        // Setup initial inventory
        stock.addGarmentItem("TSH001-M-RED", "Cotton T-Shirt", "Shirts", "M", "Red", 50, 19.99);
        stock.addGarmentItem("JNS001-32-BLU", "Denim Jeans", "Pants", "32", "Blue", 30, 59.99);
        stock.addGarmentItem("DRS001-S-FLR", "Summer Dress", "Dresses", "S", "Floral", 20, 39.99);
        
        // Customer order: 15 T-shirts, 10 jeans, 5 dresses
        
        // Fulfill T-shirt order
        GarmentItem tshirt = stock.getItem("TSH001-M-RED");
        assertTrue(stock.updateStock("TSH001-M-RED", tshirt.getQuantity() - 15));
        assertEquals(35, stock.getItem("TSH001-M-RED").getQuantity());
        
        // Fulfill jeans order
        GarmentItem jeans = stock.getItem("JNS001-32-BLU");
        assertTrue(stock.updateStock("JNS001-32-BLU", jeans.getQuantity() - 10));
        assertEquals(20, stock.getItem("JNS001-32-BLU").getQuantity());
        
        // Fulfill dress order
        GarmentItem dress = stock.getItem("DRS001-S-FLR");
        assertTrue(stock.updateStock("DRS001-S-FLR", dress.getQuantity() - 5));
        assertEquals(15, stock.getItem("DRS001-S-FLR").getQuantity());
        
        // Verify remaining inventory value
        double expectedValue = (35 * 19.99) + (20 * 59.99) + (15 * 39.99);
        assertEquals(expectedValue, stock.getTotalInventoryValue(), 0.01);
        
        // Check transaction history shows STOCK_OUT operations
        List<StockTransaction> transactions = stock.getTransactionHistory();
        long stockOutCount = transactions.stream()
                .filter(t -> t.getTransactionType().equals("STOCK_OUT"))
                .count();
        assertEquals(3, stockOutCount, "Should have 3 STOCK_OUT transactions");
    }
    
    @Test
    @DisplayName("Test seasonal inventory management workflow")
    void testSeasonalInventoryManagement() {
        // Scenario: End of summer season, need to clear summer items and prepare for winter
        
        // Add summer inventory
        stock.addGarmentItem("TANK001-S", "Tank Top", "Shirts", "S", "White", 40, 15.99);
        stock.addGarmentItem("TANK001-M", "Tank Top", "Shirts", "M", "White", 35, 15.99);
        stock.addGarmentItem("SHORT001-28", "Summer Shorts", "Shorts", "28", "Khaki", 25, 24.99);
        stock.addGarmentItem("SDRS001-M", "Summer Dress", "Dresses", "M", "Floral", 20, 34.99);
        
        // Add winter inventory
        stock.addGarmentItem("SWTR001-M", "Wool Sweater", "Sweaters", "M", "Navy", 30, 49.99);
        stock.addGarmentItem("COAT001-L", "Winter Coat", "Outerwear", "L", "Black", 15, 129.99);
        
        // Search for summer items to mark down
        List<GarmentItem> summerItems = stock.searchItems("Summer", "NAME");
        summerItems.addAll(stock.searchItems("Tank", "NAME"));
        summerItems.addAll(stock.searchItems("Shorts", "CATEGORY"));
        
        assertTrue(summerItems.size() >= 3, "Should find summer items for clearance");
        
        // Search for winter items to verify stock
        List<GarmentItem> winterItems = stock.searchItems("Winter", "NAME");
        winterItems.addAll(stock.searchItems("Sweaters", "CATEGORY"));
        winterItems.addAll(stock.searchItems("Outerwear", "CATEGORY"));
        
        assertTrue(winterItems.size() >= 2, "Should have winter items in stock");
        
        // Total inventory should include both seasons
        assertEquals(6, stock.getAllItems().size(), "Should have all seasonal items");
    }
    
    @Test
    @DisplayName("Test low stock alerting and reordering workflow")
    void testLowStockAlertingWorkflow() {
        // Setup items with varying stock levels
        stock.addGarmentItem("POP001", "Popular Item", "Shirts", "M", "Blue", 5, 29.99);   // Low stock
        stock.addGarmentItem("REG001", "Regular Item", "Pants", "32", "Black", 50, 39.99); // Normal stock
        stock.addGarmentItem("SLW001", "Slow Moving", "Accessories", "OS", "Brown", 2, 19.99); // Very low
        stock.addGarmentItem("NEW001", "New Item", "Dresses", "L", "Red", 100, 49.99);     // High stock
        
        // Check for low stock items (threshold = 10)
        List<GarmentItem> lowStockItems = stock.getLowStockItems(10);
        assertEquals(2, lowStockItems.size(), "Should identify 2 low stock items");
        
        // Verify specific low stock items
        assertTrue(lowStockItems.stream().anyMatch(item -> item.getItemCode().equals("POP001")));
        assertTrue(lowStockItems.stream().anyMatch(item -> item.getItemCode().equals("SLW001")));
        
        // Simulate reordering for popular item
        assertTrue(stock.updateStock("POP001", 55), "Should successfully restock popular item");
        assertEquals(55, stock.getItem("POP001").getQuantity());
        
        // Check low stock again after restocking
        List<GarmentItem> updatedLowStock = stock.getLowStockItems(10);
        assertEquals(1, updatedLowStock.size(), "Should now have only 1 low stock item");
        assertEquals("SLW001", updatedLowStock.get(0).getItemCode());
    }
    
    @Test
    @DisplayName("Test size and color variant management workflow")
    void testSizeColorVariantManagement() {
        // Add complete size run for a popular T-shirt in multiple colors
        String[] sizes = {"XS", "S", "M", "L", "XL", "XXL"};
        String[] colors = {"Black", "White", "Navy", "Red", "Gray"};
        
        int itemCount = 0;
        for (String color : colors) {
            for (String size : sizes) {
                String itemCode = String.format("TSH100-%s-%s", size, color.substring(0, 3).toUpperCase());
                assertTrue(stock.addGarmentItem(itemCode, "Premium T-Shirt", "Shirts", size, color, 25, 24.99));
                itemCount++;
            }
        }
        
        assertEquals(30, itemCount, "Should have created 30 variants (5 colors × 6 sizes)");
        assertEquals(30, stock.getAllItems().size(), "All variants should be in inventory");
        
        // Search for specific color variants
        List<GarmentItem> blackItems = stock.searchItems("Black", "COLOR");
        assertEquals(6, blackItems.size(), "Should find 6 black items (all sizes)");
        
        // Search for specific size variants
        List<GarmentItem> mediumItems = stock.searchItems("M", "SIZE");
        assertEquals(5, mediumItems.size(), "Should find 5 medium items (all colors)");
        
        // Check total inventory value for all variants
        double expectedTotalValue = 30 * 25 * 24.99;
        assertEquals(expectedTotalValue, stock.getTotalInventoryValue(), 0.01);
        
        // Simulate selling out of a popular size/color combination
        GarmentItem popularItem = stock.getItem("TSH100-M-BLA");
        assertNotNull(popularItem);
        assertTrue(stock.updateStock("TSH100-M-BLA", 0));
        
        // Verify stock level
        assertEquals(0, stock.getItem("TSH100-M-BLA").getQuantity());
        
        // Check if it appears in low stock (threshold = 5)
        List<GarmentItem> lowStock = stock.getLowStockItems(5);
        assertTrue(lowStock.stream().anyMatch(item -> item.getItemCode().equals("TSH100-M-BLA")));
    }
    
    @Test
    @DisplayName("Test return and exchange workflow")
    void testReturnExchangeWorkflow() {
        // Setup initial inventory
        stock.addGarmentItem("DRS001-M-BLU", "Blue Dress", "Dresses", "M", "Blue", 20, 59.99);
        stock.addGarmentItem("DRS001-L-BLU", "Blue Dress", "Dresses", "L", "Blue", 18, 59.99);
        
        // Customer buys medium dress (stock goes down)
        assertTrue(stock.updateStock("DRS001-M-BLU", 19));
        
        // Customer returns medium dress and exchanges for large
        // Return: medium stock goes back up
        assertTrue(stock.updateStock("DRS001-M-BLU", 20));
        // Exchange: large stock goes down
        assertTrue(stock.updateStock("DRS001-L-BLU", 17));
        
        // Verify final quantities
        assertEquals(20, stock.getItem("DRS001-M-BLU").getQuantity());
        assertEquals(17, stock.getItem("DRS001-L-BLU").getQuantity());
        
        // Check transaction history
        List<StockTransaction> transactions = stock.getTransactionHistory();
        assertEquals(5, transactions.size()); // 2 ADD + 3 UPDATE transactions
        
        // Verify transaction types
        long stockInCount = transactions.stream()
                .filter(t -> t.getTransactionType().equals("STOCK_IN"))
                .count();
        long stockOutCount = transactions.stream()
                .filter(t -> t.getTransactionType().equals("STOCK_OUT"))
                .count();
        
        assertTrue(stockInCount >= 1, "Should have at least one STOCK_IN transaction");
        assertTrue(stockOutCount >= 2, "Should have at least two STOCK_OUT transactions");
    }
    
    @Test
    @DisplayName("Test damaged goods and quality control workflow")
    void testDamagedGoodsWorkflow() {
        // Setup inventory
        stock.addGarmentItem("SHT001-L-WHT", "White Shirt", "Shirts", "L", "White", 50, 34.99);
        
        // Discover 3 damaged items during quality check
        int originalQuantity = stock.getItem("SHT001-L-WHT").getQuantity();
        int damagedQuantity = 3;
        int adjustedQuantity = originalQuantity - damagedQuantity;
        
        assertTrue(stock.updateStock("SHT001-L-WHT", adjustedQuantity));
        assertEquals(47, stock.getItem("SHT001-L-WHT").getQuantity());
        
        // Add damaged items to a separate tracking (in real system)
        assertTrue(stock.addGarmentItem("SHT001-L-WHT-DMG", "White Shirt (Damaged)", "Damaged", "L", "White", 3, 0.00));
        
        // Verify damaged items are tracked separately
        GarmentItem damagedItem = stock.getItem("SHT001-L-WHT-DMG");
        assertNotNull(damagedItem);
        assertEquals(3, damagedItem.getQuantity());
        assertEquals(0.00, damagedItem.getPrice(), 0.01);
        
        // Search for damaged items
        List<GarmentItem> damagedItems = stock.searchItems("Damaged", "CATEGORY");
        assertEquals(1, damagedItems.size());
    }
    
    @Test
    @DisplayName("Test bulk operations workflow")
    void testBulkOperationsWorkflow() {
        // Scenario: Bulk price update for end-of-season sale
        
        // Add summer collection
        stock.addGarmentItem("SUM001", "Summer Top", "Shirts", "M", "Yellow", 30, 29.99);
        stock.addGarmentItem("SUM002", "Summer Shorts", "Shorts", "32", "Blue", 25, 34.99);
        stock.addGarmentItem("SUM003", "Summer Hat", "Accessories", "OS", "White", 15, 19.99);
        
        // Bulk search for summer items
        List<GarmentItem> summerItems = stock.searchItems("Summer", "NAME");
        assertEquals(3, summerItems.size(), "Should find all summer items");
        
        // Calculate total value before discount
        double originalValue = stock.getTotalInventoryValue();
        assertTrue(originalValue > 0, "Should have positive inventory value");
        
        // In a real system, bulk price updates would be handled differently
        // Here we simulate by verifying we can access all items that need updating
        for (GarmentItem item : summerItems) {
            assertNotNull(item, "Each summer item should be accessible");
            assertTrue(item.getPrice() > 0, "Each item should have a positive price");
        }
        
        // Verify we can track bulk operations in transaction history
        int initialTransactionCount = stock.getTransactionHistory().size();
        
        // Simulate bulk stock update (clearance quantities)
        for (GarmentItem item : summerItems) {
            int newQuantity = item.getQuantity() - 5; // Sell 5 of each
            if (newQuantity >= 0) {
                stock.updateStock(item.getItemCode(), newQuantity);
            }
        }
        
        // Verify transactions were recorded
        int finalTransactionCount = stock.getTransactionHistory().size();
        assertEquals(initialTransactionCount + 3, finalTransactionCount, 
                    "Should have 3 additional transactions for bulk updates");
    }
    
    @Test
    @DisplayName("Test cross-category search and reporting workflow")
    void testCrossCategorySearchWorkflow() {
        // Setup diverse inventory across categories
        stock.addGarmentItem("BLU001-S", "Blue Shirt", "Shirts", "S", "Blue", 20, 24.99);
        stock.addGarmentItem("BLU002-M", "Blue Jeans", "Pants", "32", "Blue", 15, 54.99);
        stock.addGarmentItem("BLU003-L", "Blue Dress", "Dresses", "L", "Blue", 10, 44.99);
        stock.addGarmentItem("RED001-M", "Red Shirt", "Shirts", "M", "Red", 25, 24.99);
        stock.addGarmentItem("RED002-S", "Red Skirt", "Skirts", "S", "Red", 12, 34.99);
        
        // Cross-category color analysis
        List<GarmentItem> blueItems = stock.searchItems("Blue", "COLOR");
        List<GarmentItem> redItems = stock.searchItems("Red", "COLOR");
        
        assertEquals(3, blueItems.size(), "Should find 3 blue items across categories");
        assertEquals(2, redItems.size(), "Should find 2 red items across categories");
        
        // Category-specific searches
        List<GarmentItem> shirts = stock.searchItems("Shirts", "CATEGORY");
        assertEquals(2, shirts.size(), "Should find 2 shirts");
        
        // Verify cross-category inventory distribution
        assertEquals(5, stock.getAllItems().size(), "Should have all items across categories");
        
        // Calculate value by color
        double blueValue = blueItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        double redValue = redItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        
        assertTrue(blueValue > 0, "Blue items should have positive value");
        assertTrue(redValue > 0, "Red items should have positive value");
        assertEquals(stock.getTotalInventoryValue(), blueValue + redValue, 0.01, 
                    "Total value should equal sum of color values");
    }
}