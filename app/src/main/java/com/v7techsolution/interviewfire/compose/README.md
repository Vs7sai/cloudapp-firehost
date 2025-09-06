# QuestionCard Jetpack Compose Component

A responsive question card component that automatically adjusts text size to fit within a fixed 25% screen height container.

## Features

- **Fixed Height**: Always takes exactly 25% of screen height
- **Auto-Sizing Text**: Automatically adjusts font size based on content length
- **Left Alignment**: Text aligned to left edge with customizable padding
- **No Scrolling**: Text scales instead of scrolling
- **Loading State**: Built-in loading placeholder support

## Usage

### Basic Usage

```kotlin
@Composable
fun MyScreen() {
    QuestionCard(
        question = "Your question text here",
        modifier = Modifier.fillMaxWidth()
    )
}
```

### Advanced Usage

```kotlin
@Composable
fun MyScreen() {
    QuestionCard(
        question = "Your question text here",
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Blue,
        textColor = Color.White,
        padding = 16.dp,
        isLoading = false
    )
}
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `question` | String | Required | The question text to display |
| `modifier` | Modifier | Modifier | Modifier for the container |
| `backgroundColor` | Color | Color(0xFF6200EE) | Background color of the card |
| `textColor` | Color | Color.White | Color of the text |
| `padding` | Dp | 16.dp | Padding around the text |
| `isLoading` | Boolean | false | Shows loading placeholder when true |

## Auto-Sizing Logic

The component automatically adjusts font size based on text characteristics:

- **Short text (≤30 chars)**: Uses maximum font size (24sp)
- **Medium text (31-120 chars)**: Uses medium font size (14-18sp)
- **Long text (>120 chars)**: Uses minimum font size (8sp)
- **Long words**: Automatically reduces font size further

## Dependencies

Add these to your `build.gradle.kts`:

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.activity:activity-compose:1.8.1")

// Accompanist
implementation("com.google.accompanist:accompanist-placeholder-material:0.28.0")
implementation("com.google.accompanist:accompanist-flowlayout:0.28.0")
implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
implementation("com.google.accompanist:accompanist-insets:0.28.0")
```

## Complete Example

See `QuestionScreenExample.kt` for a complete implementation showing:
- Question card with 25% height
- Explanation card with 75% height
- Navigation buttons
- Loading states
- Multiple question examples

## Requirements Met

✅ Blue container takes exactly 25% of screen height  
✅ Text automatically resizes to fit container  
✅ Left-aligned text with padding  
✅ No scrolling allowed  
✅ Uses Accompanist libraries  
✅ Responsive font scaling  
✅ Fixed container height regardless of text length  
