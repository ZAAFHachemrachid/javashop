# Cosmetics Shop Implementation Plan

## Overview
This document outlines the plan to convert the existing computer parts shop into a cosmetics shop with a pink theme. The changes will affect both the visual appearance and the product data structure.

## 1. Theme Updates

### Color Scheme
```xml
<!-- Primary colors -->
<color name="primary">#FF69B4</color>         <!-- Hot Pink -->
<color name="primary_dark">#FF1493</color>    <!-- Deep Pink -->
<color name="primary_light">#FFC0CB</color>   <!-- Pink -->

<!-- Secondary colors -->
<color name="secondary">#FFB6C1</color>       <!-- Light Pink -->
<color name="secondary_dark">#FF69B4</color>  <!-- Hot Pink -->
<color name="secondary_light">#FFF0F5</color> <!-- Lavender Blush -->
```

### Files to Update
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-night/colors.xml`
- `app/src/main/res/values-night/themes.xml`

## 2. Product Categories Structure

### Main Categories
1. Face Products
   - Foundation
   - Concealer
   - Face primer
   - Setting powder/spray
   - Blush
   - Highlighter
   - Bronzer
   - Contour products
   - BB/CC cream

2. Eye Products
   - Eyeshadow palettes
   - Single eyeshadows
   - Eyeliner
   - Mascara
   - Eyebrow products
   - False eyelashes
   - Eye primer

3. Lip Products
   - Lipstick
   - Lip gloss
   - Lip liner
   - Lip balm
   - Lip tint/stain
   - Lip plumper

4. Skin Care
   - Moisturizers
   - Serums
   - Toners
   - Cleansers
   - Face masks
   - Exfoliators
   - Sunscreen
   - Makeup remover

5. Tools & Accessories
   - Makeup brushes
   - Beauty blenders/sponges
   - Eyelash curlers
   - Makeup brush cleaner
   - Cosmetic bags
   - Tweezers

## 3. Implementation Steps

1. **Theme Update**
   - Implement new color scheme
   - Update existing UI elements for cosmetics context
   - Ensure night theme compatibility

2. **Visual Assets**
   - Create new category icons
   - Update placeholder images
   - Modify product display layouts if needed

3. **Data Migration**
   - Update `DataInitializer.java` with new categories
   - Add sample products for each category
   - Maintain existing data structure (no schema changes needed)

4. **Testing**
   - Verify theme appearance
   - Test category navigation
   - Validate product display
   - Check cart and checkout flow
   - Test night mode
   - Verify responsiveness

## 4. Technical Considerations

### Database
- Existing models support the new structure:
  - Category model supports parent/child relationships
  - Product model accommodates cosmetics attributes
  - Cart and Order systems remain unchanged

### UI Components
- Update category card layouts
- Modify product detail view for cosmetics-specific information
- Adjust search and filter functionality for cosmetics attributes

## 5. Timeline

1. Theme Implementation (1-2 days)
2. Category & Product Data Update (2-3 days)
3. Visual Assets Creation (2-3 days)
4. Testing & Refinement (1-2 days)

## 6. Success Metrics

- Successful theme update with consistent pink color scheme
- All categories properly displaying with appropriate icons
- Products properly categorized and displayed
- Smooth navigation between categories
- Functional cart and checkout process
- Proper display in both light and dark modes