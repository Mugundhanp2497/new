import java.util.*;
import java.time.LocalDateTime;

/**
 * Stock Screen for Warehouse Management in Garment Industry
 * Handles inventory management, stock tracking, and garment-specific operations
 */
public class Stock {
    private Map<String, GarmentItem> inventory;
    private List<StockTransaction> transactions;
    
    public Stock() {
        this.inventory = new HashMap<>();
        this.transactions = new ArrayList<>();
    }
    
    /**
     * Add new garment item to stock
     */
    public boolean addGarmentItem(String itemCode, String itemName, String category, 
                                  String size, String color, int quantity, double price) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            return false;
        }
        
        GarmentItem item = new GarmentItem(itemCode, itemName, category, size, color, quantity, price);
        inventory.put(itemCode, item);
        
        // Record transaction
        transactions.add(new StockTransaction("ADD", itemCode, quantity, LocalDateTime.now()));
        return true;
    }
    
    /**
     * Update stock quantity for existing item
     */
    public boolean updateStock(String itemCode, int newQuantity) {
        if (!inventory.containsKey(itemCode) || newQuantity < 0) {
            return false;
        }
        
        GarmentItem item = inventory.get(itemCode);
        int oldQuantity = item.getQuantity();
        item.setQuantity(newQuantity);
        
        // Record transaction
        String transactionType = newQuantity > oldQuantity ? "STOCK_IN" : "STOCK_OUT";
        transactions.add(new StockTransaction(transactionType, itemCode, 
                                            Math.abs(newQuantity - oldQuantity), LocalDateTime.now()));
        return true;
    }
    
    /**
     * Search items by various criteria
     */
    public List<GarmentItem> searchItems(String searchTerm, String searchType) {
        List<GarmentItem> results = new ArrayList<>();
        
        for (GarmentItem item : inventory.values()) {
            boolean matches = false;
            
            switch (searchType.toUpperCase()) {
                case "CODE":
                    matches = item.getItemCode().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case "NAME":
                    matches = item.getItemName().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case "CATEGORY":
                    matches = item.getCategory().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case "COLOR":
                    matches = item.getColor().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case "SIZE":
                    matches = item.getSize().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
            }
            
            if (matches) {
                results.add(item);
            }
        }
        
        return results;
    }
    
    /**
     * Get low stock items (quantity <= threshold)
     */
    public List<GarmentItem> getLowStockItems(int threshold) {
        List<GarmentItem> lowStockItems = new ArrayList<>();
        
        for (GarmentItem item : inventory.values()) {
            if (item.getQuantity() <= threshold) {
                lowStockItems.add(item);
            }
        }
        
        return lowStockItems;
    }
    
    /**
     * Remove item from inventory
     */
    public boolean removeItem(String itemCode) {
        if (!inventory.containsKey(itemCode)) {
            return false;
        }
        
        inventory.remove(itemCode);
        transactions.add(new StockTransaction("REMOVE", itemCode, 0, LocalDateTime.now()));
        return true;
    }
    
    /**
     * Get item by code
     */
    public GarmentItem getItem(String itemCode) {
        return inventory.get(itemCode);
    }
    
    /**
     * Get all items
     */
    public List<GarmentItem> getAllItems() {
        return new ArrayList<>(inventory.values());
    }
    
    /**
     * Get total inventory value
     */
    public double getTotalInventoryValue() {
        double total = 0.0;
        for (GarmentItem item : inventory.values()) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }
    
    /**
     * Get stock transactions history
     */
    public List<StockTransaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Clear all inventory (for testing purposes)
     */
    public void clearInventory() {
        inventory.clear();
        transactions.clear();
    }
}

/**
 * Garment Item class
 */
class GarmentItem {
    private String itemCode;
    private String itemName;
    private String category;
    private String size;
    private String color;
    private int quantity;
    private double price;
    
    public GarmentItem(String itemCode, String itemName, String category, 
                       String size, String color, int quantity, double price) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.category = category;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters and Setters
    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    
    @Override
    public String toString() {
        return String.format("GarmentItem{code='%s', name='%s', category='%s', size='%s', color='%s', quantity=%d, price=%.2f}",
                itemCode, itemName, category, size, color, quantity, price);
    }
}

/**
 * Stock Transaction class for audit trail
 */
class StockTransaction {
    private String transactionType;
    private String itemCode;
    private int quantity;
    private LocalDateTime timestamp;
    
    public StockTransaction(String transactionType, String itemCode, int quantity, LocalDateTime timestamp) {
        this.transactionType = transactionType;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }
    
    // Getters
    public String getTransactionType() { return transactionType; }
    public String getItemCode() { return itemCode; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getTimestamp() { return timestamp; }
}