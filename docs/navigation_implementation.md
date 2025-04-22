# Navigation Implementation Plan

## 1. AndroidManifest.xml Updates
- Add internet permission for image loading:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 2. Navigation Setup

### 2.1 Product Details Fragment
Create new files:
- `app/src/main/res/layout/fragment_product_details.xml`
- `app/src/main/java/com/example/java_shop/fragments/ProductDetailsFragment.java`
- `app/src/main/java/com/example/java_shop/viewmodels/ProductDetailsViewModel.java`

### 2.2 Update Navigation Graph
Add new destinations to `nav_graph.xml`:
```xml
<fragment
    android:id="@+id/productDetailsFragment"
    android:name="com.example.java_shop.fragments.ProductDetailsFragment"
    android:label="Product Details">
    <argument
        android:name="productId"
        app:argType="string" />
</fragment>
```

### 2.3 ViewModel Navigation Methods
Update HomeViewModel with navigation methods:
```java
public void navigateToProduct(Product product) {
    // Navigate using NavController with productId
}

public void navigateToCategory(Category category) {
    // Navigate using NavController with categoryId
}

public void navigateToSearch() {
    // Navigate to search fragment
}
```

## 3. Implementation Order

1. Update AndroidManifest.xml with internet permission
2. Create ProductDetailsFragment layout
3. Implement ProductDetailsFragment and ViewModel
4. Update navigation graph with new destination
5. Implement navigation methods in HomeViewModel
6. Update navigation in MainActivity for proper handling

## 4. Success Criteria
- Internet permission is properly added
- Product details screen displays correctly
- Navigation between screens works smoothly
- Back navigation functions properly
- Product images load correctly

## 5. Next Steps
1. Switch to Code mode
2. Implement changes in the order specified above
3. Test navigation flow
4. Verify image loading works